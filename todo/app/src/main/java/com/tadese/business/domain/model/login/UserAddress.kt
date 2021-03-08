package com.tadese.business.domain.model.login

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserAddress constructor(
    val street: String = "",
    val suite: String = "",
    val city : String = "",
    val zipcode: String ="",
    val geo : UserGeolocation?
): Parcelable {
    override fun toString(): String {
        return "UserAddress(street='$street', suite='$suite', city='$city', zipcode='$zipcode', geo=$geo)"
    }
}