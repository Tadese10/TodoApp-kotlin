package com.tadese.framework.datasource.network.util

import com.tadese.framework.datasource.network.api.AppNetworkServiceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkRetrofitBuilder{
    val baseUrl = "https://jsonplaceholder.typicode.com/"
    val retrofitBuilder : Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(LiveDataCallAdapterFactory())
    }

    val API_SERVICE_API : AppNetworkServiceApi by lazy {
        retrofitBuilder.build().create(AppNetworkServiceApi::class.java)
    }

}