package com.tadese.business.data.cache.implementation

import com.tadese.business.data.cache.abstraction.AppCacheDataSource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.framework.datasource.cache.abstraction.AppDaoService
import javax.inject.Inject

class AppCacheDataSourceImple
@Inject
constructor(
    val appDaoService: AppDaoService
) : AppCacheDataSource{

    override suspend fun addTodo(todo: Todo) = appDaoService.addTodo(todo)

    override suspend fun searchTodo(query: String, filterAndOrder: String, page: Int) = appDaoService.searchTodo(query, page)

    override suspend fun searchTodoById(primaryKey: Int) = appDaoService.searchTodoById(primaryKey)

    override suspend fun getNumTodo() = appDaoService.getNumTodo()

    override suspend fun getNumTodoWithQuery(query: String) = appDaoService.getNumTodoWithQuery(query)

    override suspend fun getAllTodo() = appDaoService.getAllTodo()

    override suspend fun getAllTodoByPage(page: Int) = appDaoService.getAllTodoByPage(page)

    override suspend fun saveLoggedInUserData(data: LoginUser) = appDaoService.saveLoggedInUserData(data)

    override suspend fun getLoggedInUserData() = appDaoService.getLoggedInUserData()

    override suspend fun saveUserTodos(usersTodo: List<Todo>) = appDaoService.saveUserTodos(usersTodo)

    override suspend fun deleteTodos(todos: List<Int>) = appDaoService.deleteTodos(todos)

    override suspend fun savePosts(posts: List<Post>) = appDaoService.savePosts(posts)

    override suspend fun deleteAllTodos(): Int = appDaoService.deleteAllTodos()

}