@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.optipret.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.optipret.data.model.LoanStats
import com.optipret.ui.components.BottomNavBar
import com.optipret.ui.components.BottomTab
import com.optipret.ui.screens.AboutScreen
import com.optipret.ui.screens.LoanDetailScreen
import com.optipret.ui.screens.HomeScreen
import com.optipret.ui.screens.SettingsScreen
import com.optipret.ui.screens.StatisticsScreen
import com.optipret.ui.state.LoanUiState
import com.optipret.ui.viewmodel.LoanViewModel
import com.optipret.ui.viewmodel.SettingsViewModel
import com.optipret.ui.viewmodel.StatisticsViewModel

@Composable
fun NavigationHost(
  loanViewModel: LoanViewModel,
  statisticsViewModel: StatisticsViewModel,
  settingsViewModel: SettingsViewModel,
  modifier: Modifier = Modifier
) {
  val navController = rememberNavController()
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route ?: BottomTab.Loans.route
  val selectedTab = BottomTab.values().firstOrNull { it.route == currentRoute } ?: BottomTab.Loans
  val settingsState by settingsViewModel.uiState.collectAsState()
  val loanMessage by loanViewModel.message.collectAsState()
  val showBottomBar = BottomTab.values().any { it.route == currentRoute }
  val canNavigateBack = navController.previousBackStackEntry != null && !showBottomBar

  val navigateToTab: (BottomTab) -> Unit = { tab ->
    navController.navigate(tab.route) {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  LaunchedEffect(settingsState.apiUrl) {
    loanViewModel.fetchLoans()
    statisticsViewModel.refresh(force = true)
  }

  Scaffold(
    modifier = modifier,
    containerColor = Color(0xFFF9FAFB),
    topBar = {
      TopAppBar(
        title = {
          Text(
            titleForRoute(currentRoute),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          if (canNavigateBack) {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Retour"
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color(0xFFF9FAFB),
          titleContentColor = Color(0xFF111827)
        )
      )
    },
    bottomBar = {
      if (showBottomBar) {
        BottomNavBar(
          selectedTab = selectedTab,
          onTabSelected = navigateToTab
        )
      }
    }
  ) { paddingValues ->
    NavHost(
      navController = navController,
      startDestination = BottomTab.Loans.route,
      modifier = Modifier.padding(paddingValues)
    ) {
      composable(BottomTab.Loans.route) {
        val loanState by loanViewModel.uiState.collectAsState()
        val statsState by statisticsViewModel.uiState.collectAsState()

        val isLoading = loanState is LoanUiState.Loading
        val errorMessage = (loanState as? LoanUiState.Error)?.message
        val loans = (loanState as? LoanUiState.Success)?.loans.orEmpty()
        val stats = LoanStats(
          total = statsState.total,
          min = statsState.min,
          max = statsState.max
        )

        LaunchedEffect(loans) {
          statisticsViewModel.refresh(force = true)
        }

        HomeScreen(
          loans = loans,
          stats = stats,
          isLoading = isLoading,
          errorMessage = errorMessage,
          onAddLoan = { loanViewModel.addLoan(it) },
          onUpdateLoan = { loanViewModel.updateLoan(it) },
          onDeleteLoan = { loanViewModel.deleteLoan(it) },
          onOpenLoan = { loan ->
            navController.navigate(loanDetailRoute(loan.numCompte))
          },
          onOpenStats = { navigateToTab(BottomTab.Stats) },
          onRefresh = { loanViewModel.refresh() },
          errorPopupMessage = loanMessage,
          onErrorPopupDismiss = { loanViewModel.clearMessage() }
        )
      }
      composable(LoanDetailRoute) { entry ->
        val numCompte = entry.arguments?.getString("numCompte")
        val loanState by loanViewModel.uiState.collectAsState()
        val selectedLoan = (loanState as? LoanUiState.Success)
          ?.loans
          ?.firstOrNull { it.numCompte == numCompte }
        val detailLoading = loanState is LoanUiState.Loading
        val detailError = (loanState as? LoanUiState.Error)?.message

        if (selectedLoan == null) {
          androidx.compose.foundation.layout.Box(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Prêt introuvable",
              style = MaterialTheme.typography.bodyLarge,
              color = Color(0xFF6B7280)
            )
          }
        } else {
          LoanDetailScreen(
            loan = selectedLoan,
            onEdit = { loanViewModel.updateLoan(it) },
            onDelete = {
              loanViewModel.deleteLoan(it)
              navController.popBackStack()
            },
            onNavigateBack = { navController.popBackStack() },
            isLoading = detailLoading,
            errorMessage = detailError,
            errorPopupMessage = loanMessage,
            onErrorPopupDismiss = { loanViewModel.clearMessage() }
          )
        }
      }
      composable(BottomTab.Stats.route) {
        StatisticsScreen(viewModel = statisticsViewModel)
      }
      composable(BottomTab.Settings.route) {
        SettingsScreen(viewModel = settingsViewModel)
      }
      composable(BottomTab.About.route) {
        AboutScreen()
      }
    }
  }
}

private fun titleForRoute(route: String): String {
  return when (route) {
    BottomTab.Loans.route -> "Mes Prêts"
    LoanDetailRoute -> "Détails du prêt"
    BottomTab.Stats.route -> "Analyses"
    BottomTab.Settings.route -> "Paramètres"
    BottomTab.About.route -> "À propos"
    else -> "Optiprêt"
  }
}

private const val LoanDetailRoute = "loan-detail/{numCompte}"

private fun loanDetailRoute(numCompte: String): String {
  return "loan-detail/$numCompte"
}
