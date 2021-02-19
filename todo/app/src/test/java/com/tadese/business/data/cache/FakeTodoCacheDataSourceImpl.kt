package com.tadese.business.data.cache

import android.util.Log
import com.tadese.business.data.cache.abstract.TodoCacheDataSource
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.framework.datasource.cache.database.TODO_PAGINATION_PAGE_SIZE
import com.tadese.util.Constants.TAG

class FakeTodoCacheDataSourceImpl
constructor(
    private val todos: HashMap<Int, Todo>,
    private var savedUserdata: LoginUser? = null,
    var throwSQLiteError : Boolean = false
) : TodoCacheDataSource {

    override suspend fun addTodo(todo: Todo): Long {
        if(todo.id == FORCE_NEW_TODO_EXCEPTION){
            throw Exception("Something went wrong inserting the todo.")
        }
        if(todo.id == FORCE_GENERAL_FAILURE){
            return -1 // fail -  Simulating if SQLite failed to insert the new note and -1 is being returned
        }
        todos[todo.id] =  todo
        return 1 // success
    }

    override suspend fun searchTodo(query: String, filterAndOrder: String, page: Int): List<Todo> {
       //Handles general exception while searching for todo
        if(query == FORCE_TODO_SEARCH_GENERAL_EXCEPTION){
            throw Exception(FORCE_TODO_SEARCH_GENERAL_EXCEPTION)
        }
        val results: ArrayList<Todo> = ArrayList()
        for(todo in todos.values){
            if(todo.title.contains(query)){
                results.add(todo)
            }
            else if(todo.userId.contains(query)){
                results.add(todo)
            }
            if(results.size > (page * TODO_PAGINATION_PAGE_SIZE)){
                break
            }
        }

        return results
    }

    override suspend fun searchTodoById(primaryKey: Int): Todo? {
        return todos[primaryKey]
    }

    override suspend fun getNumTodo(): Int {
        return todos.size
    }

    override suspend fun getAllTodo(): List<Todo> {
        return ArrayList(todos.values)
    }

    override suspend fun saveLoggedInUserData(data: LoginUser): Long {
        if(data.id == FORCE_GENERAL_FAILURE){
            return -1 // fail -  Simulating if SQLite failed to insert the new note and -1 is being returned
            savedUserdata = null  //set user's data to null
        }
        savedUserdata = data
        return 1
    }

    override suspend fun getLoggedInUserData(): LoginUser? {
        return savedUserdata
    }

    override suspend fun saveUserTodos(data: List<Todo>): LongArray {
        if(throwSQLiteError){//To simulate SQLite Err
            throw Exception("SQLite Error")
        }
        val response = LongArray(data.size)
        for((index,todo) in data.withIndex()){
            this.todos[todo.id] = todo
            response[index] = 1
        }
        return response
    }

    override suspend fun savePosts(posts: List<Post>): List<Post> {
        TODO("Not yet implemented")
    }

    companion object{
        const val FORCE_NEW_TODO_EXCEPTION = -1
        const val FORCE_GENERAL_FAILURE = -2
        const val FORCE_TODO_SEARCH_GENERAL_EXCEPTION = "FORCE_TODO_SEARCH_GENERAL_EXCEPTION"
    }

}