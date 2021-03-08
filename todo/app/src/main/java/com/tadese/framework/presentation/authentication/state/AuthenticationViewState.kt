package com.tadese.framework.presentation.authentication.state

import android.os.Parcelable
import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthenticationViewState(
    var userLogin: LoginUser? = null,
    var userName: String?  = null
) : ViewState, Parcelable {

    override fun toString(): String {
        return "AuthenticationViewState(userLogin=$userLogin)"
    }
}