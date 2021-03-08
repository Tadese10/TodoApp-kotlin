package com.tadese.framework.presentation.comment.state

import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.state.StateEvent

sealed class CommentStateEvent :StateEvent {

    class AddCommentStateEvent(
        val comment: Comment
    ) : CommentStateEvent(){

        override fun errorInfo(): String {
            return "Error adding Comment."
        }

        override fun eventName(): String {
            return "AddCommentStateEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }

    class GetPostCommentsStateEvent(
        val post: Post
    ) : CommentStateEvent(){

        override fun errorInfo(): String {
            return "Error fetching Post Comment(s)."
        }

        override fun eventName(): String {
            return "GetPostCommentsStateEvent"
        }

        override fun shouldDisplayProgressBar() = true

    }
}