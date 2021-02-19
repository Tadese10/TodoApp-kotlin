package com.tadese.framework.presentation.post.state

import android.os.Parcelable
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PostViewState constructor(
    var Posts : List<Post> = ArrayList(),
    var newComment: Todo? = null, // comment that can be created with fab
    var page: Int? = null,
    var layoutManagerState: Parcelable? = null,
): Parcelable, ViewState {

    override fun toString(): String {
        return "PostViewState(Posts=$Posts, newComment=$newComment, page=$page, layoutManagerState=$layoutManagerState)"
    }
}