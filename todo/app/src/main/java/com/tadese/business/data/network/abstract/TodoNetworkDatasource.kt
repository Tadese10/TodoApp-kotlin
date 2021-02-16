package com.tadese.business.data.network.abstract

import com.tadese.business.domain.model.todo.Todo

interface TodoNetworkDatasource {

    suspend fun addTodo(todo : Todo):Long

    suspend fun getAllTodoByUserId(userId : String):List<Todo>

    suspend fun getAllTodo(): List<Todo>
}