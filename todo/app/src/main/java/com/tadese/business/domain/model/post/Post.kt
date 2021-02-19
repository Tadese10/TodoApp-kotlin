package com.tadese.business.domain.model.post

import android.os.Parcelable
import com.tadese.business.domain.model.comment.Comment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    val id : Int,
    val userId : Int,
    val title: String,
    val body : String,
    val comments: List<Comment>
) : Parcelable {
    override fun toString(): String {
        return "Post(id=$id, userId=$userId, title='$title', body='$body', comments=$comments)"
    }
}