package com.tadese.business.domain.model.todo

data class Todo (
    val id: Int,
    val userId : String,
    val title : String,
    val completed: Boolean
) {
    override fun toString(): String {
        return "Todo(id=$id, userId='$userId', title='$title', completed=$completed)"
    }
}