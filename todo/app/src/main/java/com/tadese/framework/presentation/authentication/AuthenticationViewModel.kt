package com.tadese.framework.presentation.authentication

import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.*
import com.tadese.business.interactors.authentication.AuthenticationInteractors
import com.tadese.business.interactors.common.CommonInteractors
import com.tadese.business.interactors.common.GetSavedUserData
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent
import com.tadese.framework.presentation.authentication.state.AuthenticationViewState
import com.tadese.framework.presentation.common.BaseViewModel
import com.tadese.framework.presentation.common.CommonStateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

@FlowPreview
@ExperimentalCoroutinesApi
class AuthenticationViewModel(
    private val authenticationInteractors: AuthenticationInteractors,
    private val commonInteractors: CommonInteractors
) : BaseViewModel<AuthenticationViewState>() {

    override fun handleNewData(data: AuthenticationViewState) {
        data?.let { viewState ->

            viewState.userLogin?.let {
                setLoginData(it)
            }
        }

    }

    private fun setLoginData(data: LoginUser){
        val update = getCurrentViewStateOrNew()
        update.userLogin = data
        setViewState(update)
    }

    fun hasUserLoggedIn(): Boolean{
        var userHasLoggedIn = false
        runBlocking {
           userHasLoggedIn = commonInteractors.getSavedUserData.get() != null
        }
        return userHasLoggedIn
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<AuthenticationViewState>?> = when(stateEvent){
            is AuthenticationStateEvent.AuthenticateUserEvent -> {
                 val username = stateEvent.username
                if(!username.isNullOrEmpty()){
                    authenticationInteractors.userLogin.login(
                        stateEvent = stateEvent
                    )
                }else{
                    emitStateMessageEvent(
                        stateMessage = StateMessage(
                            response = Response(
                                message = EMPTY_USERNAME,
                                uiComponentType = UIComponentType.Toast(),
                                messageType = MessageType.Error()
                            )
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

            else ->  emitInvalidStateEvent(stateEvent)
        }

        launchJob(stateEvent, job)
    }

    private fun getUserName(): String? {
        return getCurrentViewStateOrNew().userName
    }

    override fun initNewViewState(): AuthenticationViewState {
      return AuthenticationViewState()
    }

    companion object{
        const val EMPTY_USERNAME = "Username is empty!"
    }
}