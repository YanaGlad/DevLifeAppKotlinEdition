package com.example.firstkotlinapp.adapters

import android.content.Context
import android.content.res.AssetManager
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.values.ErrorHandler
import com.example.firstkotlinapp.viewmodel.GifViewModel

class GifViewHolder(private val context: Context, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var image: AppCompatImageView
    var description: AppCompatTextView
    var author: AppCompatTextView
    private var viewModel: GifViewModel? = null
    private val progressBar: ProgressBar
    private val linearLayoutCompat: LinearLayoutCompat
    private val assetManager: AssetManager
    private val requestListener: RequestListener<GifDrawable> =

        object : RequestListener<GifDrawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<GifDrawable?>,
                isFirstResource: Boolean
            ): Boolean {
                errorHandler = ErrorHandler()
                errorHandler.setImageError()
                viewModel!!.setError(errorHandler)
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable?,
                model: Any,
                target: Target<GifDrawable?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                if (viewModel != null) {
                    viewModel!!.setIsGifLoaded(true)
                    errorHandler.setSuccess()
                    viewModel!!.setError(errorHandler)
                } else {
                    errorHandler.setImageError()
                    viewModel!!.setError(errorHandler)
                }
                return false
            }
        }

    fun setupRecycleErrorParams(assetManager: AssetManager?) {
        prepareForLoading()
        progressBar.visibility = View.GONE
        description.setText(R.string.no_internet)
        author.text = ":("
    }

    private fun prepareForLoading() {
        if (viewModel != null) viewModel!!.setIsGifLoaded(false)
        image.setImageResource(R.color.disabled_btn)
    }

    fun loadImage(url: String?) {
        prepareForLoading()
        Glide.with(context)
            .asGif()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(requestListener)
            .placeholder(R.drawable.waiting_background)
            .into(image)
    }

    fun setViewModel(viewModel: GifViewModel) {
        this.viewModel = viewModel
        viewModel.getIsCurrentGifLoaded().observe(context as LifecycleOwner) { isLoaded ->
            if (isLoaded) progressBar.visibility = View.GONE else progressBar.visibility =
                View.VISIBLE
        }
        viewModel.error.observe(context as LifecycleOwner) { error ->
            if (error.currentError == ErrorHandler.success()
            ) if (error.currentError == ErrorHandler.imageError()
            ) setupRecycleErrorParams(
                assetManager
            ) else linearLayoutCompat.visibility = View.VISIBLE
        }
    }

    companion object {
        private var errorHandler: ErrorHandler = ErrorHandler()
    }

    init {
        assetManager = context.assets
        image = itemView.findViewById(R.id.load_image)
        description = itemView.findViewById(R.id.load_description)
        author = itemView.findViewById(R.id.load_author)
        progressBar = itemView.findViewById(R.id.load_progressbar)
        linearLayoutCompat = itemView.findViewById(R.id.load_linear_layout)
    }
}