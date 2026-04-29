package com.optipret.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
  Loans("loans", "Prets", Icons.Default.AccountBalance),
  Stats("statistics", "Statistiques", Icons.Default.BarChart),
  Settings("settings", "Parametres", Icons.Default.Settings),
  About("about", "A propos", Icons.Default.Info)
}

@Composable
fun BottomNavBar(
  selectedTab: BottomTab,
  onTabSelected: (BottomTab) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier,
    containerColor = Color.White,
    tonalElevation = 0.dp
  ) {
    BottomTab.values().forEach { tab ->
      NavigationBarItem(
        selected = tab == selectedTab,
        onClick = { onTabSelected(tab) },
        icon = {
          Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = if (tab == selectedTab) Color(0xFF2563EB) else Color(0xFF6B7280)
          )
        },
        label = {
          Text(
            text = tab.label,
            color = if (tab == selectedTab) Color(0xFF2563EB) else Color(0xFF6B7280)
          )
        },
        colors = NavigationBarItemDefaults.colors(
          indicatorColor = Color(0xFFE5E7EB)
        )
      )
    }
  }
}
