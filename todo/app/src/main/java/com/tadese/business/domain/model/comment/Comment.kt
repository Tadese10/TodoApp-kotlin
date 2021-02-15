package com.tadese.business.domain.model.comment

data class Comment (
    val Id : Int,
    val PostID : Int,
    val Name: String,
    val body: String,
    val Email : String
) {
    override fun toString(): String {
        return "Comment(Id=$Id, PostID=$PostID, Name='$Name', body='$body', Email='$Email')"
    }
}
