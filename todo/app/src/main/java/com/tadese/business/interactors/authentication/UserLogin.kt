package com.tadese.business.interactors.authentication

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.example.cleanarchitecture.business.data.util.appCacheCall
import com.tadese.business.data.cache.CacheResponseHandler
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstraction.AppNetworkDatasource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.*
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent
import com.tadese.framework.presentation.authentication.state.AuthenticationViewState
import com.tadese.framework.presentation.comment.state.CommentViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserLogin(
    private val todoNetworkDataSource: AppNetworkDatasource,
    private val appCacheDataSource: AppCacheDataSource
) {
    fun login(stateEvent: AuthenticationStateEvent.AuthenticateUserEvent): Flow<DataState<AuthenticationViewState>?> =
        flow {

            val networkResult = appApiCall(Dispatchers.IO) {
                todoNetworkDataSource.loginUser(stateEvent.username)
            }

            val response = object : ApiResponseHandler<AuthenticationViewState, LoginUser>(
                response = networkResult,
                stateEvent = null
            ) {
                override suspend fun handleSuccess(resultObj: LoginUser): DataState<AuthenticationViewState> {
                    val data = AuthenticationViewState(
                        userLogin = resultObj
                    )
                    return DataState.data(
                        response = Response(
                            message = AUTHENTICATION_SUCCESSFUL,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        data = data,
                        stateEvent = stateEvent
                    )

                }

            }.getResult()

            response.data?.let {
                var savedResponse = saveUserLoggedInData(response.data, stateEvent)
            }

            emit(response)
        }

    private suspend fun saveUserLoggedInData(data: AuthenticationViewState?, stateEvent : AuthenticationStateEvent.AuthenticateUserEvent): DataState<Long>? {
        val response  = appCacheCall(Dispatchers.IO) {
            appCacheDataSource.saveLoggedInUserData(data?.userLogin!!)
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