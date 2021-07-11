package com.example.firstkotlinapp.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Instance {
    private const val DOMAIN = "https://developerslife.ru/"
    private var builder: Retrofit.Builder? = null;

    fun getInstance(): Retrofit {
        if(builder == null){
            val okHttpBuiler : OkHttpClient.Builder = OkHttpClient.Builder()

            builder = Retrofit.Builder()
                .baseUrl(DOMAIN)
                .client(okHttpBuiler.build())
        }
        return builder!!.build()
    }

}