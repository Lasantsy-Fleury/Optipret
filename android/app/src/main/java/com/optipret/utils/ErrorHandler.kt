package com.optipret.utils

import retrofit2.HttpException
import java.io.IOException

object ErrorHandler {
  fun toMessage(throwable: Throwable): String {
    return when (throwable) {
      is HttpException -> {
        val code = throwable.code()
        "HTTP error $code"
      }
      is IOException -> "Network error. Check your connection."
      else -> "Unexpected error."
    }
  }
}
