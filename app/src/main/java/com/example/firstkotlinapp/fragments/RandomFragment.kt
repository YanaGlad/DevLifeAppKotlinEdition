package com.example.firstkotlinapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.firstkotlinapp.R
import com.example.firstkotlinapp.api.Api
import com.example.firstkotlinapp.api.Instance
import com.example.firstkotlinapp.models.Gif
import com.example.firstkotlinapp.models.GifModel
import com.example.firstkotlinapp.values.ErrorHandler
import com.example.firstkotlinapp.viewmodel.RandomFragmentViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class RandomFragment : ButtonSupportedFragment() {
    var isOnScreen = false
        set(value) {
            field = value
        }
    private val randomFragmentViewModel: RandomFragmentViewModel by viewModels()
    private lateinit var image: AppCompatImageView
    private lateinit var toolbar: LinearLayoutCompat
    private lateinit var title: AppCompatTextView
    private lateinit var subtitle: AppCompatTextView
    private lateinit var savedUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        randomFragmentViewModel.viewModelScope.launch {
            randomFragmentViewModel.loadRandomGif()
            loadGifWithGlide(randomFragmentViewModel.getCurrentGif().value?.gifURL)
        }

        onPrevClickListener = View.OnClickListener {
            if (!randomFragmentViewModel.goBack()) Log.e(
                "Cache is empty",
                "No cached gifs"
            ) else loadGifWithGlide(
                randomFragmentViewModel.getCurrentGif().value?.gifURL
            )

            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        onNextClickListener = View.OnClickListener { loadGif() }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_random, container, false)
        image = view.findViewById(R.id.load_image)
        toolbar = view.findViewById(R.id.load_linear_layout)
        title = view.findViewById(R.id.load_description)
        subtitle = view.findViewById(R.id.load_author)
        val loadProgress = view.findViewById<ProgressBar>(R.id.load_progressbar)
        val errorButton = view.findViewById<Button>(R.id.recycl_error_btn)
        val errorProgressBar = view.findViewById<ProgressBar>(R.id.recycle_error_progressbar)
        btnPrev = requireActivity().findViewById(R.id.btn_previous)
        btnNex = requireActivity().findViewById(R.id.btn_next)
        btnPrev?.setOnClickListener(onPrevClickListener)
        btnNex?.setOnClickListener(onNextClickListener)
        title.text = ""
        subtitle.text = ""
        randomFragmentViewModel.getCurrentGif().observe(viewLifecycleOwner) { gif: GifModel? ->
            if (gif != null) {
                title.text = gif.description
                subtitle.text = view.context.getString(R.string.by) + gif.author
            }
        }
        randomFragmentViewModel.getIsCurrentGifLoaded().observe(
            viewLifecycleOwner
        ) { isLoaded ->
            if (isLoaded) loadProgress.visibility = View.INVISIBLE else loadProgress.visibility =
                View.VISIBLE
        }
        randomFragmentViewModel.getCanLoadNext()
            .observe(viewLifecycleOwner) { enabled: Boolean? ->
                if (isOnScreen) btnNex?.isEnabled = enabled!!
            }
        randomFragmentViewModel.getCanLoadPrevious().observe(viewLifecycleOwner) { enabled ->
            if (isOnScreen) btnPrev?.isEnabled = enabled
        }
        randomFragmentViewModel.getError().observe(viewLifecycleOwner) { e ->
            randomFragmentViewModel.updateCanLoadPrevious()
            errorProgressBar.visibility = View.INVISIBLE
            if (e.currentError != ErrorHandler.success()) {
                toolbar.visibility = View.GONE
                when (e.currentError) {
                    "LOAD_ERROR" -> errorButton.visibility = View.VISIBLE
                    "IMAGE_ERROR" -> {
                        errorButton.visibility = View.VISIBLE
                        setupErrorParams(context, requireContext().assets)
                        randomFragmentViewModel.setCanLoadNext(false)
                    }
                }
                errorButton.setOnClickListener { v: View? ->
                    if (errorProgressBar.visibility == View.INVISIBLE) {
                        errorProgressBar.visibility = View.VISIBLE
                        if (e.currentError == ErrorHandler.imageError()) {
                            loadGifWithGlide(savedUrl)
                        }
                    }
                }
            } else {
                errorButton.visibility = View.GONE
                errorProgressBar.visibility = View.GONE
                toolbar.visibility = View.VISIBLE
            }
        }
        return view
    }

    private fun loadGifWithGlide(url: String?) {
        randomFragmentViewModel.setIsCurrentGifLoaded(false)
        randomFragmentViewModel.setCanLoadNext(false)
        Glide.with(requireActivity())
            .asGif()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.waiting_background)
            .listener(object : RequestListener<GifDrawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<GifDrawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    errorHandler.setImageError()
                    savedUrl = model as String
                    randomFragmentViewModel.setAppError(errorHandler)
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any,
                    target: Target<GifDrawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (randomFragmentViewModel.getError()
                            .value?.currentError == ErrorHandler.imageError()
                    ) {
                        errorHandler.setSuccess()
                        randomFragmentViewModel.setAppError(errorHandler)
                    }
                    randomFragmentViewModel.setIsCurrentGifLoaded(true)
                    randomFragmentViewModel.setCanLoadNext(true)
                    return false
                }
            })
            .into(image)
    }

    private fun setupErrorParams(context: Context?, assetManager: AssetManager?) {
        image.setImageResource(R.color.disabled_btn)

//        Glide.with(context)
//                .load(Support.loadBitmapImage(assetManager, "devnull.png"))
//                .into(image);
        title.setText(R.string.no_internet)
        subtitle.text = ":("
    }

    private fun loadGif() {
        if (!randomFragmentViewModel.goNext()) {
            randomFragmentViewModel.setCanLoadNext(false)

            randomFragmentViewModel.viewModelScope.launch {
                randomFragmentViewModel.loadRandomGif()
                loadGifWithGlide(randomFragmentViewModel.getCurrentGif().value?.gifURL)
            }

        } else loadGifWithGlide(
            randomFragmentViewModel.getCurrentGif().value?.gifURL
        )
    }

    override fun nextEnabled(): Boolean {
        return randomFragmentViewModel.getCanLoadNext().value!!
    }

    override fun previousEnabled(): Boolean {
        return randomFragmentViewModel.getCanLoadPrevious().value!!
    }

    companion object {
        internal val errorHandler: ErrorHandler = ErrorHandler()
    }
}