package com.tadese.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tadese.business.domain.model.comment.Comment
import com.tadese.framework.datasource.cache.model.PostEntity.Companion.name

@Entity(tableName = name)
data class PostEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="id")
    val id : Int,

    @ColumnInfo(name="userId")
    val userId : Int,

    @ColumnInfo(name="title")
    val title: String,

    @ColumnInfo(name="body")
    val body : String,

    @ColumnInfo(name="comments")
    val comments: String?

) {
    @Ignore
    val commentList : List<Comment> = convertListOfCommentsToJson(comments = comments)

    companion object{

        const val name = "post"

        fun convertListOfCommentsToString(comments: List<Comment>?) : String{
             return if(comments == null){
                ""
            }else{
                Gson()
                    .toJson(
                        comments,
                        object: TypeToken<List<Comment>>() {}.type
                    )
            }
        }

        fun convertListOfCommentsToJson(comments: String?) : List<Comment>{
            return if(comments.isNullOrEmpty()){
                 ArrayList()
            }else{
                 Gson()
                    .fromJson(
                        comments,
                        object: TypeToken<List<Comment>>() {}.type
                    )
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostEntity

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (comments != other.comments) return false
        if (commentList != other.commentList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + userId
        result = 31 * result + title.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + comments.hashCode()
        result = 31 * result + commentList.hashCode()
        return result
    }

}