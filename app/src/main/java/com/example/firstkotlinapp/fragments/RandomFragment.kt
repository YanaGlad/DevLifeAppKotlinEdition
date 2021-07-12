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
import androidx.lifecycle.ViewModelProvider
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
import com.example.firstkotlinapp.values.ErrorHandler
import com.example.firstkotlinapp.viewmodel.RandomFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class RandomFragment : ButtonSupportedFragment() {
    var isOnScreen = false
    set(value) {
        field = value
    }
    private var randomFragmentViewModel: RandomFragmentViewModel? = null
    private var image: AppCompatImageView? = null
    private var toolbar: LinearLayoutCompat? = null
    private var title: AppCompatTextView? = null
    private var subtitle: AppCompatTextView? = null
    private var savedUrl: String? = null
    private val gifCallback: Callback<Gif?> = object : Callback<Gif?> {
        override fun onResponse(call: Call<Gif?>, response: Response<Gif?>) {
            if (response.isSuccessful()) {
                if (randomFragmentViewModel?.getError()
                    ?.getValue()!!.currentError == ErrorHandler.loadError()
                )
                    errorHandler.setSuccess()
                randomFragmentViewModel?.setAppError(errorHandler)
                if (response.body() != null) {
                    response.body()!!.createGifModel()
                        ?.let { randomFragmentViewModel?.addGifModel(it) }
                    loadGifWithGlide(response.body()!!.createGifModel()?.gifURL)
                }
            } else {
                errorHandler.setLoadError()
                randomFragmentViewModel?.setAppError(errorHandler)
                Log.e("Callback error", "Can't load post")
            }
        }

        override fun onFailure(call: Call<Gif?>, t: Throwable) {
            errorHandler.setLoadError()
            randomFragmentViewModel?.setAppError(errorHandler)
            Log.e("Callback error", "onFailure: $t")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        randomFragmentViewModel = ViewModelProvider(this).get<RandomFragmentViewModel>(
            RandomFragmentViewModel::class.java
        )
        val api: Api = Instance.getInstance().create(Api::class.java)
        api.getRandomGif()?.enqueue(gifCallback)

        onPrevClickListener = View.OnClickListener {
            if (!randomFragmentViewModel!!.goBack()) Log.e(
                "Cache is empty",
                "No cached gifs"
            ) else loadGifWithGlide(
                randomFragmentViewModel!!.getCurrentGif().value?.gifURL
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
        title?.setText("")
        subtitle?.setText("")
        randomFragmentViewModel?.getCurrentGif()?.observe(viewLifecycleOwner) { gif ->
            if (gif != null) {
                title?.setText(gif.description)
                subtitle?.setText(view.context.getString(R.string.by) + gif.author)
            }
        }
        randomFragmentViewModel?.getIsCurrentGifLoaded()?.observe(
            viewLifecycleOwner
        ) { isLoaded ->
            if (isLoaded) loadProgress.visibility = View.INVISIBLE else loadProgress.visibility =
                View.VISIBLE
        }
        randomFragmentViewModel?.getCanLoadNext()
            ?.observe(viewLifecycleOwner) { enabled: Boolean? ->
                if (isOnScreen) btnNex?.setEnabled(
                    enabled!!
                )
            }
        randomFragmentViewModel?.getCanLoadPrevious()?.observe(viewLifecycleOwner) { enabled ->
            if (isOnScreen) btnPrev?.setEnabled(
                enabled
            )
        }
        randomFragmentViewModel?.getError()?.observe(viewLifecycleOwner) { e ->
            randomFragmentViewModel?.updateCanLoadPrevious()
            errorProgressBar.visibility = View.INVISIBLE
            if (!(e.currentError == ErrorHandler.success())) {
                toolbar?.setVisibility(View.GONE)
                when (e.currentError) {
                    "LOAD_ERROR" -> errorButton.visibility = View.VISIBLE
                    "IMAGE_ERROR" -> {
                        errorButton.visibility = View.VISIBLE
                        setupErrorParams(context, requireContext().assets)
                        randomFragmentViewModel?.setCanLoadNext(false)
                    }
                }
                errorButton.setOnClickListener { v: View? ->
                    if (errorProgressBar.visibility == View.INVISIBLE) {
                        errorProgressBar.visibility = View.VISIBLE
                        if (e.currentError.equals(ErrorHandler.imageError())) {
                            loadGifWithGlide(savedUrl)
                        }
                    }
                }
            } else {
                errorButton.visibility = View.GONE
                errorProgressBar.visibility = View.GONE
                toolbar?.setVisibility(View.VISIBLE)
            }
        }
        return view
    }

    private fun loadGifWithGlide(url: String?) {
        randomFragmentViewModel?.setIsCurrentGifLoaded(false)
        randomFragmentViewModel?.setCanLoadNext(false)
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
                    randomFragmentViewModel?.setAppError(errorHandler)
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any,
                    target: Target<GifDrawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (randomFragmentViewModel?.getError()
                        ?.getValue()?.currentError == ErrorHandler.imageError()
                    ) {
                        errorHandler.setSuccess()
                        randomFragmentViewModel?.setAppError(errorHandler)
                    }
                    randomFragmentViewModel?.setIsCurrentGifLoaded(true)
                    randomFragmentViewModel?.setCanLoadNext(true)
                    return false
                }
            })
            .into(image!!)
    }

    fun setupErrorParams(context: Context?, assetManager: AssetManager?) {
        image!!.setImageResource(R.color.disabled_btn)

//        Glide.with(context)
//                .load(Support.loadBitmapImage(assetManager, "devnull.png"))
//                .into(image);
        title!!.setText(R.string.no_internet)
        subtitle!!.text = ":("
    }

    private fun loadGif() {
        if (!randomFragmentViewModel?.goNext()!!) {
            randomFragmentViewModel?.setCanLoadNext(false)
            val api: Api = Instance.getInstance().create(Api::class.java)
            api.getRandomGif()!!.enqueue(gifCallback)
        } else loadGifWithGlide(
            randomFragmentViewModel?.getCurrentGif()?.getValue()?.gifURL
        )
    }

    override fun nextEnabled(): Boolean {
        return randomFragmentViewModel?.getCanLoadNext()?.getValue()!!
    }

    override fun previousEnabled(): Boolean {
        return randomFragmentViewModel?.getCanLoadPrevious()?.getValue()!!
    }

    companion object {
        private val errorHandler: ErrorHandler = ErrorHandler()
    }
}