package com.example.cleanarchitecture.business.data.util


import com.example.cleanarchitecture.business.data.util.GenericErrors.ERROR_UNKNOWN
import com.tadese.business.data.cache.CacheConstant.CACHE_TIMEOUT
import com.tadese.business.data.cache.CacheErrors.CACHE_ERROR_TIMEOUT
import com.tadese.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.tadese.business.data.cache.CacheResult
import com.tadese.business.data.network.ApiResult
import com.tadese.business.data.network.NetworkConstants.NETWORK_TIMEOUT
import com.tadese.business.data.network.NetworkErrors.NETWORK_ERROR_TIMEOUT
import com.tadese.business.data.network.NetworkErrors.NETWORK_ERROR_UNKNOWN
import com.tadese.framework.datasource.network.implementation.AppNetworkServiceImple
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException

/**
 * Reference: https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
 */

suspend fun <T> appApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(NETWORK_TIMEOUT){
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    ApiResult.NetworkError
                }
                is AppNetworkServiceImple.Companion.AppException -> {
                    ApiResult.GenericError(404, throwable.message)
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    //cLog(errorResponse)
                    ApiResult.GenericError(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    //cLog(NETWORK_ERROR_UNKNOWN)
                    ApiResult.GenericError(
                        null,
                        try {
                            throwable.message?.split(":")?.get(1)?.trim()
                        }catch (ex: Exception)
                        {
                            throwable.message
                        }
                    )
                }
            }
        }
    }
}

suspend fun <T> appCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT){
                CacheResult.Success(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {

                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    //cLog(CACHE_ERROR_UNKNOWN)
                    CacheResult.GenericError(CACHE_ERROR_UNKNOWN)
                }
            }
        }
    }
}


private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}























