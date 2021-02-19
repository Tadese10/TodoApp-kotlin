package com.tadese.business.interactors.authentication

import com.example.cleanarchitecture.business.data.util.safeApiCall
import com.example.cleanarchitecture.business.data.util.safeCacheCall
import com.tadese.business.data.cache.CacheResponseHandler
import com.tadese.business.data.cache.abstract.TodoCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.*
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserLogin(
    private val todoNetworkDataSource: TodoNetworkDatasource,
    private val todoCacheDataSource: TodoCacheDataSource
) {
    suspend fun login(stateEvent: AuthenticationStateEvent.AuthenticateUserEvent): Flow<DataState<LoginUser>?> =
        flow {

            val networkResult = safeApiCall(Dispatchers.IO) {
                todoNetworkDataSource.loginUser(stateEvent.username)
            }

            val response = object : ApiResponseHandler<LoginUser, LoginUser>(
                response = networkResult,
                stateEvent = null
            ) {
                override suspend fun handleSuccess(resultObj: LoginUser): DataState<LoginUser> {
                    return DataState.data(
                        response = Response(
                            message = AUTHENTICATION_SUCCESSFUL,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        data = resultObj,
                        stateEvent = stateEvent
                    )

                }

            }.getResult()

            response.data?.let {
                var savedResponse = saveUserLoggedInData(response.data, stateEvent)

                savedResponse?.data?.let {
                    emit(response)
                }
            }

        }

    private suspend fun saveUserLoggedInData(data: LoginUser?, stateEvent : AuthenticationStateEvent.AuthenticateUserEvent): DataState<Long>? {
        val response  = safeCacheCall(Dispatchers.IO) {
            todoCacheDataSource.saveLoggedInUserData(data!!)
        }

        var handler = object : CacheResponseHandler<Long, Long>(
            response = response,
            stateEvent = null
        ){
            override fun handleSuccess(resultObject: Long): DataState<Long> {
                return if(resultObject > 0) {
                    DataState.data(
                        response = Response(
                            message = AUTHENTICATION_SUCCESSFUL_SAVED_USER_DATA,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        data = resultObject,
                        stateEvent = stateEvent
                    )
                }else{
                    DataState.data(
                        response = Response(
                            message = AUTHENTICATION_FAILED_SAVED_USER_DATA,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ),
                        data = resultObject,
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()

        return (handler)
    }

    companion object {
        const val AUTHENTICATION_SUCCESSFUL = "Authentication was successful."
        const val AUTHENTICATION_SUCCESSFUL_SAVED_USER_DATA = "successful saved user's data."
        const val AUTHENTICATION_FAILED_SAVED_USER_DATA = "Failed to save user's data."
    }
}