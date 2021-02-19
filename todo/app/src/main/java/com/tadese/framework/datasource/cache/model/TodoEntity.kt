package com.tadese.framework.datasource.cache.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tadese.framework.datasource.cache.model.TodoEntity.Companion.name
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = name)
data class TodoEntity constructor(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name= "id")
    val id: Int,

    @ColumnInfo(name= "userId")
    val userId : String,

    @ColumnInfo(name= "title")
    val title : String,

    @ColumnInfo(name= "completed")
    val completed: Boolean
) : Parcelable{
    companion object {
        const val name = "todo"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TodoEntity

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