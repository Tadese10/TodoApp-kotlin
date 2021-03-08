package com.tadese.framework.presentation.todo.state

import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.StateEvent

sealed class TodoStateEvent : StateEvent {

    class AddTodoEvent(
        val todo: Todo
    ) : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error adding TODO."
        }

        override fun eventName(): String {
            return "AddTodoEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class GetAllUserTodoEvent(
        val username: String
    ) : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error fetching TODO list."
        }

        override fun eventName(): String {
            return "GetAllUserTodoEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

    class GetAllUserTodoInCacheEvent(
        val page: Int
    ) : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error fetching TODO list."
        }

        override fun eventName(): String {
            return "GetAllUserTodoInCacheEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class GetAllUserTodoNumInCacheEvent() : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error fetching TODO list."
        }

        override fun eventName(): String {
            return "GetAllUserTodoNumInCacheEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

    class DeleteAllTodoUserInCacheEvent() : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error fetching TODO list."
        }

        override fun eventName(): String {
            return "DeleteAllTodoUserInCacheEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }

    class GetAllUserTodoNumInCacheWithQueryEvent(
        val query: String
    ) : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error fetching TODO list."
        }

        override fun eventName(): String {
            return "GetAllUserTodoNumInCacheEvent"
        }

        override fun shouldDisplayProgressBar() = false
    }


    class SearchTodoListEvent(
        val query : String,
        val filterAndOrder: String,
        val page: Int
    ) : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error fetching TODO list."
        }

        override fun eventName(): String {
            return "SearchTodoListEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class SearchTodoByIdEvent(
        val Id: String
    ) : TodoStateEvent() {

        override fun errorInfo(): String {
            return "Error fetching TODO."
        }

        override fun eventName(): String {
            return "SearchTodoByIdEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

//    class GetTodoNumEvent(
//    ) : TodoStateEvent() {
//
//        override fun errorInfo(): String {
//            return "Error fetching TODO."
//        }
//
//        override fun eventName(): String {
//            return "GetTodoNumEvent"
//        }
//
//        override fun shouldDisplayProgressBar() = true
//    }

//    class GetAllTodoEvent(
//    ) : TodoStateEvent() {
//
//        override fun errorInfo(): String {
//            return "Error fetching TODO list."
//        }
//
//        override fun eventName(): String {
//            return "GetAllTodoEvent"
//        }
//
//        override fun shouldDisplayProgressBar() = true
//    }


}
