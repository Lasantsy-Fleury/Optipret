package com.optipret.network

import com.optipret.data.model.Loan
import com.optipret.data.model.LoanStats
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
  @GET("/api/loans")
  suspend fun getLoans(): Response<List<Loan>>

  @GET("/api/loans/{id}")
  suspend fun getLoanById(@Path("id") id: String): Response<Loan>

  @POST("/api/loans")
  suspend fun createLoan(@Body loan: Loan): Response<Loan>

  @PUT("/api/loans/{id}")
  suspend fun updateLoan(@Path("id") id: String, @Body loan: Loan): Response<Loan>

  @DELETE("/api/loans/{id}")
  suspend fun deleteLoan(@Path("id") id: String): Response<Unit>

  @GET("/api/loans/stats")
  suspend fun getLoanStats(): Response<LoanStats>
}
