package com.tadese.business.data.network

import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo

class FakeTodoNetworkDataSourceImpl
constructor(
    private val todos: HashMap<Int, Todo>,
    private val posts: HashMap<Int, Post>,
    private val users: HashMap<String, LoginUser>,
    var throwPostGeneralError: Boolean = false
) : TodoNetworkDatasource {

    override suspend fun loginUser(username: String): LoginUser? {
        if(username.isNullOrEmpty()){//Handles null or empty username
            throw Exception(USERNAME_EMPTY)
        }
        if(username == FORCE_LOGIN_GENERAL_EXCEPTION){//Handles general exception
            throw Exception(SOMETHING_WENT_WRONG)
        }
        return if(!users.containsKey(username)){//Handles if the network database doesn't contains key username
            null
        } else{
            users[username] //Success
        }
    }

    override suspend fun addTodo(todo: Todo): Todo? {
        if(todo.userId == SQLiteError){//Handles general exception
            throw Exception(SQLiteError)
        }
        if(todo.userId == WrongUserId){//Handles wrong userId
            return null
        }
        if(todo.userId == FORCE_ADD_TODO_GENERAL_EXCEPTION){//Handles general exception
            throw Exception(FORCE_ADD_TODO_GENERAL_EXCEPTION)
        }
        todos[todo.id] = todo//Success
        return todo
    }

    override suspend fun getAllTodoByUserId(userId: String): List<Todo> {
        if(userId == FORCE_GENERAL_EXCEPTION){//Force general exception while fetching TODO List
            throw Exception(FORCE_GENERAL_EXCEPTION)
        }
        return todos.values.filter { it.userId == userId }.toList() //Success
    }

    override suspend fun getAllTodo(): List<Todo> {
        return ArrayList(todos.values)
    }

    override suspend fun addPostComment(comment: Comment): Comment {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPost(): List<Post> {
        if(throwPostGeneralError){
            throw Exception(POST_GENERAL_ERROR)
        }
       return ArrayList(posts.values)
    }

    companion object{
        const val FORCE_LOGIN_GENERAL_EXCEPTION = "FORCE_LOGIN_GENERAL_EXCEPTION"
        const val SOMETHING_WENT_WRONG = "Something went wrong while authenticating user."
        const val FORCE_ADD_TODO_GENERAL_EXCEPTION = "FORCE ADD TODO GENERAL EXCEPTION"
        const val FORCE_GENERAL_EXCEPTION = "GENERAL EXCEPTION"
        const val USERNAME_EMPTY = "Username is empty"
        const val SQLiteError = "SQLiteError"
        const val WrongUserId = "WrongUserId"
        const val POST_GENERAL_ERROR = "An error occurred while fetching POSTS."
    }

}