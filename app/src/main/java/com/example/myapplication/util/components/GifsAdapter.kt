package com.example.myapplication.util.components

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.core.Response.GifsItem

class GifsAdapter(
    private val gifs: MutableList<GifsItem>,
    val loadMore: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var allGifs: MutableList<GifsItem> = mutableListOf()
    private var isLoading = false

    companion object {
        private const val VIEW_TYPE_GIF = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    init {
        allGifs.addAll(gifs)
    }

    open class GifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivGif)
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GIF -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layout, parent, false)
                GifViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_main, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) gifs.size + 1 else gifs.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == gifs.size && isLoading) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_GIF
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GifViewHolder -> {
                val data = gifs[position]
                holder.imageView.apply {
                    Glide.with(context)
                        .load(data.images.original.url)
                        .into(this)
                }
            }
            is LoadingViewHolder -> {
                loadMore()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGifs(newGifs: List<GifsItem>) {
        gifs.clear()
        allGifs.clear()
        gifs.addAll(newGifs)
        allGifs.addAll(newGifs)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun showLoadingIndicator(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }
}


