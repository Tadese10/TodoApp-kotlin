package com.tadese.business.domain.model.login


data class UserGeolocation constructor(
    val lat : String,
    val lng : String
){
    override fun toString(): String {
        return "UserGeolocation(lat='$lat', lng='$lng')"
    }
}