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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SearchTodoListInCache(
    private val appCacheDataSource: AppCacheDataSource
) {
    fun searchTodoList(
        stateEvent: TodoStateEvent.SearchTodoListEvent
    ): Flow<DataState<TodoViewState>> = flow {

        val networkResult = appApiCall(Dispatchers.IO) {
            appCacheDataSource.searchTodo(
                stateEvent.query,
                stateEvent.filterAndOrder,
                stateEvent.page
            )
        }

        var handler = object : ApiResponseHandler<TodoViewState, List<Todo>>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: List<Todo>): DataState<TodoViewState> {

                val viewState = TodoViewState(
                    userTodoList = resultObj,
                )
                var message: String? = SEARCH_TODO_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None()
                if (resultObj.isEmpty()) {
                    message = SEARCH_TODO_NO_MATCHING_RESULTS
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
        const val SEARCH_TODO_SUCCESS = "Successfully found todo"
        const val SEARCH_TODO_NO_MATCHING_RESULTS = "Todo not found"
    }
}