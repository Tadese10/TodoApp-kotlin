package com.tadese.business.interactors.todo

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.example.cleanarchitecture.business.data.util.appCacheCall
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstraction.AppNetworkDatasource
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


class AddTodo(
    private val todoNetworkDataSource: AppNetworkDatasource,
    private val appCacheDataSource: AppCacheDataSource
)
{
    suspend fun addTodo(
        stateEvent : TodoStateEvent.AddTodoEvent
    ) : Flow<DataState<TodoViewState>> = flow{

        val networkResult = appApiCall(Dispatchers.IO){
            todoNetworkDataSource.addTodo(stateEvent.todo)
        }

        var handler = object : ApiResponseHandler<TodoViewState, Todo?>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: Todo?): DataState<TodoViewState> {

                val viewState = TodoViewState(
                    newTodo = resultObj
                )
                return  DataState.data(
                    response = Response(
                        message = ADD_USER_TODO_SUCCESSFUL,
                        uiComponentType = UIComponentType.Toast(),
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
                    message = ADD_USER_TODO_FAILED,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                data = handler.data,
                stateEvent = stateEvent
            ))
        }else{
            handler.data?.newTodo?.let {
                saveNewTodoToCachedata(it)//Cached the response
            }

            emit(handler)
        }
    }

    private suspend fun saveNewTodoToCachedata(newTodo: Todo?) {
        appCacheCall(Dispatchers.IO){
            appCacheDataSource.addTodo(newTodo!!)
        }
    }

    companion object{
        const val ADD_USER_TODO_SUCCESSFUL = "ADD_USER_TODO_WAS_SUCCESSFUL"
        const val ADD_USER_TODO_FAILED = "ADD_USER_TODO_FAILED"
    }
}