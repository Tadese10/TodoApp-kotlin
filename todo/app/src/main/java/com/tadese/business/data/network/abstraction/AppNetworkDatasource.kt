package com.tadese.business.data.network.abstraction

import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo

interface AppNetworkDatasource {

    suspend fun loginUser(username : String): LoginUser?

    suspend fun addTodo(todo : Todo):Todo?

    suspend fun getAllTodoByUserId(userId : String):List<Todo>

    suspend fun getAllTodo(): List<Todo>

    suspend fun addPostComment(comment: Comment): Comment

    suspend fun addPost(post: Post): Post

    suspend fun getAllPost(): List<Post>

    suspend fun findPostById(potsId: Int): Post?

    suspend fun findCommentById(commentId: Int): Comment?

    suspend fun getPostByUserId(userId: Int) : List<Post>

    suspend fun getPostCommentsByPostId(postId: Int) : List<Comment>

}