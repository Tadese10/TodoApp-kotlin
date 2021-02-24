package com.tadese.business.interactors.todo

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.domain.model.todo.Todo
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


class GetAllTodoListInCache(
    private val appCacheDataSource: AppCacheDataSource
) {
    fun getAll(
        stateEvent: TodoStateEvent.GetAllUserTodoInCacheEvent
    ): Flow<DataState<TodoViewState>> = flow {

        val networkResult = appApiCall(Dispatchers.IO) {
            appCacheDataSource.getAllTodoByPage(stateEvent.page)
        }

        var handler = object : ApiResponseHandler<TodoViewState, List<Todo>>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: List<Todo>): DataState<TodoViewState> {

                printLogD("Cache Response", resultObj.toString())
                val viewState = TodoViewState(
                    userTodoList = resultObj
                )
                var message: String? = GET_ALL_TODO_LIST_IN_CACHE_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None()
                if (resultObj.isEmpty()) {
                    message = GET_TODO_LIST_IN_CACHE_SUCCESS_WITH_EMPTY_LIST
                    uiComponentType = UIComponentType.Toast()
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

        emit(handler)

    }


    companion object {
        const val GET_ALL_TODO_LIST_IN_CACHE_SUCCESS = "Successfully Fetched Todo List In Cache"
        const val GET_TODO_LIST_IN_CACHE_SUCCESS_WITH_EMPTY_LIST = "Empty List found"
    }
}