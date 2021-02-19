package com.tadese.business.interactors.post

import com.tadese.business.data.cache.FakeTodoCacheDataSourceImpl
import com.tadese.business.data.network.FakeTodoNetworkDataSourceImpl
import com.tadese.business.data.network.NetworkErrors.NETWORK_ERROR_UNKNOWN
import com.tadese.business.domain.state.DataState
import com.tadese.di.DependencyContainer
import com.tadese.framework.presentation.post.state.PostStateEvent
import com.tadese.framework.presentation.post.state.PostViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@InternalCoroutinesApi
class GetAllPostsTest {

    /*
           Use Cases
           1. GetAllPosts_Success_RetrievedListOfPostsAndConfirmNotEmpty
           2. GetAllPosts_Failed_GeneralErrorAndConfirmEmptyListOfPost
     */

    //System in test
    private val getAllPost: GetAllPost

    // dependencies
    private val dependencyContainer: DependencyContainer = DependencyContainer()
    private val todoCacheDataSource: FakeTodoCacheDataSourceImpl
    private val todoNetworkDataSource: FakeTodoNetworkDataSourceImpl

    init {
        dependencyContainer.build()
        todoCacheDataSource = dependencyContainer.todoCacheDataSource
        todoNetworkDataSource = dependencyContainer.todoNetworkDatasource
        getAllPost = GetAllPost(
            todoNetworkDataSource = todoNetworkDataSource
        )
    }

    @Test
    fun GetAllPosts_Success_RetrievedListOfPostsAndConfirmNotEmpty() = runBlocking {
        getAllPost.getAllPost(PostStateEvent.GetAllPostEvent()).collect(

            object : FlowCollector<DataState<PostViewState>> {
                override suspend fun emit(value: DataState<PostViewState>) {

                    assertEquals(
                        value.stateMessage?.response?.message,
                        GetAllPost.FETCHED_POST_SUCCESS
                    )

                    assertFalse(value.data?.Posts.isNullOrEmpty())
                }

            }
        )
    }

    @Test
    fun GetAllPosts_Failed_GeneralErrorAndConfirmEmptyListOfPost() = runBlocking {
        todoNetworkDataSource.throwPostGeneralError = true
        getAllPost.getAllPost(PostStateEvent.GetAllPostEvent()).collect(

            object : FlowCollector<DataState<PostViewState>> {
                override suspend fun emit(value: DataState<PostViewState>) {

                    assertTrue(
                        value.stateMessage?.response?.message!!.contains(
                            NETWORK_ERROR_UNKNOWN
                        )
                    )

                    assertNull(value.data?.Posts)
                }

            }
        )
    }
}