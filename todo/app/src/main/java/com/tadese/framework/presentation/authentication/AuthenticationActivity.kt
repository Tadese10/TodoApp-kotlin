package com.tadese.framework.presentation.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.tadese.R
import com.tadese.business.domain.state.*
import com.tadese.business.interactors.authentication.UserLogin
import com.tadese.framework.presentation.BaseApplication
import com.tadese.framework.presentation.MainActivity
import com.tadese.framework.presentation.UIController
import com.tadese.framework.presentation.authentication.state.AuthenticationStateEvent
import com.tadese.framework.presentation.common.*
import kotlinx.android.synthetic.main.activity_authentication.*
import javax.inject.Inject

class AuthenticationActivity : AppCompatActivity() , UIController {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: AuthenticationViewModel by viewModels {
        viewModelFactory
    }

    private var dialogInView: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        if(viewModel.hasUserLoggedIn())
            gotoMainPage()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        viewModel.setupChannel()
        subscribeObservers()
    }

    private fun inject(){
        (application as BaseApplication).appComponent
            .inject(this)
    }

    fun enableButton(){
        button_login.enable()
    }

    fun disableButton(){
        button_login.disable()
    }

    fun Click(view: View){
        hideSoftKeyboard()
        disableButton()
        viewModel.setStateEvent(AuthenticationStateEvent.AuthenticateUserEvent(username = username.text.toString()))
    }

    private fun subscribeObservers() {

        viewModel.shouldDisplayProgressBar.observe(this, Observer {
            this.displayProgressBar(it)
        })

        viewModel.viewState.observe(this, Observer {
            if(it != null){
                it.userLogin?.let { userLogin ->
                   gotoMainPage()
                }
            }
        })

        viewModel.stateMessage.observe(this, Observer { stateMessage ->

            stateMessage?.response?.let { response ->
                viewModel.clearStateMessage()
                viewModel.clearActiveStateEvents()
                when(response.message){

                    UserLogin.AUTHENTICATION_SUCCESSFUL -> {
                    }

                    Username_Empty ->{
                        onResponseReceived(
                            response = stateMessage.response,
                            stateMessageCallback = object: StateMessageCallback {
                                override fun removeMessageFromStack() {
                                    enableButton()
                                }
                            }
                        )
                    }

                    else -> {
                        onResponseReceived(
                            response = stateMessage.response,
                            stateMessageCallback = object: StateMessageCallback {
                                override fun removeMessageFromStack() {
                                    enableButton()
                                }
                            }
                        )
                    }

                }
            }

        })
    }

    private fun gotoMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isDisplayed: Boolean) {
        if(isDisplayed)
            progress_bar.visible()
        else
            progress_bar.gone()
    }


    override fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback) {
        dialogInView = MaterialDialog(this).show {
            title(text = title)
            input(
                waitForPositiveButton = true,
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            ){ _, text ->
                callback.onTextCaptured(text.toString())
            }
            positiveButton(R.string.text_ok)
            onDismiss {
                dialogInView = null
            }
            cancelable(true)
        }
    }

    override fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    ) {
        when(response.uiComponentType){

//            is UIComponentType.SnackBar -> {
//                val onDismissCallback: TodoCallback?
//                        = response.uiComponentType.onDismissCallback
//                val undoCallback: SnackbarUndoCallback?
//                        = response.uiComponentType.undoCallback
//                response.message?.let { msg ->
//                    displaySnackbar(
//                        message = msg,
//                        snackbarUndoCallback = undoCallback,
//                        onDismissCallback = onDismissCallback,
//                        stateMessageCallback = stateMessageCallback
//                    )
//                }
//            }

            is UIComponentType.AreYouSureDialog -> {

                response.message?.let {
                    areYouSureDialog(
                        message = it,
                        callback = response.uiComponentType.callback,
                        stateMessageCallback = stateMessageCallback
                    )
                }
            }

            is UIComponentType.Toast -> {
                response.message?.let {
                    displayToast(
                        message = it,
                        stateMessageCallback = stateMessageCallback
                    )
                }
            }

            is UIComponentType.Dialog -> {
                displayDialog(
                    response = response,
                    stateMessageCallback = stateMessageCallback
                )
            }

            is UIComponentType.None -> {
                // This would be a good place to send to your Error Reporting
                // software of choice (ex: Firebase crash reporting)
                stateMessageCallback.removeMessageFromStack()
            }
        }
    }

    private fun displayDialog(
        response: Response,
        stateMessageCallback: StateMessageCallback
    ){
        response.message?.let { message ->

            dialogInView = when (response.messageType) {

                is MessageType.Error -> {
                    displayErrorDialog(
                        message = message,
                        stateMessageCallback = stateMessageCallback
                    )
                }

                is MessageType.Success -> {
                    displaySuccessDialog(
                        message = message,
                        stateMessageCallback = stateMessageCallback
                    )
                }

                is MessageType.Info -> {
                    displayInfoDialog(
                        message = message,
                        stateMessageCallback = stateMessageCallback
                    )
                }

                else -> {
                    // do nothing
                    stateMessageCallback.removeMessageFromStack()
                    null
                }
            }
        }?: stateMessageCallback.removeMessageFromStack()
    }

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        if(dialogInView != null){
            (dialogInView as MaterialDialog).dismiss()
            dialogInView = null
        }
    }

    private fun displaySuccessDialog(
        message: String?,
        stateMessageCallback: StateMessageCallback
    ): MaterialDialog {
        return MaterialDialog(this)
            .show{
                title(R.string.text_success)
                message(text = message)
                positiveButton(R.string.text_ok){
                    stateMessageCallback.removeMessageFromStack()
                    dismiss()
                }
                onDismiss {
                    dialogInView = null
                }
                cancelable(false)
            }
    }

    private fun displayErrorDialog(
        message: String?,
        stateMessageCallback: StateMessageCallback
    ): MaterialDialog {
        return MaterialDialog(this)
            .show{
                title(R.string.text_error)
                message(text = message)
                positiveButton(R.string.text_ok){
                    stateMessageCallback.removeMessageFromStack()
                    dismiss()
                }
                onDismiss {
                    dialogInView = null
                }
                cancelable(false)
            }
    }

    private fun displayInfoDialog(
        message: String?,
        stateMessageCallback: StateMessageCallback
    ): MaterialDialog {
        return MaterialDialog(this)
            .show{
                title(R.string.text_info)
                message(text = message)
                positiveButton(R.string.text_ok){
                    stateMessageCallback.removeMessageFromStack()
                    dismiss()
                }
                onDismiss {
                    dialogInView = null
                }
                cancelable(false)
            }
    }

    private fun areYouSureDialog(
        message: String,
        callback: AreYouSureCallback,
        stateMessageCallback: StateMessageCallback
    ): MaterialDialog {
        return MaterialDialog(this)
            .show{
                title(R.string.are_you_sure)
                message(text = message)
                negativeButton(R.string.text_cancel){
                    stateMessageCallback.removeMessageFromStack()
                    callback.cancel()
                    dismiss()
                }
                positiveButton(R.string.text_yes){
                    stateMessageCallback.removeMessageFromStack()
                    callback.proceed()
                    dismiss()
                }
                onDismiss {
                    dialogInView = null
                }
                cancelable(false)
            }
    }

    companion object{
        const val Username_Empty = "Username is empty"
    }
}