package com.tadese.business.interactors.common

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.DataState
import com.tadese.business.domain.state.MessageType
import com.tadese.business.domain.state.Response
import com.tadese.business.domain.state.UIComponentType
import com.tadese.business.interactors.authentication.UserLogin
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent
import kotlinx.coroutines.*


class GetSavedUserData(
    private val appCacheDataSource: AppCacheDataSource
) {
   suspend fun get(): LoginUser? {

        val networkResult =
            appApiCall(Dispatchers.IO) {
                appCacheDataSource.getLoggedInUserData()
            }

        val response = object : ApiResponseHandler<LoginUser, LoginUser>(
            response = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: LoginUser): DataState<LoginUser> {

                return DataState.data(
                    response = Response(
                        message = UserLogin.AUTHENTICATION_SUCCESSFUL,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Success()
                    ),
                    data = resultObj,
                    stateEvent = null
                )

            }

        }.getResult()

        return response.data
    }
}