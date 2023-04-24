package com.example.myapplication.core


import com.example.myapplication.core.Responce.GifsResponse
import com.example.myapplication.util.Constant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("gifs/trending") // For future using
    fun getGifs(
        @Query("api_key") apiKey: String = Constant.KEY,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): Call<GifsResponse>

    @GET("gifs/search")
    fun searchGifs(
        @Query("q") query: String,
        @Query("api_key") apiKey: String = Constant.KEY,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): Call<GifsResponse>
}