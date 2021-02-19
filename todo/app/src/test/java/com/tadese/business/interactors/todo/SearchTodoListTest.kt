package com.tadese.business.interactors.todo

import com.tadese.business.data.cache.FakeTodoCacheDataSourceImpl
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.DataState
import com.tadese.di.DependencyContainer
import com.tadese.framework.datasource.cache.database.TODO_PAGINATION_PAGE_SIZE
import com.tadese.framework.datasource.cache.database.TodoDao
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
class SearchTodoListTest {

    /*
        Test cases
        1. SearchTodoListQuery_Success_ConfirmListRetrieved
        2. SearchTodoListEmptyQuery_Success_ConfirmListRetrieved
        3. SearchTodoListRandomQuery_Success_ConfirmNoListRetrieved
        4. SearchTodoList_Failed_GeneralExceptionConfirmNoListRetrieved
     */

    //System in test
    private val searchTodoList : SearchTodoList

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val todoCacheDataSource: FakeTodoCacheDataSourceImpl
    private val todoNetworkDataSource: TodoNetworkDatasource
    private var loggedInUser : LoginUser? = null

    init {
        dependencyContainer.loadTodoData = true
        dependencyContainer.userId = 1
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        searchTodoList = SearchTodoList(
            todoCacheDataSource = todoCacheDataSource
        )

        //Login User
        runBlocking {
            loggedInUser = todoNetworkDataSource.loginUser(AddTodoTest.RightUsername)
        }

    }

    @Test
    fun SearchTodoListQuery_Success_ConfirmListRetrieved() = runBlocking {

        searchTodoList.searchTodoList(TodoStateEvent.SearchTodoListEvent(
            query = loggedInUser?.id.toString(),
            filterAndOrder = filterAndOrder,
            page = 1
        )).collect(
            object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertTrue(value.data?.userTodoList!!.isNotEmpty()) //Confirm the retrieved list is not empty

                    assertEquals(value.data?.userTodoList, todoCacheDataSource.searchTodo(
                            query = query,
                        filterAndOrder = filterAndOrder,
                        page = 1
                    )) //Confirm that the retrieved list matches the cached data
                }

            }
        )
    }

    @Test
    fun SearchTodoListEmptyQuery_Success_ConfirmListRetrieved() = runBlocking {
        searchTodoList.searchTodoList(TodoStateEvent.SearchTodoListEvent(
            query = empty_query,
            filterAndOrder = filterAndOrder,
            page = 1
        )).collect(
            object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertTrue(value.data?.userTodoList!!.isNotEmpty()) //Confirm the retrieved list is not empty

                    assertEquals(value.data?.userTodoList, todoCacheDataSource.searchTodo(
                        query = query,
                        filterAndOrder = filterAndOrder,
                        page = 1
                    )) //Confirm that the retrieved list matches the cached data
                }

            }
        )
    }

    @Test
    fun SearchTodoListRandomQuery_Success_ConfirmNoListRetrieved() = runBlocking {
        searchTodoList.searchTodoList(TodoStateEvent.SearchTodoListEvent(
            query = random_query,
            filterAndOrder = filterAndOrder,
            page = 1
        )).collect(
            object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertTrue(value.data?.userTodoList!!.isEmpty()) //Confirm the retrieved list is empty

                }

            }
        )
    }

    @Test
    fun SearchTodoList_Failed_GeneralExceptionConfirmNoListRetrieved() = runBlocking {
        searchTodoList.searchTodoList(TodoStateEvent.SearchTodoListEvent(
            query = FakeTodoCacheDataSourceImpl.FORCE_TODO_SEARCH_GENERAL_EXCEPTION,
            filterAndOrder = filterAndOrder,
            page = 1
        )).collect(
            object : FlowCollector<DataState<TodoViewState>>{

                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertTrue(value.data == null) //Confirm the retrieved list is empty

                }

            }
        )
    }

    companion object{
        const val query = ""
        const val empty_query = ""
        const val random_query = "random_query"
        const val filterAndOrder = ""
    }

}