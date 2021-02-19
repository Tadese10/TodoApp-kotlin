package com.tadese.business.interactors.todo

import com.tadese.business.data.cache.FakeTodoCacheDataSourceImpl
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl.Companion.FORCE_ADD_TODO_GENERAL_EXCEPTION
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl.Companion.SQLiteError
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl.Companion.WrongUserId
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.DataState
import com.tadese.business.interactors.todo.AddTodo.Companion.ADD_USER_TODO_FAILED
import com.tadese.business.interactors.todo.AddTodo.Companion.ADD_USER_TODO_SUCCESSFUL
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

@InternalCoroutinesApi
class AddTodoTest {

    /*
        Use Cases
        1. AddUserTodo_Success_ConfirmCacheDataUpdated
        2. AddUserTodo_Failed_GeneralNetworkExceptionExceptionConfirmNotInCache
        3. AddUserTodo_Failed_WrongUserIdConfirmNotInCache
        4. AddUserTodo_Failed_SQLitExceptionConformNotInCache
     */

    //System in test
    private val addTodo : AddTodo

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val todoCacheDataSource: FakeTodoCacheDataSourceImpl
    private val todoNetworkDataSource: TodoNetworkDatasource
    private var loggedInUser : LoginUser? = null

    init {
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        addTodo = AddTodo(
            todoNetworkDataSource = todoNetworkDataSource,
            todoCacheDataSource = todoCacheDataSource
        )

        //Login User
        CoroutineScope(Dispatchers.IO).launch {
            loggedInUser = todoNetworkDataSource.loginUser(RightUsername)
        }
    }


    @Test
    fun AddUserTodo_Success_ConfirmCacheDataUpdated() = runBlocking {

        todoNetworkDataSource.getAllTodoByUserId(loggedInUser?.id.toString())

        var todoData = Todo(
            userId = loggedInUser?.id.toString(),
            title = UUID.randomUUID().toString(),
            completed = false,
        )
        addTodo.addTodo(TodoStateEvent.AddTodoEvent(todoData)).collect(
            object : FlowCollector<DataState<TodoViewState>>{
                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertNotNull(value.data?.newTodo) //Confirm that the new todo returned

                    assertEquals(value?.stateMessage?.response?.message,
                        ADD_USER_TODO_SUCCESSFUL
                    )//Confirm the success message

                    assertNotNull(todoCacheDataSource.searchTodoById(value.data?.newTodo?.id!!)) //Confirm that the new todo was added to the cached data

                }

            }
        )
    }

    @Test
    fun AddUserTodo_Failed_GeneralNetworkExceptionExceptionConfirmNotInCache() = runBlocking {
        var todoData = Todo(
            userId = FORCE_ADD_TODO_GENERAL_EXCEPTION,
            title = UUID.randomUUID().toString(),
            completed = false,
            id = todoCacheDataSource.getNumTodo() + 1 //Get total todo and increment it
        )
        addTodo.addTodo(TodoStateEvent.AddTodoEvent(todoData)).collect(
            object : FlowCollector<DataState<TodoViewState>>{
                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertNull(value.data)//Confirm that the response data is null

                    assertEquals(value?.stateMessage?.response?.message,
                        ADD_USER_TODO_FAILED
                    )//Confirm the failed response message

                    assertNull(todoCacheDataSource.searchTodoById(todoData.id)) //Confirm that the new todo wasn't added to the cached data

                }

            }
        )
    }

    @Test
    fun AddUserTodo_Failed_WrongUserIdConfirmNotInCache() = runBlocking {
        var todoData = Todo(
            userId = WrongUserId,
            title = UUID.randomUUID().toString(),
            completed = false,
            id = todoCacheDataSource.getNumTodo() + 1 //Get total todo and increment it
        )
        addTodo.addTodo(TodoStateEvent.AddTodoEvent(todoData)).collect(
            object : FlowCollector<DataState<TodoViewState>>{
                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertNull(value.data)//Confirm that the response data is null

                    assertEquals(value?.stateMessage?.response?.message,
                        ADD_USER_TODO_FAILED
                    )//Confirm the failed response message

                    assertNull(todoCacheDataSource.searchTodoById(todoData.id)) //Confirm that the new todo wasn't added to the cached data

                }

            }
        )
    }

    @Test
     fun AddUserTodo_Failed_SQLitExceptionConformNotInCache() = runBlocking {
        var todoData = Todo(
            userId = SQLiteError,
            title = UUID.randomUUID().toString(),
            completed = false,
            id = todoCacheDataSource.getNumTodo() + 1 //Get total todo and increment it
        )
        addTodo.addTodo(TodoStateEvent.AddTodoEvent(todoData)).collect(
            object : FlowCollector<DataState<TodoViewState>>{
                override suspend fun emit(value: DataState<TodoViewState>) {

                    assertNull(value.data)//Confirm that the response data is null

                    assertEquals(value?.stateMessage?.response?.message,
                        ADD_USER_TODO_FAILED
                    )//Confirm the failed response message

                    assertNull(todoCacheDataSource.searchTodoById(todoData.id)) //Confirm that the new todo wasn't added to the cached data

                }

            }
        )
    }

    companion object{
        const val RightUsername = "Bret"
    }

}