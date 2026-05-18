package com.optipret.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
  private const val DEFAULT_BASE_URL = "https://optipret-backend.onrender.com/"
  private const val TIMEOUT_SECONDS = 30L

  @Volatile
  private var baseUrl: String = DEFAULT_BASE_URL

  private val gson: Gson = GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    .create()

  private val httpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .build()

  @Volatile
  private var retrofit: Retrofit = buildRetrofit(baseUrl)

  private fun buildRetrofit(url: String): Retrofit {
    return Retrofit.Builder()
      .baseUrl(url)
      .client(httpClient)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }

  fun getApiService(): ApiService = retrofit.create(ApiService::class.java)

  fun updateBaseUrl(newUrl: String) {
    val normalized = normalizeBaseUrl(newUrl)
    if (normalized != baseUrl) {
      baseUrl = normalized
      retrofit = buildRetrofit(baseUrl)
    }
  }

  fun getBaseUrl(): String = baseUrl

  private fun normalizeBaseUrl(url: String): String {
    val trimmed = url.trim()
    if (trimmed.isEmpty()) return DEFAULT_BASE_URL

    val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
      trimmed
    } else {
      "http://$trimmed"
    }

    return if (withScheme.endsWith("/")) withScheme else "$withScheme/"
  }
}
