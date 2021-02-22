package com.tadese.framework.datasource.network

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.model.todo.Todo
import com.tadese.di.TestAppComponent
import com.tadese.framework.BaseTest
import com.tadese.framework.datasource.network.abstraction.AppNetworkService
import com.tadese.framework.datasource.network.api.AppNetworkServiceApi
import com.tadese.framework.datasource.network.implementation.AppNetworkServiceImple
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TestAppNetworkServiceApiImple : BaseTest() {

    /*
           Use Cases
           1. loginUser_Success_RightUserName_RetrievedUserDetails
           2. loginUser_Failed_WrongUserName_RetrievedNull
           3. loginUser_Failed_EmptyUserName_RetrievedNull
           4. addTodo_Success_ReceivedSuccessResponse
           5. getAllTodoByUserId_Success_RetrievedUserTodos
           6. getAllTodo_Success_RetrievedAllTodos
           7. addPost_Success_AddedNewPostToNetworkAndReceivedItBack
           8. addPostComment_Success_ReceivedPositiveNetworkResponse
           9. getAllPost_Success_ReceivedAllPostsFromNetwork
           10. findPostById_Success_RetrievedPostById
           11. findCommentById_Success_RetrievedCommentDetailsById
           12. getPostByUserId_Success_RetrievedUserPostsByUserId
           13 getPostCommentsByPostId_Success_RetrievedAllCommentsByPostId
     */

    //System in test
    private lateinit var appNetworkService: AppNetworkService

    //Dependencies
    @Inject
    lateinit var appNetworkServiceApi: AppNetworkServiceApi

    override fun injectTest() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

    init {
        injectTest()
        insertTestData()
        appNetworkService = AppNetworkServiceImple(
          appNetworkServiceApi = appNetworkServiceApi
        )
    }

    private fun insertTestData() {

    }

    @Test
    fun a_loginUser_Success_RightUserName_RetrievedUserDetails() = runBlocking {
        var response = appNetworkService.loginUser(RightUsername)

        Assert.assertNotNull(response)
    }

    @Test
    fun b_loginUser_Failed_WrongUserName_RetrievedNull() = runBlocking {
        var response = appNetworkService.loginUser(WrongUsername)

        Assert.assertNull(response)
    }

    @Test
    fun c_loginUser_Failed_EmptyUserName_RetrievedNull() = runBlocking {
        var response = appNetworkService.loginUser(EmptyUsername)

        Assert.assertNull(response)
    }

    @Test
    fun d_addTodo_Success_ReceivedSuccessResponse() = runBlocking {
        val todo = Todo(
            userId = "1",
            title = UUID.randomUUID().toString(),
            completed = false
        )
        var response = appNetworkService.addTodo(todo)

        Assert.assertNotNull(response)
    }

    @Test
    fun e_getAllTodoByUserId_Success_RetrievedUserTodos() = runBlocking {
        var response = appNetworkService.getAllTodoByUserId("1")

        Assert.assertFalse(response.isNullOrEmpty())
    }

    @Test
    fun f_getAllTodo_Success_RetrievedAllTodos() = runBlocking {
        var response = appNetworkService.getAllTodo()

        Assert.assertFalse(response.isNullOrEmpty())
    }

    @Test
    fun g_addPost_Success_AddedNewPostToNetworkAndReceivedItBack() = runBlocking {
        val post = Post(
            userId = 1,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            comments = ArrayList()
        )
        var response = appNetworkService.addPost(post)

        Assert.assertNotNull(response)
    }

    @Test
    fun h_addPostComment_Success_ReceivedPositiveNetworkResponse() = runBlocking {
        val comment = Comment(
            PostID = 1,
            Name = "id labore ex et quam laborum",
            body = UUID.randomUUID().toString(),
            Email = "Eliseo@gardner.biz"
        )
        var response = appNetworkService.addPostComment(comment)

        Assert.assertNotNull(response)
    }

    @Test
    fun i_getAllPost_Success_ReceivedAllPostsFromNetwork() = runBlocking {
        var response = appNetworkService.getAllPost()

        Assert.assertFalse(response.isNullOrEmpty())
    }

    @Test
    fun j_findPostById_Success_RetrievedPostById() = runBlocking {
        var response = appNetworkService.findPostById(1)

        Assert.assertNotNull(response)
    }

    @Test
    fun k_findCommentById_Success_RetrievedCommentDetailsById() = runBlocking {
        var response = appNetworkService.findCommentById(1)

        Assert.assertNotNull(response)
    }

    @Test
    fun l_getPostByUserId_Success_RetrievedUserPostsByUserId() = runBlocking {
        var response = appNetworkService.getPostByUserId(1)

        Assert.assertFalse(response.isNullOrEmpty())
    }

    @Test
    fun m_getPostCommentsByPostId_Success_RetrievedAllCommentsByPostId() = runBlocking {
        var response = appNetworkService.getPostCommentsByPostId(1)

        Assert.assertFalse(response.isNullOrEmpty())
    }


    companion object{
        const val RightUsername = "Bret"
        const val WrongUsername = "WrongUsername"
        const val EmptyUsername = ""
    }
}