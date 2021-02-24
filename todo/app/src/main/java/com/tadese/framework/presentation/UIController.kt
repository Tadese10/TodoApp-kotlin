package com.tadese.framework.presentation

import com.tadese.business.domain.state.DialogInputCaptureCallback
import com.tadese.business.domain.state.Response
import com.tadese.business.domain.state.StateMessageCallback


interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}