package com.tadese.business.interactors.comment

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstraction.TodoNetworkDatasource
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.state.*
import com.tadese.framework.presentation.comment.state.CommentStateEvent
import com.tadese.framework.presentation.comment.state.CommentViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AddPostComment(
    private val todoNetworkDataSource: TodoNetworkDatasource
) {
    suspend fun addPostComment(
        stateEvent: CommentStateEvent.AddCommentStateEvent
    ): Flow<DataState<CommentViewState>> = flow {

        val networkResult = appApiCall(Dispatchers.IO) {
            todoNetworkDataSource.addPostComment(stateEvent.comment)
        }

        var handler = object : ApiResponseHandler<CommentViewState, Comment>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: Comment): DataState<CommentViewState> {

                val viewState = CommentViewState(
                    AddComment  = resultObj
                )
                return DataState.data(
                    response = Response(
                        message = ADD_COMMENT_SUCCESS,
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
                    message = handler.stateMessage?.response?.message +" - "+ ADD_COMMENT_FAILED,
                    uiComponentType =  UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }

        emit(handler)
    }

    companion object {
        const val ADD_COMMENT_SUCCESS = "Successfully added Comment."
        const val ADD_COMMENT_FAILED = "An error occurred while adding Comment."
    }
}