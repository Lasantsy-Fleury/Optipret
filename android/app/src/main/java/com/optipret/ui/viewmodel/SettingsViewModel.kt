package com.optipret.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.optipret.data.local.DataStoreManager
import com.optipret.network.RetrofitInstance
import com.optipret.utils.CurrencyOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
  private val dataStore: DataStoreManager
) : ViewModel() {

  private val _uiState = MutableStateFlow(SettingsUiState())
  val uiState: StateFlow<SettingsUiState> = _uiState

  init {
    viewModelScope.launch {
      combine(
        dataStore.apiUrlFlow,
        dataStore.currencyFlow,
        dataStore.offlineModeFlow
      ) { apiUrl, currency, offline ->
        Triple(apiUrl, currency, offline)
      }.collect { (apiUrl, currency, offline) ->
        val option = CurrencyOption.values().firstOrNull { it.name == currency } ?: CurrencyOption.ARIARY
        _uiState.update {
          it.copy(
            apiUrl = apiUrl,
            selectedCurrency = option,
            offlineMode = offline
          )
        }
        RetrofitInstance.updateBaseUrl(apiUrl)
      }
    }

    viewModelScope.launch {
      ConnectionStatusStore.status.collect { status ->
        _uiState.update { it.copy(connectionStatus = status) }
      }
    }
  }

  fun onApiUrlChange(value: String) {
    _uiState.update { it.copy(apiUrl = value) }
  }

  fun saveApiUrl() {
    val value = _uiState.value.apiUrl.trim()
    viewModelScope.launch {
      dataStore.setApiUrl(value)
      RetrofitInstance.updateBaseUrl(value)
    }
  }

  fun selectCurrency(option: CurrencyOption) {
    _uiState.update { it.copy(selectedCurrency = option) }
    viewModelScope.launch { dataStore.setCurrency(option.name) }
  }

  fun setOfflineMode(enabled: Boolean) {
    _uiState.update { it.copy(offlineMode = enabled) }
    viewModelScope.launch { dataStore.setOfflineMode(enabled) }
  }

  fun testConnection() {
    viewModelScope.launch {
      ConnectionStatusStore.update(ConnectionStatus.Testing)
      try {
        val response = RetrofitInstance.getApiService().getLoans()
        val status = if (response.isSuccessful) ConnectionStatus.Connected else ConnectionStatus.Disconnected
        ConnectionStatusStore.update(status)
      } catch (error: Exception) {
        ConnectionStatusStore.update(ConnectionStatus.Disconnected)
      }
    }
  }

  fun clearCache() {
    _uiState.update { it.copy(snackbarMessage = "Cache vide") }
  }

  fun clearMessage() {
    _uiState.update { it.copy(snackbarMessage = null) }
  }

  companion object {
    fun provideFactory(dataStore: DataStoreManager): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(dataStore) as T
          }
          throw IllegalArgumentException("Unknown ViewModel class")
        }
      }
    }
  }
}

data class SettingsUiState(
  val apiUrl: String = "http://10.0.2.2:3000",
  val selectedCurrency: CurrencyOption = CurrencyOption.ARIARY,
  val offlineMode: Boolean = false,
  val connectionStatus: ConnectionStatus = ConnectionStatus.Unknown,
  val snackbarMessage: String? = null
)
