package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.core.Response.GifsItem
import com.example.myapplication.util.Constant
import com.example.myapplication.util.components.GifsAdapter
import com.example.myapplication.util.components.GifsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: GifsViewModel
    private lateinit var adapter: GifsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(this).get(GifsViewModel::class.java)

        val gifs = mutableListOf<GifsItem>()
        adapter = GifsAdapter(gifs) {}
        progressBar.visibility = View.GONE

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchGifs(query, Constant.KEY, limit = 10, offset = adapter.itemCount, recyclerView, progressBar, adapter)
                adapter.updateGifs(emptyList())
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        searchView.setQuery("", false)

        viewModel.getGifs().observe(this) { gifs ->
            adapter.updateGifs(gifs)
        }
    }
}
