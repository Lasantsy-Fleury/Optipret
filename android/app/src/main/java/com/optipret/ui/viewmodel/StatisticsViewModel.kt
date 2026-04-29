package com.optipret.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.optipret.data.model.Loan
import com.optipret.data.model.LoanStats
import com.optipret.repository.LoanRepository
import com.optipret.ui.components.StatBarItem
import com.optipret.ui.components.StatPieItem
import com.optipret.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(
  private val repository: LoanRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(StatisticsUiState())
  val uiState: StateFlow<StatisticsUiState> = _uiState

  private var lastStatsFetchMillis: Long = 0L
  private var cachedStats: LoanStats? = null

  init {
    refresh(force = true)
  }

  fun refresh(force: Boolean = false) {
    viewModelScope.launch {
      val requestBaseUrl = RetrofitInstance.getBaseUrl()
      val now = System.currentTimeMillis()
      val canFetchStats = force || cachedStats == null || now - lastStatsFetchMillis >= 60_000

      _uiState.update { it.copy(isLoading = canFetchStats, errorMessage = null) }
      try {
        val loansResponse = repository.getLoans()
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        val loans = loansResponse.body().orEmpty()

        if (canFetchStats) {
          val statsResponse = repository.getLoanStats()
          if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
          val stats = statsResponse.body()

          if (!statsResponse.isSuccessful || stats == null) {
            ConnectionStatusStore.update(ConnectionStatus.Disconnected)
            _uiState.update { it.copy(isLoading = false, errorMessage = "Impossible de charger les statistiques") }
            return@launch
          }

          cachedStats = stats
          lastStatsFetchMillis = now
        }

        val stats = cachedStats ?: LoanStats(total = 0.0, min = 0.0, max = 0.0)

        _uiState.update {
          it.copy(
            isLoading = false,
            total = stats.total,
            min = stats.min,
            max = stats.max,
            barItems = buildBarItems(stats),
            pieItems = buildPieItems(stats),
            loans = loans
          )
        }
        ConnectionStatusStore.update(ConnectionStatus.Connected)
      } catch (error: Exception) {
        if (requestBaseUrl != RetrofitInstance.getBaseUrl()) return@launch
        ConnectionStatusStore.update(ConnectionStatus.Disconnected)
        _uiState.update { it.copy(isLoading = false, errorMessage = "Erreur reseau") }
      }
    }
  }

  fun selectChartMode(mode: ChartMode) {
    _uiState.update { it.copy(chartMode = mode) }
  }

  private fun buildBarItems(stats: LoanStats): List<StatBarItem> {
    return listOf(
      StatBarItem(label = "Total", value = stats.total, color = 0xFF5B8FF9),
      StatBarItem(label = "Max", value = stats.max, color = 0xFF6FCF97),
      StatBarItem(label = "Min", value = stats.min, color = 0xFFE57373)
    )
  }

  private fun buildPieItems(stats: LoanStats): List<StatPieItem> {
    return listOf(
      StatPieItem(label = "Total", value = stats.total, color = 0xFF5B8FF9),
      StatPieItem(label = "Max", value = stats.max, color = 0xFF6FCF97),
      StatPieItem(label = "Min", value = stats.min, color = 0xFFE57373)
    )
  }

  companion object {
    fun provideFactory(repository: LoanRepository): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
          }
          throw IllegalArgumentException("Unknown ViewModel class")
        }
      }
    }
  }
}

data class StatisticsUiState(
  val isLoading: Boolean = false,
  val errorMessage: String? = null,
  val total: Double = 0.0,
  val min: Double = 0.0,
  val max: Double = 0.0,
  val chartMode: ChartMode = ChartMode.Bar,
  val chartModeOptions: List<ChartMode> = listOf(ChartMode.Bar, ChartMode.Pie),
  val barItems: List<StatBarItem> = emptyList(),
  val pieItems: List<StatPieItem> = emptyList(),
  val loans: List<Loan> = emptyList()
)

enum class ChartMode(val label: String) {
  Bar("Histogramme"),
  Pie("Camembert")
}
