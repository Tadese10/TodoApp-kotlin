package com.tadese.framework.datasource.data

import android.app.Application
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestAppDataFactory
@Inject
constructor(
    private val application : Application,
    private val testClassLoader: ClassLoader
){

    fun produceListOfTodo(): List<Todo>{
        return Gson()
            .fromJson(
                getDataFromFile("todos_list.json"),
                object: TypeToken<List<Todo>>() {}.type
            )
    }

    fun produceHashMapOfTodo(todoList: List<Todo>): HashMap<Int, Todo>{
        val map = HashMap<Int, Todo>()
        for(note in todoList){
            map[note.id] = note
        }
        return map
    }

    fun produceHashMapOfPosts(posts: List<Post>): HashMap<Int, Post>{
        val map = HashMap<Int, Post>()
        for(post in posts){
            map[post.id!!] = post
        }
        return map
    }

    fun produceEmptyListOfTodo(): List<Todo>{
        return ArrayList()
    }

    fun getDataFromFile(fileName: String): String {
        return testClassLoader.getResource(fileName).readText()
    }

    fun produceHashMapOfUsers(produceListOfUsers: List<LoginUser>): HashMap<String, LoginUser> {
        val map = HashMap<String, LoginUser>()
        for(note in produceListOfUsers){
            map[note.username] = note
        }
        return map
    }

    fun produceListOfUsers(): List<LoginUser> {
        return Gson()
            .fromJson(
                getDataFromFile("users_list.json"),
                object: TypeToken<List<LoginUser>>() {}.type
            )
    }

    fun produceHashMapOfPostsComments(produceListOfPostsComments: List<Comment>): HashMap<Int, Comment> {
        val map = HashMap<Int, Comment>()
        for(comment in produceListOfPostsComments){
            map[comment.Id!!] = comment
        }
        return map
    }

    fun produceListOfPostsComments(): List<Comment> {
        return Gson()
            .fromJson(
                getDataFromFile("post_comments_list.json"),
                object: TypeToken<List<Comment>>() {}.type
            )
    }

    fun produceListOfPosts(): List<Post> {
        return Gson()
            .fromJson(
                getDataFromFile("posts_list.json"),
                object: TypeToken<List<Post>>() {}.type
            )
    }


    private fun readJSONFromAsset(fileName: String): String?{
        var json : String? = null;
        json = try {
            val inputStream : InputStream = (application.assets as AssetManager).open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        }catch (e: IOException){
            e.printStackTrace()
            return null
        }
        return json
    }

}