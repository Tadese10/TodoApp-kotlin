package com.tadese.framework.presentation.common

import com.tadese.business.domain.state.StateEvent
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent

sealed class CommonStateEvent : StateEvent {

    class GetUserSavedData(
        val username: String
    ) : CommonStateEvent() {

        override fun errorInfo(): String {
            return "Error getting user's saved data."
        }

        override fun eventName(): String {
            return "GetUserSavedData"
        }

        override fun shouldDisplayProgressBar() = false
    }
}