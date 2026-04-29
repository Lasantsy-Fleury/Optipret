package com.optipret.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.optipret.data.model.Loan
import com.optipret.repository.LoanRepository
import com.optipret.ui.state.LoanUiState
import com.optipret.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoanViewModel(
  private val repository: LoanRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<LoanUiState>(LoanUiState.Loading)
  val uiState: StateFlow<LoanUiState> = _uiState
  private val _message = MutableStateFlow<String?>(null)
  val message: StateFlow<String?> = _message
  private var cachedLoans: List<Loan> = emptyList()

  fun fetchLoans() {
    viewModelScope.launch {
      val requestBaseUrl = RetrofitInstance.getBaseUrl()
      _uiState.value = LoanUiState.Loading
      try {
        val response = repository.getLoans()
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        if (response.isSuccessful) {
          ConnectionStatusStore.update(ConnectionStatus.Connected)
          cachedLoans = response.body().orEmpty()
          _uiState.value = LoanUiState.Success(cachedLoans)
        } else {
          ConnectionStatusStore.update(ConnectionStatus.Disconnected)
          _uiState.value = LoanUiState.Error("Impossible de charger les prets")
        }
      } catch (error: Exception) {
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        ConnectionStatusStore.update(ConnectionStatus.Disconnected)
        _uiState.value = LoanUiState.Error("Erreur reseau")
      }
    }
  }

  fun refresh() {
    fetchLoans()
  }

  fun addLoan(loan: Loan) {
    viewModelScope.launch {
      val requestBaseUrl = RetrofitInstance.getBaseUrl()
      _uiState.value = LoanUiState.Loading
      try {
        val response = repository.createLoan(loan)
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        if (response.isSuccessful) {
          ConnectionStatusStore.update(ConnectionStatus.Connected)
          fetchLoans()
        } else {
          ConnectionStatusStore.update(ConnectionStatus.Disconnected)
          _uiState.value = LoanUiState.Error("Impossible d'ajouter le pret")
        }
      } catch (error: Exception) {
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        ConnectionStatusStore.update(ConnectionStatus.Disconnected)
        _uiState.value = LoanUiState.Error("Erreur reseau")
      }
    }
  }

  fun updateLoan(loan: Loan) {
    viewModelScope.launch {
      val requestBaseUrl = RetrofitInstance.getBaseUrl()
      _uiState.value = LoanUiState.Loading
      try {
        val response = repository.updateLoan(loan.numCompte, loan)
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        if (response.isSuccessful) {
          ConnectionStatusStore.update(ConnectionStatus.Connected)
          fetchLoans()
        } else {
          ConnectionStatusStore.update(ConnectionStatus.Disconnected)
          _message.value = "Impossible de modifier le pret"
          fetchLoans()
        }
      } catch (error: Exception) {
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        ConnectionStatusStore.update(ConnectionStatus.Disconnected)
        _message.value = "Impossible de modifier le pret"
        fetchLoans()
      }
    }
  }

  fun deleteLoan(loan: Loan) {
    viewModelScope.launch {
      val requestBaseUrl = RetrofitInstance.getBaseUrl()
      _uiState.value = LoanUiState.Loading
      try {
        val response = repository.deleteLoan(loan.numCompte)
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        if (response.isSuccessful) {
          ConnectionStatusStore.update(ConnectionStatus.Connected)
          fetchLoans()
        } else {
          ConnectionStatusStore.update(ConnectionStatus.Disconnected)
          _uiState.value = LoanUiState.Error("Impossible de supprimer le pret")
        }
      } catch (error: Exception) {
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        ConnectionStatusStore.update(ConnectionStatus.Disconnected)
        _uiState.value = LoanUiState.Error("Erreur reseau")
      }
    }
  }

  fun clearMessage() {
    _message.value = null
  }

  companion object {
    fun provideFactory(repository: LoanRepository): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          if (modelClass.isAssignableFrom(LoanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoanViewModel(repository) as T
          }
          throw IllegalArgumentException("Unknown ViewModel class")
        }
      }
    }
  }
}
