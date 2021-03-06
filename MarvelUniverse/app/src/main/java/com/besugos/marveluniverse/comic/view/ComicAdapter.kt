package com.besugos.marveluniverse.comic.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.besugos.marveluniverse.R
import com.besugos.marveluniverse.comic.model.ComicModel

class ComicAdapter(
    private var comics: MutableList<ComicModel>,
    private val listener: (ComicModel) -> Unit
) :
    RecyclerView.Adapter<ComicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_comic_item, parent, false)
        return ComicViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        val comic = comics[position]
        holder.bind(comic)
        holder.itemView.setOnClickListener { listener(comic) }
    }

    override fun getItemCount() = comics.size
}