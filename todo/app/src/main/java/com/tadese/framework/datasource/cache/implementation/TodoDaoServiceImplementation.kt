package com.tadese.framework.datasource.cache.implementation

import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.framework.datasource.cache.abstraction.TodoDaoService
import com.tadese.framework.datasource.cache.database.TodoDao
import com.tadese.framework.datasource.cache.model.LoggedInUserCacheMapper
import com.tadese.framework.datasource.cache.model.PostCacheMapper
import com.tadese.framework.datasource.cache.model.TodoCacheMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoDaoServiceImplementation
@Inject
constructor(
    val postDao: TodoDao,
    val postCacheMapper: PostCacheMapper,
    val todoCacheMapper: TodoCacheMapper,
    val loggedInUserCacheMapper: LoggedInUserCacheMapper
) : TodoDaoService {

    override suspend fun addTodo(todo: Todo): Long {
        return postDao.insertTodo(todoCacheMapper.mapToEntity(todo))
    }

    override suspend fun searchTodo(query: String, page: Int): List<Todo> {
        return todoCacheMapper.mapFromEntityList(postDao.searchTodos(query, page))
    }

    override suspend fun searchTodoById(primaryKey: Int): Todo? {
        return todoCacheMapper.mapFromEntity(postDao.findTodoById(primaryKey))
    }

    override suspend fun getNumTodo(): Int {
        return postDao.getNumTodos()
    }

    override suspend fun getAllTodo(): List<Todo> {
        return todoCacheMapper.mapFromEntityList(postDao.getAllTodos())
    }

    override suspend fun saveUserTodos(usersTodo: List<Todo>): LongArray {
        return postDao.insertTodos(todoCacheMapper.mapToEntityList(usersTodo))
    }

    override suspend fun saveLoggedInUserData(data: LoginUser): Long {
        return postDao.insertLoggedInUser(loggedInUserCacheMapper.mapToEntity(data))
    }

    override suspend fun getLoggedInUserData(): LoginUser? {
        val usersList = postDao.findLoggedInUser()
        return if (!usersList.isNullOrEmpty()) {
            loggedInUserCacheMapper.mapFromEntity(usersList[0])
        } else
            null
    }

    override suspend fun addPost(post: Post): Long {
        return postDao.insertPost(postCacheMapper.mapToEntity(post))
    }

    override suspend fun searchPost(query: String, page: Int): List<Todo> {
        return todoCacheMapper.mapFromEntityList(postDao.searchTodos(query, page))
    }

    override suspend fun searchPostById(primaryKey: Int): Post? {
        return postCacheMapper.mapFromEntity(postDao.findPostById(primaryKey))
    }

    override suspend fun getNumPost(): Int {
        return postDao.getNumPosts()
    }

    override suspend fun getAllPost(): List<Post> {
        return postCacheMapper.mapFromEntityList(postDao.getAllPosts())
    }

    override suspend fun savePosts(posts: List<Post>): LongArray {
        return postDao.insertPosts(postCacheMapper.mapToEntityList(posts))
    }


}