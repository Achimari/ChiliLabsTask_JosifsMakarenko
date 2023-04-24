package com.example.myapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.core.Responce.GifsItem
import java.util.Locale

class GifsAdapter(val context: Context, val gifs: MutableList<GifsItem>) : RecyclerView.Adapter<GifsAdapter.ViewHolder>() {
    private var allGifs: MutableList<GifsItem> = mutableListOf()
    private var filteredGifs: MutableList<GifsItem> = mutableListOf()

    init {
        allGifs.addAll(gifs)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.ivGif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return gifs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = gifs[position]
        Glide.with(context).load(data.images.original.url)
            .into(holder.imageView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredGifs.clear()
        if (query.isEmpty()) {
            filteredGifs.addAll(allGifs)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            for (gif in allGifs) {
                if (gif.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredGifs.add(gif)
                }
            }
        }
        notifyDataSetChanged()
    }

}
