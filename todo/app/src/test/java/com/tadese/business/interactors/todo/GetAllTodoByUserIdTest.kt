package com.tadese.business.interactors.todo

import com.tadese.business.data.cache.FakeAppCacheDataSourceImpl
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.business.data.network.abstraction.TodoNetworkDatasource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.DataState
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import org.junit.Assert.*
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

@InternalCoroutinesApi
class GetAllTodoByUserIdTest {

    /*
            Use Cases
            1. FetchTodoList_Success_RightUserIdConfirmNetworkCacheData
            2. FetchTodoList_Failed_RightUserIdSQLiteErrorConfirmFailedCacheData
            3. FetchTodoList_Failed_RightUserIdGeneralExceptionConfirmFailedCacheData
            4. FetchTodoList_Failed_WrongUserIdConfirmEmptyNetworkCacheData
            5. FetchTodoList_Failed_EmptyUserIdConfirmFailedCacheData
     */

    //System in test
    private val _getAllTodoByUserId : GetAllTodoByUserId

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val todoCacheDataSource: FakeAppCacheDataSourceImpl
    private val todoNetworkDataSource: TodoNetworkDatasource
    private var loggedInUser : LoginUser? = null

    init {
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        _getAllTodoByUserId = GetAllTodoByUserId(
            todoNetworkDataSource = todoNetworkDataSource,
            appCacheDataSource = todoCacheDataSource
        )

        //Login User
        runBlocking {
            loggedInUser = todoNetworkDataSource.loginUser(RightUsername)
        }
    }

    @Test
    fun FetchTodoList_Success_RightUserIdConfirmNetworkCacheData() = runBlocking {

        _getAllTodoByUserId.getAllTodoByUserId(TodoStateEvent.GetAllUserTodoEvent(loggedInUser!!.id.toString()))
            .collect(object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertEquals(value.data?.userTodoList?.isEmpty(), false)

                    assertEquals(value.stateMessage?.response?.message,
                        GetAllTodoByUserId.FETCHING_USER_TODOS_LIST_WAS_SUCCESSFUL
                    )

                    assertEquals(
                        value.data?.userTodoList, todoCacheDataSource.getAllTodo()
                    )

                }

            })
    }

    @Test
    fun FetchTodoList_Failed_RightUserIdGeneralExceptionConfirmFailedCacheData() = runBlocking {

         _getAllTodoByUserId.getAllTodoByUserId(TodoStateEvent.GetAllUserTodoEvent(
            FakeTodoNetworkDataSourceImpl.FORCE_GENERAL_EXCEPTION
        ))
            .collect(object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertNull(value.data)//Assert that the data is null because of the exception thrown

                    assertEquals(value.stateMessage?.response?.message,
                        GetAllTodoByUserId.FETCHING_USER_TODOS_LIST_FAILED
                    )
                }

            })

        assertEquals(todoCacheDataSource.getAllTodo().isEmpty(), true)

    }

    @Test
    fun FetchTodoList_Failed_RightUserIdSQLiteErrorConfirmFailedCacheData() = runBlocking {
        todoCacheDataSource.throwSQLiteError = true
        _getAllTodoByUserId.getAllTodoByUserId(TodoStateEvent.GetAllUserTodoEvent(loggedInUser!!.id.toString()))
            .collect(object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertEquals(value.data?.userTodoList?.isEmpty(),false) //Confirm that list of todo received

                    assertTrue(todoCacheDataSource.getAllTodo().isEmpty())//Confirm cache todo list is empty
                }

            })
    }

    @Test
    fun FetchTodoList_Failed_WrongUserIdConfirmEmptyNetworkCacheData() = runBlocking {
        _getAllTodoByUserId.getAllTodoByUserId(TodoStateEvent.GetAllUserTodoEvent(Wrong_UserId))
            .collect(object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertEquals(value.data?.userTodoList?.isEmpty(),true) //Confirm that list of todo is empty

                    assertTrue(todoCacheDataSource.getAllTodo().isEmpty()) //Confirm that cache list of todo is empty

                }

            })
    }

    @Test
    fun FetchTodoList_Failed_EmptyUserIdConfirmFailedCacheData() = runBlocking {
        _getAllTodoByUserId.getAllTodoByUserId(TodoStateEvent.GetAllUserTodoEvent(Empty_UserId))
            .collect(object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertEquals(value.data?.userTodoList?.isEmpty(),true) //Confirm that list of todo is empty

                    assertTrue(todoCacheDataSource.getAllTodo().isEmpty())//Confirm that cache list of todo is empty

                }

            })
    }

    companion object{
        const val RightUsername = "Bret"
        const val Empty_UserId = ""
        const val Wrong_UserId = ""
    }

}