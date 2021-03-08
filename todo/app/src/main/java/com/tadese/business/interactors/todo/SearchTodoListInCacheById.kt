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


class SearchTodoListInCacheById(
    private val appCacheDataSource: AppCacheDataSource
) {
    fun searchTodoListById(
        stateEvent: TodoStateEvent.SearchTodoByIdEvent
    ): Flow<DataState<TodoViewState>> = flow {

        val networkResult = appApiCall(Dispatchers.IO) {
            appCacheDataSource.searchTodoById(stateEvent.Id.toInt())
        }

        var handler = object : ApiResponseHandler<TodoViewState, Todo>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: Todo): DataState<TodoViewState> {

                val viewState = TodoViewState(
                    searchTodo = resultObj,
                )
                var message: String? = SEARCH_TODO_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None()
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