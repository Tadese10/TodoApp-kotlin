package com.tadese.business.data.network.abstract

import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo

interface TodoNetworkDatasource {

    suspend fun loginUser(username : String): LoginUser?

    suspend fun addTodo(todo : Todo):Todo?

    suspend fun getAllTodoByUserId(userId : String):List<Todo>

    suspend fun getAllTodo(): List<Todo>

    suspend fun addPostComment(comment: Comment): Comment

    suspend fun getAllPost(): List<Post>
}