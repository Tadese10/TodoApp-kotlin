package com.tadese.business.interactors.comment

import com.tadese.business.data.cache.FakeTodoCacheDataSourceImpl
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.DataState
import com.tadese.business.interactors.comment.AddPostComment.Companion.ADD_COMMENT_FAILED
import com.tadese.business.interactors.comment.AddPostComment.Companion.ADD_COMMENT_SUCCESS
import com.tadese.business.interactors.post.AddNewPost
import com.tadese.business.interactors.post.AddNewPostTest
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.comment.state.CommentStateEvent
import com.tadese.framework.presentation.comment.state.CommentViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

@InternalCoroutinesApi
class AddPostCommentTest {

    /*
            Test Cases
            1. AddPostComment_Success_ConfirmAddedToNetworkDataAndReceivedSuccessMessage
            2. AddPostComment_Failed_GeneralErrorConfirmNotAddedToNetworkAndReceivedFailureMessage
            3. AddPostComment_Failed_WrongPostIdConfirmNotAddedToNetworkAndReceivedFailureMessage
            4. AddPostComment_Failed_WrongEmailConfirmNotAddedToNetworkAndReceivedFailureMessage
     */

    //System in test
    private val addNewComment: AddPostComment

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val todoCacheDataSource: FakeTodoCacheDataSourceImpl
    private val todoNetworkDataSource: FakeTodoNetworkDataSourceImpl
    private var loggedInUser : LoginUser? = null

    init {
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        addNewComment = AddPostComment(
            todoNetworkDataSource = todoNetworkDataSource
        )

        //Active User
        runBlocking {
            loggedInUser = todoNetworkDataSource.loginUser(RightUsername)
        }

    }

    @Test
    fun AddPostComment_Success_ConfirmAddedToNetworkDataAndReceivedSuccessMessage() = runBlocking {
        var randomPost = todoNetworkDataSource.getPostByUserId(loggedInUser!!.id)[0]
        var comment = Comment(
            PostID = randomPost.id!!,
            Email = loggedInUser!!.email,
            body = UUID.randomUUID().toString()
        )
        addNewComment.addPostComment(CommentStateEvent.AddCommentStateEvent(comment)).collect(

            object: FlowCollector<DataState<CommentViewState>>{
                override suspend fun emit(value: DataState<CommentViewState>) {

                    assertEquals(value.stateMessage?.response?.message, ADD_COMMENT_SUCCESS)

                    assertNotNull(value.data?.AddComment)

                    assertEquals(value.data?.AddComment, todoNetworkDataSource.findCommentById(value.data?.AddComment?.Id!!))
                }

            }
        )
    }

    @Test
    fun AddPostComment_Failed_GeneralErrorConfirmNotAddedToNetworkAndReceivedFailureMessage() = runBlocking {
        todoNetworkDataSource.throwPostGeneralError = true

        var randomPost = todoNetworkDataSource.getPostByUserId(loggedInUser!!.id)[0]
        var comment = Comment(
            PostID = randomPost.id!!,
            Email = loggedInUser!!.email,
            body = UUID.randomUUID().toString()
        )
        addNewComment.addPostComment(CommentStateEvent.AddCommentStateEvent(comment)).collect(

            object: FlowCollector<DataState<CommentViewState>>{
                override suspend fun emit(value: DataState<CommentViewState>) {

                    assertTrue(value.stateMessage?.response?.message?.contains(ADD_COMMENT_FAILED) == true)

                    assertNull(value.data?.AddComment)

                }

            }
        )
    }


    @Test
    fun AddPostComment_Failed_WrongPostIdConfirmNotAddedToNetworkAndReceivedFailureMessage() = runBlocking {
        var randomPost = todoNetworkDataSource.getPostByUserId(loggedInUser!!.id)[0]
        var comment = Comment(
            PostID = 1000000,
            Email = loggedInUser!!.email,
            body = UUID.randomUUID().toString()
        )
        addNewComment.addPostComment(CommentStateEvent.AddCommentStateEvent(comment)).collect(

            object: FlowCollector<DataState<CommentViewState>>{
                override suspend fun emit(value: DataState<CommentViewState>) {

                    assertTrue(value.stateMessage?.response?.message?.contains(ADD_COMMENT_FAILED) == true)

                    assertNull(value.data?.AddComment)

                }

            }
        )
    }

    @Test
    fun AddPostComment_Failed_WrongEmailConfirmNotAddedToNetworkAndReceivedFailureMessage() = runBlocking {
        var randomPost = todoNetworkDataSource.getPostByUserId(loggedInUser!!.id)[0]
        var comment = Comment(
            PostID = randomPost.id!!,
            Email = randome_email,
            body = UUID.randomUUID().toString()
        )
        addNewComment.addPostComment(CommentStateEvent.AddCommentStateEvent(comment)).collect(

            object: FlowCollector<DataState<CommentViewState>>{
                override suspend fun emit(value: DataState<CommentViewState>) {

                    assertTrue(value.stateMessage?.response?.message?.contains(ADD_COMMENT_FAILED) == true)

                    assertNull(value.data?.AddComment)

                }

            }
        )
    }

    companion object{
        const val RightUsername = "Bret"
        const val randome_email = "randome_email@gmail.com"
    }

}