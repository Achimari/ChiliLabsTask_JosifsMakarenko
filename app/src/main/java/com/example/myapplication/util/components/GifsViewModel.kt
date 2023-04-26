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

const val TAG = "MainActivity"
class GifsViewModel(application: Application) : AndroidViewModel(application) {
    private val retroService: ApiService = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    private val tempGifs = mutableListOf<GifsItem>()
    private val gifs: MutableLiveData<List<GifsItem>> = MutableLiveData()
    private var tempQuery = ""

    fun getGifs(): LiveData<List<GifsItem>> {
        return gifs
    }

    fun searchGifs(query: String, key : String, limit: Int, offset: Int, recyclerView: RecyclerView, progressBar: ProgressBar, adapter: GifsAdapter) {
        gifs.value = emptyList()
        findFirstTen(query, key, limit, offset)
        loadMore(recyclerView, progressBar, adapter)
        Log.d("TAG", query);
    }

    private fun findFirstTen(query: String, key: String, limit: Int, offset: Int) {
        tempQuery = query
        retroService.searchGifs(query, key, limit, offset).enqueue(object : Callback<GifsResponse> {
            override fun onResponse(call: Call<GifsResponse>, response: Response<GifsResponse>) {
                val body = response.body()
                if (body == null) {
                    Log.d(TAG, "onResponse: No response")
                } else {
                    tempGifs.clear()
                    tempGifs.addAll(body.data)
                    gifs.value = tempGifs
                }
            }

            override fun onFailure(call: Call<GifsResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun loadMore(
        recyclerView: RecyclerView, progressBar: ProgressBar, adapter: GifsAdapter) {
        val VISIBLE_THRESHOLD = 3
        var isLoading = false

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + VISIBLE_THRESHOLD)) {
                    isLoading = true
                    progressBar.visibility = View.VISIBLE
                    adapter.showLoadingIndicator(true)
                    retroService.searchGifs(tempQuery, limit = 10, offset = totalItemCount)
                        .enqueue(object : Callback<GifsResponse> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onResponse(
                                call: Call<GifsResponse>,
                                response: Response<GifsResponse>
                            ) {
                                val body = response.body()
                                if (body == null) {
                                    Log.d(TAG, "onResponse: No response")
                                } else {
                                    Log.d("TAG", tempQuery);
                                    tempGifs.addAll(body.data)
                                    gifs.value = tempGifs
                                    adapter.notifyDataSetChanged()
                                }
                                progressBar.visibility = View.GONE
                                isLoading = false
                                adapter.showLoadingIndicator(false)
                            }

                            override fun onFailure(call: Call<GifsResponse>, t: Throwable) {
                                progressBar.visibility = View.GONE
                                Log.d(TAG, "onFailure: ${t.message}")
                                isLoading = false
                                adapter.showLoadingIndicator(false)
                            }
                        })
                }
            }
        })
    }
}

