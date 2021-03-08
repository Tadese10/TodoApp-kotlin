package com.tadese.business.data.network.implementation

import com.tadese.business.data.network.abstraction.AppNetworkDatasource
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.framework.datasource.network.implementation.AppNetworkServiceImple
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNetworkDatasourceImple
@Inject
constructor(
    private val appNetworkServiceImple: AppNetworkServiceImple
) : AppNetworkDatasource {

    override suspend fun loginUser(username: String) : LoginUser? = appNetworkServiceImple.loginUser(username)

    override suspend fun addTodo(todo: Todo): Todo?  = appNetworkServiceImple.addTodo(todo)

    override suspend fun getAllTodoByUserId(userId: String): List<Todo> = appNetworkServiceImple.getAllTodoByUserId(userId)

    override suspend fun getAllTodo(): List<Todo>  = appNetworkServiceImple.getAllTodo()

    override suspend fun addPostComment(comment: Comment): Comment  = appNetworkServiceImple.addPostComment(comment)

    override suspend fun addPost(post: Post): Post = appNetworkServiceImple.addPost(post)

    override suspend fun getAllPost(): List<Post>  = appNetworkServiceImple.getAllPost()

    override suspend fun findPostById(postId: Int): Post?  = appNetworkServiceImple.findPostById(postId)

    override suspend fun findCommentById(commentId: Int): Comment?  = appNetworkServiceImple.findCommentById(commentId)

    override suspend fun getPostByUserId(userId: Int): List<Post> = appNetworkServiceImple.getPostByUserId(userId)

    override suspend fun getPostCommentsByPostId(postId: Int): List<Comment>  = appNetworkServiceImple.getPostCommentsByPostId(postId)
}