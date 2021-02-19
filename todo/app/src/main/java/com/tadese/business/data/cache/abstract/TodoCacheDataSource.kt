package com.tadese.business.data.cache.abstract

import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo

interface TodoCacheDataSource {

    suspend fun addTodo(todo : Todo):Long

    suspend fun searchTodo(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Todo>

    suspend fun searchTodoById(primaryKey: Int): Todo?

    suspend fun getNumTodo(): Int

    suspend fun getAllTodo(): List<Todo>

    suspend fun saveLoggedInUserData(data: LoginUser): Long

    suspend fun getLoggedInUserData(): LoginUser?

    suspend fun saveUserTodos(usersTodo: List<Todo>):LongArray

    suspend fun savePosts(posts: List<Post>): List<Post>
}