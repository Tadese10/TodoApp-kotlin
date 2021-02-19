package com.tadese.di

import com.tadese.business.data.TodoDataFactory
import com.tadese.business.data.cache.FakeTodoCacheDataSourceImpl
import com.tadese.business.data.cache.abstract.TodoCacheDataSource
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.util.isUnitTest
import kotlin.collections.HashMap

class DependencyContainer {
    lateinit var todoDataFactory: TodoDataFactory
    lateinit var todoCacheDataSource: FakeTodoCacheDataSourceImpl
    lateinit var todoNetworkDatasource: FakeTodoNetworkDataSourceImpl
    var loadTodoData : Boolean = false //Simulating this for loggedIn User
    var userId : Int = 0 //LoggedIn UserId

    init {
        isUnitTest = true // for Logger.kt
    }

    fun build() {

        this.javaClass.classLoader?.let{classLoader ->
            todoDataFactory = TodoDataFactory(classLoader)
        }
        todoNetworkDatasource = FakeTodoNetworkDataSourceImpl(
            todos = todoDataFactory.produceHashMapOfTodo(
                todoDataFactory.produceListOfTodo()
            ),
            posts = todoDataFactory.produceHashMapOfPosts(
                todoDataFactory.produceListOfPosts()
            ),
            users = todoDataFactory.produceHashMapOfUsers(
              todoDataFactory.produceListOfUsers()
            ),
            postsComments = todoDataFactory.produceHashMapOfPostsComments(
                todoDataFactory.produceListOfPostsComments()
            )
        )

        todoCacheDataSource = FakeTodoCacheDataSourceImpl(
            todos = todoDataFactory.produceHashMapOfTodo(
                if(loadTodoData) todoDataFactory.produceListOfTodo().filter { it.userId == userId.toString() } else todoDataFactory.produceEmptyListOfTodo()
            )
        )
    }


}