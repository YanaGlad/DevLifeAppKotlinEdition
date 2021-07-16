package com.example.firstkotlinapp.api

import com.example.firstkotlinapp.models.Gif
import com.example.firstkotlinapp.models.Gifs
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("/random?json=true")
    suspend fun getRandomGif() : Response<Gif>

    @GET("/latest/{page}?json=true")
    suspend fun getLatestGifs(
        @Path(value = "page", encoded = true)page: Int,
        @Query(value = "pageSize", encoded = true)pageSize:Int,
        @Query(value = "types", encoded = true)types: String?
    ) : Response<Gifs>


    @GET("/top/{page}?json=true")
    suspend fun getTopGifs(
        @Path(value = "page", encoded = true)page: Int,
        @Query(value = "pageSize", encoded = true)pageSize:Int,
        @Query(value = "types", encoded = true)types: String?
    ) : Response<Gifs>
}