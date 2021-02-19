package com.tadese.framework.presentation.post.state

import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.state.StateEvent

sealed class PostStateEvent :StateEvent{

    class AddNewPostEvent(
        val comment: Comment
    ) : PostStateEvent() {

        override fun errorInfo(): String {
            return "Error adding TODO."
        }

        override fun eventName(): String {
            return "AddNewPostEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }

    class GetAllPostEvent(
    ) : PostStateEvent() {

        override fun errorInfo(): String {
            return "Error adding TODO."
        }

        override fun eventName(): String {
            return "AddNewPostEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }
}