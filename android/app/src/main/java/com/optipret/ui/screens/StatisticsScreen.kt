@file:OptIn(androidx.compose.material.ExperimentalMaterialApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.optipret.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.optipret.ui.components.BarChart
import com.optipret.ui.components.ErrorState
import com.optipret.ui.components.PieChart
import com.optipret.ui.viewmodel.StatisticsViewModel

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh(force = true) }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Analyse de votre portefeuille",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val selectedIndex = uiState.chartModeOptions.indexOf(uiState.chartMode).coerceAtLeast(0)

            TabRow(
                selectedTabIndex = selectedIndex,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF111827),
                indicator = { tabPositions ->
                    if (selectedIndex in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                            color = Color(0xFF2563EB),
                            height = 2.dp
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                }
            ) {
                uiState.chartModeOptions.forEachIndexed { index, option ->
                    val isSelected = selectedIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectChartMode(option) },
                        text = {
                            val label = when(option.label) {
                                "Histogramme" -> "Répartition"
                                "Circulaire" -> "Proportions"
                                else -> option.label
                            }
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFF6B7280),
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.chartMode.label == "Histogramme") {
                BarChart(
                    items = uiState.barItems,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    showLegend = false
                )
            } else {
                PieChart(
                    items = uiState.pieItems,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    showLegend = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LegendStatCard(
                total = uiState.total,
                max = uiState.max,
                min = uiState.min
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                ErrorState(
                    message = uiState.errorMessage ?: "Une erreur est survenue lors du chargement des analyses.",
                    onAction = { viewModel.refresh(force = true) }
                )
            }
        }

        PullRefreshIndicator(
            refreshing = uiState.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = Color(0xFF2563EB)
        )
    }
}

@Composable
private fun LegendStatCard(total: Double, max: Double, min: Double) {
    val totalColor = Color(0xFF2563EB)
    val maxColor = Color(0xFF10B981)
    val minColor = Color(0xFFEF4444)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendStatRow(label = "Encours total", value = total, color = totalColor)
            LegendStatRow(label = "Engagement maximal", value = max, color = maxColor)
            LegendStatRow(label = "Engagement minimal", value = min, color = minColor)
        }
    }
}

@Composable
private fun LegendStatRow(label: String, value: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = color,
                shape = CircleShape,
                modifier = Modifier.size(10.dp)
            ) {}
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
        }
        Text(
            text = String.format(java.util.Locale.FRANCE, "%,.2f Ar", value),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )
    }
}
