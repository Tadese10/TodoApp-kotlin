package com.tadese.business.interactors.comment

import com.example.cleanarchitecture.business.data.util.appApiCall
import com.tadese.business.data.network.ApiResponseHandler
import com.tadese.business.data.network.abstract.TodoNetworkDatasource
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.post.Post
import com.tadese.business.domain.state.*
import com.tadese.framework.presentation.comment.state.CommentStateEvent
import com.tadese.framework.presentation.comment.state.CommentViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class GetPostComment(
    private val todoNetworkDataSource: TodoNetworkDatasource,
) {
    suspend fun getPostComment(
        stateEvent: CommentStateEvent.GetPostCommentsStateEvent
    ): Flow<DataState<CommentViewState>> = flow {

        val networkResult = appApiCall(Dispatchers.IO) {
            todoNetworkDataSource.getPostCommentsByPostId(stateEvent.post?.id!!)
        }

        var handler = object : ApiResponseHandler<CommentViewState, List<Comment>>(
            response = networkResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: List<Comment>): DataState<CommentViewState> {

                val viewState = CommentViewState(
                    Post  = Post(
                        id = stateEvent.post?.id,
                        userId = stateEvent.post?.userId,
                        title = stateEvent.post?.title!!,
                        body = stateEvent.post?.body!!,
                        comments  = resultObj
                    )
                )
                return DataState.data(
                    response = Response(
                        message = GET_COMMENT_SUCCESS,
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
                    message = handler.stateMessage?.response?.message +" - "+ GET_COMMENT_FAILED,
                    uiComponentType =  UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }

        emit(handler)
    }

    companion object {
        const val GET_COMMENT_SUCCESS = "Successfully fetched Comment(s)."
        const val GET_COMMENT_FAILED = "An error occurred while fetching Comment."
    }
}