package com.tadese.framework.datasource.network.implementation

import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.framework.datasource.network.abstraction.AppNetworkService
import com.tadese.framework.datasource.network.api.AppNetworkServiceApi
import com.tadese.util.printLogD
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNetworkServiceImple
@Inject
constructor(
    private val appNetworkServiceApi: AppNetworkServiceApi
) : AppNetworkService {

   override suspend fun loginUser(username : String): LoginUser? {

        val response = appNetworkServiceApi.loginUser(username)
        try {
            val apiResponse: List<LoginUser>? = response.body()
            return if(apiResponse?.isEmpty() == false){
                apiResponse[0]
            }else
            {
                throw AppException("Wrong Username.")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun addTodo(todo : Todo): Todo?{
        val response = appNetworkServiceApi.addTodo(todo)
        try {
            //val response = callSync.execute()
            return response.body()
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun getAllTodoByUserId(userId : String):List<Todo>{
        val response = appNetworkServiceApi.getAllTodoByUserId(userId)
        try {
            //val response = callSync.execute()
            var result :  List<Todo>? =  response.body()
            return if(!result.isNullOrEmpty()){
                printLogD("getAllTodoByUserId", result.toString())
                result
            }else{
                ArrayList()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun getAllTodo(): List<Todo>{
        val response = appNetworkServiceApi.getAllTodo()
        try {
            //val response = callSync.execute()
            var result :  List<Todo>? =  response.body()
            return if(!result.isNullOrEmpty()){
                result
            }else{
                ArrayList()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun addPostComment(comment: Comment): Comment{
        val response = appNetworkServiceApi.addPostComment(comment)
        try {
            //val response = callSync.execute()
            return  response.body() ?: throw Exception(response.body().toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun addPost(post: Post): Post{
        val response = appNetworkServiceApi.addPost(post)
        try {
            //val response = callSync.execute()
            return  response.body() ?: throw Exception(response.body().toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override  suspend fun getAllPost(): List<Post>{
        val response = appNetworkServiceApi.getAllPost()
        try {
            //val response = callSync.execute()
            var result :  List<Post>? =  response.body()
            return if(!result.isNullOrEmpty()){
                result
            }else{
                ArrayList()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun findPostById(postId: Int): Post?{
        val response = appNetworkServiceApi.findPostById(postId)
        try {
            //val response = callSync.execute()
            return  response.body() ?: throw Exception(response.body().toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun findCommentById(commentId: Int): Comment?{
        val response = appNetworkServiceApi.findCommentById(commentId)
        try {
            //val response = callSync.execute()
            val apiResponse: List<Comment>? = response.body()
            return if(apiResponse?.size!! > 0){
                apiResponse[0]
            }else
            {
                null
            }
        } catch (ex:Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }


    override suspend fun getPostByUserId(userId: Int) : List<Post>{
        val response = appNetworkServiceApi.getPostByUserId(userId)
        try {
            //val response = callSync.execute()
            return  response.body() ?: throw Exception(response.body().toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    override suspend fun getPostCommentsByPostId(postId: Int) : List<Comment>{
        val response = appNetworkServiceApi.getPostCommentsByPostId(postId)
        try {
            //val response = callSync.execute()
            return  response.body() ?: throw Exception(response.body().toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw Exception(ex)
        }
    }

    companion object{

        class AppException : Exception {
            constructor() : super()
            constructor(message: String) : super(message)
            constructor(message: String, cause: Throwable) : super(message, cause)
            constructor(cause: Throwable) : super(cause)
        }
    }

}