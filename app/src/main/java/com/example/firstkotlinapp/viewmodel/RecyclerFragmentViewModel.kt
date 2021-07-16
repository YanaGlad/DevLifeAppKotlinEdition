package com.example.firstkotlinapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firstkotlinapp.api.Api
import com.example.firstkotlinapp.api.Instance
import com.example.firstkotlinapp.models.Gif
import com.example.firstkotlinapp.models.GifModel
import com.example.firstkotlinapp.models.Gifs
import com.example.firstkotlinapp.values.ErrorHandler
import com.example.firstkotlinapp.values.PageOperation
import retrofit2.Call
import retrofit2.Response
import java.util.*

class RecyclerFragmentViewModel : PageViewModel() {
    val type = MutableLiveData<String?>(null)
    val currentPage = MutableLiveData(0)
        get() = field

    var error: MutableLiveData<ErrorHandler> = MutableLiveData<ErrorHandler>(ErrorHandler())

    suspend fun loadRecyclerGifs(type: String) {
        var response: Response<Gifs>? = null

        when (type) {
            "latest" -> {
                response = currentPage.value?.let {
                    Instance.getInstance().create(Api::class.java).getLatestGifs(
                        it, 10, "gif"
                    )
                }
            }
            "top" -> {
                response = currentPage.value?.let {
                    Instance.getInstance().create(Api::class.java).getTopGifs(
                        it, 10, "gif"
                    )
                }
            }
        }


        if (response?.isSuccessful == true) {
            setCanLoadNext(true)
            if (response.body() != null) response.body()!!.gifs?.let {
                createListOfGifModels(it)
            }
        } else {
            val errorHandler = ErrorHandler()
            errorHandler.setLoadError()
            setError(errorHandler)
        }


    }

    fun setError(error: ErrorHandler?) {
        this.error.value = error
    }

    private val gifModels: MutableLiveData<ArrayList<GifModel>?> =
        MutableLiveData<ArrayList<GifModel>?>(null)

    override fun setCanLoadNext(canLoadNext: Boolean) {
        super.setCanLoadNext(canLoadNext)
    }

    fun updateCanLoadPrevious() {
        super.setCanLoadPrevious(
            currentPage.value!! > 0 &&
                    error.getValue()!!.currentError == ErrorHandler.success()
        )
    }

    override fun getCanLoadNext(): LiveData<Boolean> {
        return super.getCanLoadNext()
    }

    override fun getCanLoadPrevious(): LiveData<Boolean> {
        return super.getCanLoadPrevious()
    }

    fun setType(type: String?) {
        this.type.value = type
    }

    fun setCurrentPage(currentPage: Int, pageOperation: PageOperation) {
        this.currentPage.value = currentPage + pageOperation.pos
    }

    fun createListOfGifModels(gifs: ArrayList<Gif>) {
        val result: ArrayList<GifModel> = ArrayList<GifModel>()
        for (gif in gifs) result.add(gif.createGifModel()!!)
        setGifModels(result)
    }

    fun getGifModels(): MutableLiveData<ArrayList<GifModel>?> {
        return gifModels
    }

    private fun setGifModels(gifModels: ArrayList<GifModel>) {
        this.gifModels.value = gifModels
    }
}