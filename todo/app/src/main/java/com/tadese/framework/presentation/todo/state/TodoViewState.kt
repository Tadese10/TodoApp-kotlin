package com.tadese.framework.presentation.todo.state

import android.os.Parcelable
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TodoViewState(
    var userTodoList: List<Todo>? = null,
    var latestUserTodoList: List<Todo>? = null,
    var newTodo: Todo? = null, // todo that can be created with fab
    var searchQuery: String? = null,
    var searchTodo: Todo? = null,
    var page: Int? = null,
    var isQueryExhausted: Boolean? = null,
    var filter: String? = null,
    var order: String? = null,
    var layoutManagerState: Parcelable? = null,
    var numTodosInCache: Int? = null,
    var isSearching: Boolean  = false
): Parcelable, ViewState {
    override fun toString(): String {
        return "TodoViewState(userTodoList=$userTodoList, newTodo=$newTodo, searchQuery=$searchQuery, page=$page, isQueryExhausted=$isQueryExhausted, filter=$filter, order=$order, layoutManagerState=$layoutManagerState, numTodosInCache=$numTodosInCache)"
    }
}