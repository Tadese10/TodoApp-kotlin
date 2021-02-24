package com.tadese.business.interactors.authentication

import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl.Companion.FORCE_LOGIN_GENERAL_EXCEPTION
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl.Companion.SOMETHING_WENT_WRONG
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl.Companion.USERNAME_EMPTY
import com.tadese.business.data.network.NetworkErrors.NETWORK_DATA_NULL
import com.tadese.business.data.network.NetworkErrors.NETWORK_ERROR_UNKNOWN
import com.tadese.business.data.network.abstraction.AppNetworkDatasource
import com.tadese.business.domain.state.DataState
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent
import com.tadese.framework.presentation.authentication.state.AuthenticationViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions.*


@InternalCoroutinesApi
class AuthenticationTest {
    /*
            Test Cases
            1. UserAuthenticate_Success_RightUsername
            2. UserAuthenticate_Failed_WrongUsername
            3. UserAuthenticate_Failed_Force_General_Exception
            4. UserAuthenticate_Failed_Empty_Username
     */

    //System in test
    private val userLogin : UserLogin

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val appCacheDataSource: AppCacheDataSource
    private val todoNetworkDataSource: AppNetworkDatasource

    init {
        dependencyContainer.build()
        appCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        userLogin = UserLogin(
            todoNetworkDataSource = todoNetworkDataSource,
            appCacheDataSource = appCacheDataSource
        )
    }

    @Test
    fun UserAuthenticate_Success_RightUsername() = runBlocking {
        userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(RightUsername))
            .collect(object : FlowCollector<DataState<AuthenticationViewState>?>{

                override suspend fun emit(value: DataState<AuthenticationViewState>?) {
                    assertEquals(
                        value?.stateMessage?.response?.message,
                        UserLogin.AUTHENTICATION_SUCCESSFUL
                    )

                    //Confirm if the user's data was cached
                    assertEquals(appCacheDataSource.getLoggedInUserData(),value!!.data?.userLogin)
                }

            })
    }

    @Test
    fun UserAuthenticate_Failed_WrongUsername() = runBlocking {
       userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(Wrong_Username))
            .collect(object : FlowCollector<DataState<AuthenticationViewState>?>{

                override suspend fun emit(value: DataState<AuthenticationViewState>?) {
                    assert(
                        value?.stateMessage?.response?.message!!.contains(NETWORK_DATA_NULL)
                    )
                }

            })

        //Confirm if the user's data wasn't cached
        assertNull(appCacheDataSource.getLoggedInUserData())
    }

    @Test
    fun UserAuthenticate_Failed_ForceGeneralException() = runBlocking {
        userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(FORCE_LOGIN_GENERAL_EXCEPTION))
            .collect(object : FlowCollector<DataState<AuthenticationViewState>?>{

                override suspend fun emit(value: DataState<AuthenticationViewState>?) {
                    assert(
                        value?.stateMessage?.response?.message!!.contains(SOMETHING_WENT_WRONG)
                    )
                }

            })

        //Confirm if the user's data wasn't cached
        assertNull(appCacheDataSource.getLoggedInUserData())
    }

    @Test
    fun UserAuthenticate_Failed_EmptyUsername() = runBlocking {
        userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(Empty_Username))
            .collect(object : FlowCollector<DataState<AuthenticationViewState>?>{

                override suspend fun emit(value: DataState<AuthenticationViewState>?) {
                    assert(
                        value?.stateMessage?.response?.message!!.contains(USERNAME_EMPTY)
                    )
                }

            })

        //Confirm if the user's data wasn't cached
        assertNull(appCacheDataSource.getLoggedInUserData())
    }


    companion object{
        const val RightUsername = "Bret"
        const val Empty_Username = ""
        const val Wrong_Username = "Wrong Username"
    }
}