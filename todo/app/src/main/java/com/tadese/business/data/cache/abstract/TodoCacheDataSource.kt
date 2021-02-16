package com.tadese.business.data.cache.abstract

import com.tadese.business.domain.model.todo.Todo

interface TodoCacheDataSource {

    suspend fun addTodo(note : Todo):Long

    suspend fun searchTodo(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Todo>

    suspend fun searchTodoById(primaryKey: String): Todo?

    suspend fun getNumTodo(): Int

    suspend fun getAllTodo(): List<Todo>
}