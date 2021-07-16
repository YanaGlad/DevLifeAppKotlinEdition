package com.example.firstkotlinapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.models.GifModel
import com.example.firstkotlinapp.viewmodel.GifViewModel
import java.util.*

class GifsRecyclerAdapter(var context: Context, gifModels: ArrayList<GifModel>, type: String) :
    RecyclerView.Adapter<GifViewHolder>() {

    var gifModels: List<GifModel>
    private val type: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.load_item, parent, false)
        return GifViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        val gifModel: GifModel = gifModels[position]
        val gifViewModel: GifViewModel =
            ViewModelProvider((context as ViewModelStoreOwner)).get<GifViewModel>(
                type,
                GifViewModel::class.java
            )
        holder.setViewModel(gifViewModel)
        holder.loadImage(gifModel.gifURL)
        holder.author.text = context.getString(R.string.by) + " " + gifModel.author
        holder.description.setText(gifModel.description)
    }

    override fun getItemCount(): Int {
        return gifModels.size
    }

    init {
        this.gifModels = gifModels
        this.type = type
    }
}
