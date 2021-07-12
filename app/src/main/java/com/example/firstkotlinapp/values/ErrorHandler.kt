package com.example.firstkotlinapp.values

class ErrorHandler {
    var currentError = type_error[0]
        get() = field

    fun setSuccess() {
        currentError = type_error[0]
    }

    fun setLoadError() {
        currentError = type_error[1]
    }

    fun setImageError() {
        currentError = type_error[2]
    }

    companion object {
        private val type_error = arrayOf(
            "SUCCESS",
            "LOAD_ERROR",
            "IMAGE_ERROR"
        )

        fun success(): String {
            return type_error[0]
        }

        fun loadError(): String {
            return type_error[1]
        }

        fun imageError(): String {
            return type_error[2]
        }
    }
}