package com.example.firstkotlinapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel : ViewModel() {
    private val canLoadPrevious = MutableLiveData(false)
    private val canLoadNext = MutableLiveData(false)

    fun getCanLoadPrevious(): LiveData<Boolean> {
        return canLoadPrevious
    }

    fun setCanLoadPrevious(canLoadPrevious: Boolean) {
        this.canLoadPrevious.value = canLoadPrevious
    }

    fun getCanLoadNext(): LiveData<Boolean> {
        return canLoadNext
    }

    fun setCanLoadNext(canLoadNext: Boolean) {
        this.canLoadNext.value = canLoadNext
    }
}

