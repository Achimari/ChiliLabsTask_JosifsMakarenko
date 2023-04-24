package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.core.ApiService
import com.example.myapplication.core.Responce.GifsItem
import com.example.myapplication.core.Responce.GifsResponse
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

        val gifs = mutableListOf<GifsItem>()
        val adapter = GifsAdapter(this, gifs)

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter(newText)
                return true
            }
        })

        val retrofit = Retrofit.Builder().baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retroService = retrofit.create(ApiService::class.java)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                retroService.searchGifs(query).enqueue(object : Callback<GifsResponse> {
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
                    }

                    override fun onFailure(call: Call<GifsResponse>, t: Throwable) {
                        Log.d(TAG, "onFailure: ${t.message}")
                    }
                })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        retroService.getGifs().enqueue(object : Callback<GifsResponse?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<GifsResponse?>, response: Response<GifsResponse?>) {
                val body = response.body()
                if (body == null) {
                    Log.d(TAG, "onResponse: No response")
                }

                gifs.addAll(body!!.data)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<GifsResponse?>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }
}