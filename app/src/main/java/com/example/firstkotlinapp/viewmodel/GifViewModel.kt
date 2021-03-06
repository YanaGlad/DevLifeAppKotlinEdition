package com.example.firstkotlinapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firstkotlinapp.values.ErrorHandler

class GifViewModel : ViewModel() {
    private val isCurrentGifLoaded = MutableLiveData(false)
    var error: MutableLiveData<ErrorHandler> = MutableLiveData<ErrorHandler>(ErrorHandler())

    fun setError(handler: ErrorHandler) {
        error.value = handler
    }

    fun getIsCurrentGifLoaded(): LiveData<Boolean> {
        return isCurrentGifLoaded
    }

    fun setIsGifLoaded(isCurrentGifLoaded: Boolean) {
        this.isCurrentGifLoaded.value = isCurrentGifLoaded
    }
}
