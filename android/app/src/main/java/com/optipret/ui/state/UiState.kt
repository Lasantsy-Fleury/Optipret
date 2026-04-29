package com.optipret.ui.state

import com.optipret.data.model.Loan

sealed class LoanUiState {
  data object Loading : LoanUiState()
  data class Success(val loans: List<Loan>) : LoanUiState()
  data class Error(val message: String) : LoanUiState()
}
