package com.tadese.framework.presentation.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cleanarchitecture.business.data.util.GenericErrors
import com.tadese.business.domain.state.*
import com.tadese.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
@FlowPreview
abstract class BaseViewModel<ViewState> : ViewModel() {

    private val _viewState : MutableLiveData<ViewState> = MutableLiveData()

    var dataChannelManager : DataChannelManager<ViewState> =
        object : DataChannelManager<ViewState>(){

            override fun handleNewData(data: ViewState) {
                this@BaseViewModel.handleNewData(data)
            }

        }

    val viewState: LiveData<ViewState>
        get() = _viewState

    val shouldDisplayProgressBar: LiveData<Boolean>
            = dataChannelManager.shouldDisplayProgressBar

    val stateMessage: LiveData<StateMessage?>
        get() = dataChannelManager.messageStack.stateMessage

    fun setupChannel() = dataChannelManager.setupChannel()
    // FOR DEBUGGING
    fun getMessageStackSize(): Int{
        return dataChannelManager.messageStack.size
    }

    fun setViewState(viewState: ViewState){
        _viewState.value = viewState
    }


    abstract fun handleNewData(data: ViewState)

    abstract fun setStateEvent(stateEvent: StateEvent)

    fun emitStateMessageEvent(
        stateMessage: StateMessage,
        stateEvent: StateEvent
    ) = flow{
        emit(
            DataState.error<ViewState>(
                response = stateMessage.response,
                stateEvent = stateEvent
            )
        )
    }


    fun emitInvalidStateEvent(stateEvent: StateEvent) = flow {
        emit(
            DataState.error<ViewState>(
                response = Response(
                    message = GenericErrors.INVALID_STATE_EVENT,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Error()
                ),
                stateEvent = stateEvent
            )
        )
    }

    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<ViewState>?>
    ) = dataChannelManager.launchJob(stateEvent, jobFunction)

    fun getCurrentViewStateOrNew(): ViewState{
        return viewState.value ?: initNewViewState()
    }

    fun clearStateMessage(index: Int = 0){
        printLogD("BaseViewModel", "clearStateMessage")
        dataChannelManager.clearStateMessage(index)
    }

    fun clearActiveStateEvents() = dataChannelManager.clearActiveStateEventCounter()

    fun clearAllStateMessages() = dataChannelManager.clearAllStateMessages()

    fun printStateMessages() = dataChannelManager.printStateMessages()

    fun cancelActiveJobs() = dataChannelManager.cancelJobs()

    abstract fun initNewViewState(): ViewState
}