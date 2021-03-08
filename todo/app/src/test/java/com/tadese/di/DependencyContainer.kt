package com.tadese.di

import com.tadese.business.data.TodoDataFactory
import com.tadese.business.data.cache.FakeAppCacheDataSourceImpl
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.util.isUnitTest

class DependencyContainer {
    lateinit var todoDataFactory: TodoDataFactory
    lateinit var todoCacheDataSource: FakeAppCacheDataSourceImpl
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

        todoCacheDataSource = FakeAppCacheDataSourceImpl(
            todos = todoDataFactory.produceHashMapOfTodo(
                if(loadTodoData) todoDataFactory.produceListOfTodo().filter { it.userId == userId.toString() } else todoDataFactory.produceEmptyListOfTodo()
            )
        )
    }


}