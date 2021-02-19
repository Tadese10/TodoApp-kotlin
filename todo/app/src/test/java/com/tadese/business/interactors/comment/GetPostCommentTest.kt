package com.tadese.business.interactors.comment

import com.tadese.business.data.cache.FakeTodoCacheDataSourceImpl
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.state.DataState
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.comment.state.CommentStateEvent
import com.tadese.framework.presentation.comment.state.CommentViewState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
class GetPostCommentTest {

    /*
        Use Cases
        1. GetPostComments_Success_RetrievedAllCommentsAndConfirmSuccessMessage
        2. GetPostComments_Failed_GeneralExceptionConfirmEmptyDataAndFailureMessage
        3. GetPostComments_Success_WrongPostIdConfirmEmptyCommentAndNotFoundMessage
     */

    //System in test
    private val getPostComment: GetPostComment

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val todoCacheDataSource: FakeTodoCacheDataSourceImpl
    private val todoNetworkDataSource: FakeTodoNetworkDataSourceImpl
    private var loggedInUser : LoginUser? = null
    private lateinit var randomPost : Post

    init {
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        getPostComment = GetPostComment(
            todoNetworkDataSource = todoNetworkDataSource
        )

        //Active User
        runBlocking{
            loggedInUser = todoNetworkDataSource.loginUser(RightUsername)
        }

    }

    @Test
    fun a_GetPostComments_Success_RetrievedAllCommentsAndConfirmSuccessMessage() = runBlocking {
        randomPost  = todoNetworkDataSource.getPostByUserId(loggedInUser?.id!!)[0]

        getPostComment.getPostComment(CommentStateEvent.GetPostCommentsStateEvent(randomPost)).collect(

            object: FlowCollector<DataState<CommentViewState>>{
                override suspend fun emit(value: DataState<CommentViewState>) {

                    assertTrue(value.data?.Post?.comments?.size!! > 0 )

                    assertEquals(value.stateMessage?.response?.message,
                        GetPostComment.GET_COMMENT_SUCCESS
                    )

                    assertEquals(value.data?.Post?.comments, todoNetworkDataSource.getPostCommentsByPostId(value.data?.Post?.id!!))
                }

            }
        )
    }

    @Test
    fun b_GetPostComments_Failed_GeneralExceptionConfirmEmptyDataAndFailureMessage() = runBlocking {
        todoNetworkDataSource.throwPostGeneralError = true
        randomPost  = todoNetworkDataSource.getPostByUserId(loggedInUser?.id!!)[0]
        getPostComment.getPostComment(CommentStateEvent.GetPostCommentsStateEvent(randomPost)).collect(

            object: FlowCollector<DataState<CommentViewState>>{
                override suspend fun emit(value: DataState<CommentViewState>) {

                    assertNull(value.data)

                    assertTrue(value.stateMessage?.response?.message?.contains(GetPostComment.GET_COMMENT_FAILED) == true)

                }

            }
        )
    }

    @Test
    fun c_GetPostComments_Success_WrongPostIdConfirmEmptyCommentAndNotFoundMessage() = runBlocking {
        randomPost  = todoNetworkDataSource.getPostByUserId(loggedInUser?.id!!)[0]
        var newPost = Post(
            id = 1_000_399_300,
            title = randomPost!!.title,
            body = randomPost!!.body,
            userId = randomPost!!.userId
        )
        getPostComment.getPostComment(CommentStateEvent.GetPostCommentsStateEvent(newPost)).collect(

            object: FlowCollector<DataState<CommentViewState>>{
                override suspend fun emit(value: DataState<CommentViewState>) {

                    assertTrue(value.data?.Post?.comments?.isEmpty() == true)

                    assertEquals(value.stateMessage?.response?.message,
                        GetPostComment.GET_COMMENT_SUCCESS
                    )

                    assertEquals(value.data?.Post?.comments, todoNetworkDataSource.getPostCommentsByPostId(value.data?.Post?.id!!))
                }

            }
        )
    }



    companion object{
        const val RightUsername = "Bret"
        const val randome_email = "randome_email@gmail.com"
    }
}