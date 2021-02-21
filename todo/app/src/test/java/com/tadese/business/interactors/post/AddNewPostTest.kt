package com.tadese.business.interactors.post

import com.tadese.business.data.cache.FakeAppCacheDataSourceImpl
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.state.DataState
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.post.state.PostStateEvent
import com.tadese.framework.presentation.post.state.PostViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

@InternalCoroutinesApi
class AddNewPostTest {

    /*
            Test Cases
            1. AddNewPost_Success_ConfirmPostAddedToNetworkAndSuccessMessage
            2. AddNewPost_Failed_ConfirmFailedMessageReceived
     */

    //System in test
    private val addNewPost: AddNewPost

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val todoCacheDataSource: FakeAppCacheDataSourceImpl
    private val todoNetworkDataSource: FakeTodoNetworkDataSourceImpl
    private var loggedInUser : LoginUser? = null

    init {
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        addNewPost = AddNewPost(
            todoNetworkDataSource = todoNetworkDataSource
        )

        //Login User
        runBlocking {
            loggedInUser = todoNetworkDataSource.loginUser(RightUsername)
        }

    }

    @Test
    fun AddNewPost_Success_ConfirmPostAddedToNetworkAndSuccessMessage() = runBlocking {
        var post = Post(
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            userId = loggedInUser?.id,
        )
        addNewPost.addNewPost(PostStateEvent.AddNewPostEvent(post)).collect(

            object : FlowCollector<DataState<PostViewState>>{

                override suspend fun emit(value: DataState<PostViewState>) {

                    assertNotNull(value.data?.newPost)//Assert the response data is not null

                    assertEquals(value.data?.newPost, todoNetworkDataSource.findPostById(value.data?.newPost?.id!!))//Confirm the NewPost was added to network

                    assertEquals(value.stateMessage?.response?.message, AddNewPost.ADD_POST_SUCCESS)
                }

            }
        )
    }

    @Test
    fun AddNewPost_Failed_ConfirmFailedMessageReceived() = runBlocking {
       todoNetworkDataSource.throwPostGeneralError = true
        var post = Post(
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            userId = loggedInUser?.id,
        )
        addNewPost.addNewPost(PostStateEvent.AddNewPostEvent(post)).collect(

            object : FlowCollector<DataState<PostViewState>>{

                override suspend fun emit(value: DataState<PostViewState>) {

                    assertNull(value.data?.newPost)//Assert the response data is null

                    assertTrue(value.stateMessage?.response?.message?.contains(AddNewPost.ADD_POST_FAILED) == true)
                }

            }
        )
    }

    companion object{
        const val RightUsername = "Bret"
    }
}