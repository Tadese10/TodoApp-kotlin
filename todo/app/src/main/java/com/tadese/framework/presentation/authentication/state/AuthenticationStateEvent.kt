package com.tadese.framework.presentation.authentication.state

import com.tadese.business.domain.state.StateEvent

sealed class AuthenticationStateEvent: StateEvent {

    class AuthenticateUserEvent(
        val username: String
    ) : AuthenticationStateEvent() {

        override fun errorInfo(): String {
            return "Error authenticating user."
        }

        override fun eventName(): String {
            return "AuthenticateUserEvent"
        }

        override fun shouldDisplayProgressBar() = true
    }


}