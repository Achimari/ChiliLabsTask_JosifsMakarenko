package com.example.myapplication.core


import com.example.myapplication.core.Responce.GifsResponse
import com.example.myapplication.util.Constant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("gifs/search")
    fun searchGifs(
        @Query("q") query: String,
        @Query("api_key") apiKey: String = Constant.KEY,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("min_id") sinceId: String? = null
    ): Call<GifsResponse>
}