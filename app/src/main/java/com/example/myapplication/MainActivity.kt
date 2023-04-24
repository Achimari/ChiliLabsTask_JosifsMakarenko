package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.core.ApiService
import com.example.myapplication.core.Response.GifsItem
import com.example.myapplication.core.Response.GifsResponse
import com.example.myapplication.util.Constant
import com.example.myapplication.util.GifsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val gifs = mutableListOf<GifsItem>()
        val adapter = GifsAdapter(gifs) {}

        var isLoading: Boolean
        val VISIBLE_THRESHOLD = 3

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        val retrofit = Retrofit.Builder().baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retrofit.create(ApiService::class.java)

        isLoading = true
        progressBar.visibility = View.GONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                retroService.searchGifs(query, limit = 10, offset = totalItemCount).enqueue(object : Callback<GifsResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<GifsResponse>, response: Response<GifsResponse>) {
                        val body = response.body()
                        if (body == null) {
                            Log.d(TAG, "onResponse: No response")
                        } else {
                            gifs.clear()
                            gifs.addAll(body.data)
                            adapter.notifyDataSetChanged()
                        }
                        isLoading = false
                        progressBar.visibility = View.GONE
                        adapter.showLoadingIndicator(true)

                        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                                val totalItemCount = layoutManager.itemCount
                                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + VISIBLE_THRESHOLD)) {
                                    isLoading = true
                                    progressBar.visibility = View.VISIBLE
                                    adapter.showLoadingIndicator(false)
                                    retroService.searchGifs(query, limit = 10, offset = totalItemCount)
                                        .enqueue(object : Callback<GifsResponse> {
                                            @SuppressLint("NotifyDataSetChanged")
                                            override fun onResponse(call: Call<GifsResponse>, response: Response<GifsResponse>) {
                                                val body = response.body()
                                                if (body == null) {
                                                    Log.d(TAG, "onResponse: No response")
                                                } else {
                                                    gifs.addAll(body.data)
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

                    override fun onFailure(call: Call<GifsResponse>, t: Throwable) {
                        Log.d(TAG, "onFailure: ${t.message}")
                    }
                })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }
}