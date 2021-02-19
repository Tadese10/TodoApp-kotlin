package com.tadese.business.interactors.authentication

import com.tadese.business.data.cache.abstract.TodoCacheDataSource
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl.Companion.FORCE_LOGIN_GENERAL_EXCEPTION
import com.tadese.business.data.network.NetworkErrors.NETWORK_DATA_NULL
import com.tadese.business.data.network.NetworkErrors.NETWORK_ERROR_UNKNOWN
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.DataState
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent
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
    private val todoCacheDataSource: TodoCacheDataSource
    private val todoNetworkDataSource: TodoNetworkDatasource

    init {
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        userLogin = UserLogin(
            todoNetworkDataSource = todoNetworkDataSource,
            todoCacheDataSource = todoCacheDataSource
        )
    }

    @Test
    fun UserAuthenticate_Success_RightUsername() = runBlocking {
        userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(RightUsername))
            .collect(object : FlowCollector<DataState<LoginUser>?>{

                override suspend fun emit(value: DataState<LoginUser>?) {
                    assertEquals(
                        value?.stateMessage?.response?.message,
                        UserLogin.AUTHENTICATION_SUCCESSFUL
                    )

                    //Confirm if the user's data was cached
                    assertEquals(todoCacheDataSource.getLoggedInUserData(),value!!.data)
                }

            })
    }

    @Test
    fun UserAuthenticate_Failed_WrongUsername() = runBlocking {
       userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(Wrong_Username))
            .collect(object : FlowCollector<DataState<LoginUser>?>{

                override suspend fun emit(value: DataState<LoginUser>?) {
                    assert(
                        value?.stateMessage?.response?.message!!.contains(NETWORK_DATA_NULL)
                    )
                }

            })

        //Confirm if the user's data wasn't cached
        assertNull(todoCacheDataSource.getLoggedInUserData())
    }

    @Test
    fun UserAuthenticate_Failed_ForceGeneralException() = runBlocking {
        userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(FORCE_LOGIN_GENERAL_EXCEPTION))
            .collect(object : FlowCollector<DataState<LoginUser>?>{

                override suspend fun emit(value: DataState<LoginUser>?) {
                    assert(
                        value?.stateMessage?.response?.message!!.contains(NETWORK_ERROR_UNKNOWN)
                    )
                }

            })

        //Confirm if the user's data wasn't cached
        assertNull(todoCacheDataSource.getLoggedInUserData())
    }

    @Test
    fun UserAuthenticate_Failed_EmptyUsername() = runBlocking {
        userLogin.login(AuthenticationStateEvent.AuthenticateUserEvent(Empty_Username))
            .collect(object : FlowCollector<DataState<LoginUser>?>{

                override suspend fun emit(value: DataState<LoginUser>?) {
                    assert(
                        value?.stateMessage?.response?.message!!.contains(NETWORK_ERROR_UNKNOWN)
                    )
                }

            })

        //Confirm if the user's data wasn't cached
        assertNull(todoCacheDataSource.getLoggedInUserData())
    }


    companion object{
        const val RightUsername = "Bret"
        const val Empty_Username = ""
        const val Wrong_Username = "Wrong Username"
    }
}