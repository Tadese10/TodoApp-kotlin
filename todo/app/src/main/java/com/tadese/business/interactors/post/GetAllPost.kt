package com.tadese.business.interactors.post

import com.example.cleanarchitecture.business.data.util.safeApiCall
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.state.DataState
import com.tadese.business.domain.state.MessageType
import com.tadese.business.domain.state.Response
import com.tadese.business.domain.state.UIComponentType
import com.tadese.framework.presentation.post.state.PostStateEvent
import com.tadese.framework.presentation.post.state.PostViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class GetAllPost(
    private val todoNetworkDataSource: TodoNetworkDatasource,
) {
    suspend fun getAllPost(
        stateEvent: PostStateEvent.GetAllPostEvent
    ): Flow<DataState<PostViewState>> = flow {

        val networkResult = safeApiCall(Dispatchers.IO) {
            todoNetworkDataSource.getAllPost()
        }

        var handler = object : ApiResponseHandler<PostViewState, List<Post>>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: List<Post>): DataState<PostViewState> {

                val viewState = PostViewState(
                    Posts = resultObj
                )
                var message: String? = FETCHED_POST_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None()
                if (resultObj.isEmpty()) {
                    message = FETCHED_POST_EMPTY
                    uiComponentType = UIComponentType.Toast()
                }
                return DataState.data(
                    response = Response(
                        message = message,
                        uiComponentType = uiComponentType,
                        messageType = MessageType.Success()
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )
            }

        }.getResult()

        emit(handler)
    }

    companion object {
        const val FETCHED_POST_SUCCESS = "Successfully fetched posts"
        const val FETCHED_POST_EMPTY = "No post found."
    }
}