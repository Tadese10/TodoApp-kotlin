package com.tadese.business.interactors.todo

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.domain.state.DataState
import com.tadese.business.domain.state.MessageType
import com.tadese.business.domain.state.Response
import com.tadese.business.domain.state.UIComponentType
import com.tadese.framework.presentation.todo.state.TodoStateEvent
import com.tadese.framework.presentation.todo.state.TodoViewState
import com.tadese.util.printLogD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DeleteAllTodoUserInCache(
    private val appCacheDataSource: AppCacheDataSource
) {
    fun delete(
        stateEvent: TodoStateEvent.DeleteAllTodoUserInCacheEvent
    ): Flow<DataState<TodoViewState>> = flow {

        val networkResult = appApiCall(Dispatchers.IO) {
            appCacheDataSource.deleteAllTodos()
        }

        var handler = object : ApiResponseHandler<TodoViewState, Int>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: Int): DataState<TodoViewState> {

                val viewState = TodoViewState(
                    numTodosInCache = resultObj,
                )
                var message: String? = DELETE_ALL_TODO_IN_CACHE_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None()
                if (resultObj < 0) {
                    message = DELETE_ALL_TODO_IN_CACHE_FAIL
                    uiComponentType = UIComponentType.None()
                }
                return DataState.data(
                    response = Response(
                        message = message,
                        uiComponentType = uiComponentType,
                        messageType = MessageType.Success()
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )

            }

        }.getResult()

        printLogD(handler.javaClass.name, handler.toString())
        emit(handler)

    }


    companion object {
        const val DELETE_ALL_TODO_IN_CACHE_SUCCESS = "Successfully deleted todos"
        const val DELETE_ALL_TODO_IN_CACHE_FAIL = "Failed to delete todos"
    }
}