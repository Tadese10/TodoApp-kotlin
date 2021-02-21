package com.tadese.business.interactors.post

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstraction.TodoNetworkDatasource
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.state.*
import com.tadese.framework.presentation.post.state.PostStateEvent
import com.tadese.framework.presentation.post.state.PostViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AddNewPost(
    private val todoNetworkDataSource: TodoNetworkDatasource
) {
    suspend fun addNewPost(
        stateEvent: PostStateEvent.AddNewPostEvent
    ): Flow<DataState<PostViewState>> = flow {

        val networkResult = appApiCall(Dispatchers.IO) {
            todoNetworkDataSource.addPost(stateEvent.post)
        }

        var handler = object : ApiResponseHandler<PostViewState, Post>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: Post): DataState<PostViewState> {

                val viewState = PostViewState(
                    newPost = resultObj
                )
                return DataState.data(
                    response = Response(
                        message = ADD_POST_SUCCESS,
                        uiComponentType =  UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )
            }

        }.getResult()

        if(handler.data == null){
            handler.stateMessage = StateMessage(
                response = Response(
                    message = handler.stateMessage?.response?.message +" - "+ ADD_POST_FAILED,
                    uiComponentType =  UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }

        emit(handler)
    }

    companion object {
        const val ADD_POST_SUCCESS = "Successfully added post"
        const val ADD_POST_FAILED = "An error occurred while adding post."
    }
}