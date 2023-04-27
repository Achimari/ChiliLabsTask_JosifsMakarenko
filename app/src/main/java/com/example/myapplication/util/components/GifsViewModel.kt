package com.example.myapplication.util.components


import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.core.ApiService
import com.example.myapplication.core.Response.GifsItem
import com.example.myapplication.core.Response.GifsResponse
import com.example.myapplication.util.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TAG = "GifsViewModel"

class GifsViewModel(application: Application) : AndroidViewModel(application) {

    private val retroService: ApiService = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    private var tempQuery = ""
    private var limit = 10
    private var offset = 0

    val gifs: MutableLiveData<List<GifsItem>> = MutableLiveData()

    fun searchGifs(query: String, key : String) {
        gifs.value = emptyList()
        findFirstTen(query, key)
    }

    private fun findFirstTen(query: String, key: String) {
        tempQuery = query
        retroService.searchGifs(query, key, limit, offset).enqueue(object : Callback<GifsResponse> {
            override fun onResponse(call: Call<GifsResponse>, response: Response<GifsResponse>) {
                val body = response.body()
                if (body == null) {
                    Log.d(TAG, "onResponse: No response")
                } else {
                    gifs.value = body.data
                }
            }

            override fun onFailure(call: Call<GifsResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun loadMore() {
        offset += limit
        retroService.searchGifs(tempQuery, Constant.KEY, limit, offset)
            .enqueue(object : Callback<GifsResponse> {
                override fun onResponse(call: Call<GifsResponse>, response: Response<GifsResponse>) {
                    val body = response.body()
                    if (body == null) {
                        Log.d(TAG, "onResponse: No response")
                    } else {
                        val currentList = gifs.value.orEmpty().toMutableList()
                        currentList.addAll(body.data)
                        gifs.value = currentList
                    }
                }

                override fun onFailure(call: Call<GifsResponse>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                }
            })
    }
}


