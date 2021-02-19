package com.tadese.framework.datasource.cache.abstraction

import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo

interface TodoDaoService {
    //region Todo Cache Service
    suspend fun addTodo(todo: Todo): Long

    suspend fun searchTodo(
        query: String,
        page: Int
    ): List<Todo>

    suspend fun searchTodoById(primaryKey: Int): Todo?

    suspend fun getNumTodo(): Int

    suspend fun getAllTodo(): List<Todo>

    suspend fun saveUserTodos(usersTodo: List<Todo>): LongArray

    //endregion

    //region User Cache Service

    suspend fun saveLoggedInUserData(data: LoginUser): Long

    suspend fun getLoggedInUserData(): LoginUser?

    //endregion

    //region Post Cache Service
    suspend fun addPost(post: Post): Long

    suspend fun searchPost(
        query: String,
        page : Int
    ): List<Todo>

    suspend fun searchPostById(primaryKey: Int): Post?

    suspend fun getNumPost(): Int

    suspend fun getAllPost(): List<Post>

    suspend fun savePosts(posts: List<Post>): LongArray
    //endregion
}