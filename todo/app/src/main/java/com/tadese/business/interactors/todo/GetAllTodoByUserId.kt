package com.tadese.business.interactors.todo

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.example.cleanarchitecture.business.data.util.appCacheCall
import com.tadese.business.data.cache.abstract.TodoCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.DataState
import com.tadese.business.domain.state.MessageType
import com.tadese.business.domain.state.Response
import com.tadese.business.domain.state.UIComponentType
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllTodoByUserId(
    private val todoNetworkDataSource: TodoNetworkDatasource,
    private val todoCacheDataSource: TodoCacheDataSource
)
{
    suspend fun getAllTodoByUserId(
        stateEvent : TodoStateEvent.GetAllUserTodoEvent
    ) : Flow<DataState<TodoViewState>> = flow{

        val networkResult = appApiCall(Dispatchers.IO){
            todoNetworkDataSource.getAllTodoByUserId(stateEvent.username)
        }

        var handler = object : ApiResponseHandler<TodoViewState, List<Todo>>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: List<Todo>): DataState<TodoViewState> {

                val viewState = TodoViewState(
                    userTodoList = resultObj
                )
                return  DataState.data(
                    response = Response(
                        message = FETCHING_USER_TODOS_LIST_WAS_SUCCESSFUL,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Success()
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )
            }

        }.getResult()

        if(handler.data == null){
            emit(DataState.data(
                response = Response(
                    message = FETCHING_USER_TODOS_LIST_FAILED,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                data = handler.data,
                stateEvent = stateEvent
            ))
        }else {
            handler?.data?.let {
                saveUsersTodoToCachedata(it.userTodoList, stateEvent)//Cache the response
            }

            emit(handler)
        }
    }

    private suspend fun saveUsersTodoToCachedata(userTodoList: List<Todo>, stateEvent: TodoStateEvent.GetAllUserTodoEvent) {
            appCacheCall(Dispatchers.IO){
                todoCacheDataSource.saveUserTodos(userTodoList)
            }
    }

    companion object{
        const val FETCHING_USER_TODOS_LIST_WAS_SUCCESSFUL = "FETCHING_USER_TODOS_LIST_WAS_SUCCESSFUL"
        const val FETCHING_USER_TODOS_LIST_FAILED = "FETCHING_USER_TODOS_LIST_FAILED"
    }
}