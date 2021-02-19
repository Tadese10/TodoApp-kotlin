package com.tadese.framework.presentation.post.state

import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.state.StateEvent

sealed class PostStateEvent :StateEvent{

    class AddNewPostEvent(
        val post: Post
    ) : PostStateEvent() {

        override fun errorInfo(): String {
            return "Error adding POST."
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