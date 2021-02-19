package com.tadese.framework.presentation.comment.state

import android.os.Parcelable
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentViewState constructor(
    var Post : Post? = null,
    var AddComment : Comment? = null

): Parcelable, ViewState {

    override fun toString(): String {
        return "CommentViewState(Post=$Post, AddComment=$AddComment)"
    }
}