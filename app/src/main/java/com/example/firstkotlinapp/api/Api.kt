package com.example.firstkotlinapp.api

import com.example.firstkotlinapp.models.Gif
import com.example.firstkotlinapp.models.Gifs
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("/random?json=true")
    fun getRandomGif(): Call<Gif?>?

    @GET("/latest/{page}?json=true")
    fun getLatestGifs(
        @Path("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("types") types: String?
    ): Call<Gifs?>?

    @GET("/top/{page}?json=true")
    fun getTopGifs(
        @Path("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("types") types: String?
    ): Call<Gifs?>?
}