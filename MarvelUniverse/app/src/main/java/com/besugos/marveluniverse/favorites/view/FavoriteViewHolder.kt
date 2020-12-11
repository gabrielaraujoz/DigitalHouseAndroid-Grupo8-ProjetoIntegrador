package com.besugos.marveluniverse.favorites.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.besugos.marveluniverse.R
import com.besugos.marveluniverse.favorites.model.FavoriteModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class FavoriteViewHolder(val context: Context, view: View) : RecyclerView.ViewHolder(view) {

    private var name = view.findViewById<TextView>(R.id.txtNameFavoriteCard)
    private val favTxt = view.findViewById<TextView>(R.id.txtFavoriteCard)
    private val avatar = view.findViewById<ImageView>(R.id.imgAvatarFavoriteCard)

    lateinit var favoriteModel: FavoriteModel

    fun bind(favorite: FavoriteModel) {
        favoriteModel = favorite
        name.text = favoriteModel.name
        favTxt.text = context.getString(R.string.favorites_short_description, favoriteModel.id)
        Picasso.get()
            .load(R.drawable.img4)
            .transform(CropCircleTransformation())
            .into(avatar)
    }

    init {
        view.setOnClickListener {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layoutView = inflater.inflate(R.layout.favorites_detail, null)
            val alertaDialog = BottomSheetDialog(context)

            layoutView.findViewById<TextView>(R.id.txtNameFavoritesDetails).text =
                favoriteModel.name

            alertaDialog.apply {
                setContentView(layoutView)
                show()
            }

        }
    }


}