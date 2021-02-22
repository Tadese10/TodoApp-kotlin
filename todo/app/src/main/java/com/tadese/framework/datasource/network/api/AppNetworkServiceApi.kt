package com.tadese.framework.datasource.network.api

import androidx.lifecycle.LiveData
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

 interface AppNetworkServiceApi {

    @GET("users")
   suspend fun loginUser(@Query("username") username : String): Response<List<LoginUser>>

    @POST("todos")
    suspend fun addTodo(@Body todo : Todo): Response<Todo>

    @GET("todos")
    suspend fun getAllTodoByUserId(@Query("userId") userId : String): Response<List<Todo>>

    @GET("todos")
    suspend fun getAllTodo(): Response<List<Todo>>

    @POST("comments")
    suspend fun addPostComment(@Body comment: Comment): Response<Comment>

    @POST("posts")
    suspend fun addPost(@Body post: Post): Response<Post>

    @GET("posts")
    suspend fun getAllPost(): Response<List<Post>>

    @GET("posts/{postId}")
    suspend fun findPostById(@Path("postId") postId: Int): Response<Post?>

    @GET("comments")
    suspend fun findCommentById(@Query("id") id: Int): Response<List<Comment>>

    @GET("posts")
    suspend fun getPostByUserId(@Query("userId") userId: Int) : Response<List<Post>>

    @GET("comments")
    suspend fun getPostCommentsByPostId(@Query("postId") postId: Int) : Response<List<Comment>>

}