package com.tadese.business.domain.model.comment

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment (
    @Expose
    @SerializedName("id")
    val Id : Int? = null,
    @Expose
    @SerializedName("postId")
    val PostID : Int,
    val Name: String? =null,
    val body: String,
    val Email : String
) : Parcelable{
    override fun toString(): String {
        return "Comment(Id=$Id, PostID=$PostID, Name='$Name', body='$body', Email='$Email')"
    }
}
