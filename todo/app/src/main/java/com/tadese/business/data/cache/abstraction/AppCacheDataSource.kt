package com.tadese.business.data.cache.abstraction

import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo

interface AppCacheDataSource {

   suspend fun addTodo(todo: Todo):Long

    suspend fun searchTodo(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Todo>

    suspend fun searchTodoById(primaryKey: Int): Todo?

    suspend fun getNumTodo(): Int

    suspend fun getNumTodoWithQuery(query: String): Int

    suspend fun getAllTodo(): List<Todo>

    suspend fun getAllTodoByPage(page: Int): List<Todo>

    suspend fun saveLoggedInUserData(data: LoginUser): Long

    suspend fun getLoggedInUserData(): LoginUser?

    suspend fun saveUserTodos(usersTodo: List<Todo>):LongArray

    suspend fun deleteTodos(todos: List<Int>): Int

    suspend fun deleteAllTodos(): Int

    suspend fun savePosts(posts: List<Post>): LongArray
}