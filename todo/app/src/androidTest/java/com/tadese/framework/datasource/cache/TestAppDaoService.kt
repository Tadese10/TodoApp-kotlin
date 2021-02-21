package com.tadese.framework.datasource.cache

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.tadese.business.domain.model.post.Post
import com.tadese.framework.BaseTest
import com.tadese.business.domain.model.todo.Todo
import com.tadese.di.TestAppComponent
import com.tadese.framework.datasource.cache.abstraction.AppDaoService
import com.tadese.framework.datasource.cache.database.TodoDao
import com.tadese.framework.datasource.cache.implementation.AppDaoServiceImpl
import com.tadese.framework.datasource.cache.model.LoggedInUserCacheMapper
import com.tadese.framework.datasource.cache.model.PostCacheMapper
import com.tadese.framework.datasource.cache.model.TodoCacheMapper
import com.tadese.framework.datasource.data.TestAppDataFactory
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
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
class TestAppDaoService : BaseTest(){

    /*
            Test Cases
            1. addTodo_Success_ConfirmSavedIntoDb
            3. searchTodo_Success_ConfirmDbNotEmpty
            4. getNumTodo_Success_ConfirmNonZeroResult
            5. getAllTodo_Success_ConfirmListReceived
            6. saveUserTodos_Success_ConfirmSavedIntoDb
            7. saveLoggedInUserData_Success_ConfirmSavedIntoDb
            8. getLoggedInUserData_Success_ReceivedData
            9. g_addPost_Success_searchPostByIdConfirmSavedPost
            10. searchPost_Success_ConfirmResultNotEmpty
            12. getNumPost_Success
            13. getAllPost_Success
            14. savePosts_Success
     */

    //System in test
    private lateinit var appDaoService: AppDaoService

    //dependencies
    @Inject
    lateinit var  postDao: TodoDao
    @Inject
    lateinit var postCacheMapper: PostCacheMapper
    @Inject
    lateinit var todoCacheMapper: TodoCacheMapper
    @Inject
    lateinit var loggedInUserCacheMapper: LoggedInUserCacheMapper

    @Inject
    lateinit var testAppDataFactory: TestAppDataFactory

    init {
        injectTest()
        insertTestData()
        appDaoService = AppDaoServiceImpl(
            postDao = postDao,
            postCacheMapper = postCacheMapper,
            todoCacheMapper = todoCacheMapper,
            loggedInUserCacheMapper = loggedInUserCacheMapper
        )
    }

    private fun insertTestData() {

    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

    @Test
    fun a_addTodo_Success_searchTodoByIdConfirmSavedIntoDb() = runBlocking {
        var newTodo = Todo(
            id = 1,
            userId = "1",
            title = UUID.randomUUID().toString(),
            completed = false
        )
        val result = appDaoService.addTodo(newTodo)

        assertTrue(result > 0)

        val savedTodo = appDaoService.searchTodoById(newTodo.id)
        assertEquals(newTodo,savedTodo)
    }

    @Test
    fun b_searchTodo_Success_ConfirmDbNotEmpty() = runBlocking {

        val result = appDaoService.searchTodo("1",1)

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun c_getNumTodo_Success_ConfirmNonZeroResult() = runBlocking {

        val result = appDaoService.getNumTodo()

        assertTrue(result > 0)
    }

    @Test
    fun d_getAllTodo_Success_ConfirmListReceived() = runBlocking {

        val result = appDaoService.getAllTodo()

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun e_saveUserTodos_Success_ConfirmSavedIntoDb() = runBlocking {
        var todos = testAppDataFactory.produceListOfTodo() //Get random todos
        val result = appDaoService.saveUserTodos(todos)

        assertTrue(result.isNotEmpty())

        var savedList = appDaoService.getAllTodo()

        assertTrue(savedList.size > 1)
    }

    @Test
    fun f_saveLoggedInUserData_Success_ConfirmSavedIntoDb() = runBlocking {
        var user = testAppDataFactory.produceListOfUsers()[0] //Get random test user
        val result = appDaoService.saveLoggedInUserData(user)

        assertTrue(result > 0)

        var savedUser = appDaoService.getLoggedInUserData()

        assertEquals(user, savedUser)
    }

    @Test
    fun g_addPost_Success_SearchPostByIdConfirmSavedPost() = runBlocking {
        var post = Post(
            id =1,
            userId = 1,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            comments = ArrayList()
        )
        val result = appDaoService.addPost(post)

        assertTrue(result > 0)

        var savedPost = appDaoService.searchPostById(1)

        assertEquals(post, savedPost)

        assertTrue(savedPost?.comments.isNullOrEmpty())
    }

    @Test
    fun h_searchPost_Success_ConfirmResultNotEmpty() = runBlocking {
        val result = appDaoService.searchPost("1",1)

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun h_getNumPost_Success_ConfirmNumberGraterThanZero() = runBlocking {
        val result = appDaoService.getNumPost()

        assertTrue(result > 0)
    }

    @Test
    fun h_getAllPost_Success_ConfirmNumberNotEmptyList() = runBlocking {
        val result = appDaoService.getAllPost()

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun i_savePosts_Success_ConfirmNumberNotEmptyList() = runBlocking {
        var randomPosts = testAppDataFactory.produceListOfPosts()
        val result = appDaoService.savePosts(randomPosts)

        assertTrue(appDaoService.getAllPost().size > 1)
    }

}