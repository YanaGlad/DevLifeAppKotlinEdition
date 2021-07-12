package com.example.firstkotlinapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firstkotlinapp.models.GifModel
import com.example.firstkotlinapp.values.ErrorHandler
import java.util.*

class RandomFragmentViewModel : PageViewModel() {
    private val isCurrentGifLoaded = MutableLiveData(false)
    private val currentGif: MutableLiveData<GifModel?> = MutableLiveData(null)
    private val error: MutableLiveData<ErrorHandler> = MutableLiveData<ErrorHandler>(ErrorHandler())
    private val gifModels: MutableList<GifModel> = ArrayList<GifModel>()
    fun getError(): MutableLiveData<ErrorHandler> {
        return error
    }

    fun setAppError(error: ErrorHandler) {
        this.error.setValue(error)
    }

    fun getIsCurrentGifLoaded(): LiveData<Boolean> {
        return isCurrentGifLoaded
    }

    fun setIsCurrentGifLoaded(isCurrentGifLoaded: Boolean) {
        this.isCurrentGifLoaded.value = isCurrentGifLoaded
    }

    fun updateCanLoadPrevious() {
        val hasErrors: Boolean = error.getValue()!!.currentError == ErrorHandler.success()

        super.setCanLoadPrevious(!hasErrors && pos - 1 >= 0)
    }

    fun getCurrentGif(): LiveData<GifModel?> {
        return currentGif
    }

    fun addGifModel(gifModel: GifModel) {
        Log.d("Add", "POS " + pos)
        currentGif.setValue(gifModel)
        gifModels.add(gifModel)
        pos++
        cachedCurrentGif = gifModels[gifModels.size - 1]
        updateCanLoadPrevious()
    }

    fun goNext(): Boolean {
        Log.d("Next", "POS " + pos)
        if (cachedCurrentGif != null && pos < gifModels.size - 1) {
            pos++
            cachedCurrentGif = gifModels[pos]
            currentGif.setValue(cachedCurrentGif)
            updateCanLoadPrevious()
            return true
        }
        updateCanLoadPrevious()
        return false
    }

    fun goBack(): Boolean {
        Log.d("Prev", "POS " + pos)
        if (pos - 1 >= 0) {
            pos--
            cachedCurrentGif = gifModels[pos]
            currentGif.setValue(cachedCurrentGif)
            updateCanLoadPrevious()
            return true
        }
        updateCanLoadPrevious()
        return false
    }

    companion object {
        private var cachedCurrentGif: GifModel? = null
        private var pos = -1
    }
}