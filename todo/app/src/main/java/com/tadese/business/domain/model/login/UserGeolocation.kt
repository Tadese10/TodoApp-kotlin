package com.tadese.business.domain.model.login

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserGeolocation constructor(
    val lat : String,
    val lng : String
): Parcelable{
    override fun toString(): String {
        return "UserGeolocation(lat='$lat', lng='$lng')"
    }
}