package com.tadese.framework.presentation.todo.state

sealed class TodoListToolbarState {

    class MultiSelectionState: TodoListToolbarState(){

        override fun toString(): String {
            return "MultiSelectionState"
        }
    }

    class SearchViewState: TodoListToolbarState(){

        override fun toString(): String {
            return "SearchViewState"
        }
    }
}