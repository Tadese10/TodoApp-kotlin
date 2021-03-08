package com.tadese.business.domain.model.todo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Todo (
    val id: Int = 0,
    val userId : String,
    val title : String,
    val completed: Boolean
) : Parcelable {
    override fun toString(): String {
        return "Todo(id=$id, userId='$userId', title='$title', completed=$completed)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Todo

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (title != other.title) return false
        if (completed != other.completed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + userId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + completed.hashCode()
        return result
    }


}