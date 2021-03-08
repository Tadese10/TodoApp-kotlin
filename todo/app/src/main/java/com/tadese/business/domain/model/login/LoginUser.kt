package com.tadese.business.domain.model.login

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginUser constructor(
    val id: Int,
    val name : String,
    val username: String,
    val email: String,
    val phone : String,
    val website : String,
    val address: UserAddress
): Parcelable {
    override fun toString(): String {
        return "LoginUser(id=$id, name='$name', username='$username', email='$email', phone='$phone', website='$website', address=$address)"
    }
}