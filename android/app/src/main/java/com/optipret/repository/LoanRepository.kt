package com.optipret.repository

import com.optipret.data.model.Loan
import com.optipret.data.model.LoanStats
import com.optipret.network.RetrofitInstance

class LoanRepository {
  private fun api() = RetrofitInstance.getApiService()

  suspend fun getLoans() = api().getLoans()

  suspend fun getLoanById(id: String) = api().getLoanById(id)

  suspend fun createLoan(loan: Loan) = api().createLoan(loan)

  suspend fun updateLoan(id: String, loan: Loan) = api().updateLoan(id, loan)

  suspend fun deleteLoan(id: String) = api().deleteLoan(id)

  suspend fun getLoanStats(): retrofit2.Response<LoanStats> = api().getLoanStats()
}
