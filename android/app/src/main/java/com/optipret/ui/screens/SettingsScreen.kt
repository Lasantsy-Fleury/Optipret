package com.optipret.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.optipret.BuildConfig
import com.optipret.ui.viewmodel.ConnectionStatus
import com.optipret.ui.viewmodel.SettingsViewModel
import com.optipret.utils.CurrencyOption

@Composable
fun SettingsScreen(
  viewModel: SettingsViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.snackbarMessage) {
    uiState.snackbarMessage?.let { message ->
      snackbarHostState.showSnackbar(message)
      viewModel.clearMessage()
    }
  }

  Column(
    modifier = modifier
      .padding(16.dp)
      .fillMaxWidth()
  ) {
    Text(
      text = "Configuration",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF111827)
    )

    Spacer(modifier = Modifier.height(12.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Connectivité au Serveur", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = uiState.apiUrl,
          onValueChange = viewModel::onApiUrlChange,
          label = { Text("Adresse de l'API (ex: https://api.optipret.com)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
          Button(
            onClick = viewModel::testConnection,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB), contentColor = Color(0xFF374151)),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.weight(1f)
          ) {
            Text("Vérifier")
          }

          Button(
            onClick = viewModel::saveApiUrl,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.weight(1f)
          ) {
            Text("Appliquer")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val statusText = when (uiState.connectionStatus) {
          ConnectionStatus.Connected -> "Services opérationnels"
          ConnectionStatus.Disconnected -> "Connexion interrompue"
          ConnectionStatus.Testing -> "Vérification en cours..."
          ConnectionStatus.Unknown -> "Statut de connexion indéterminé"
        }

        val statusColor = when (uiState.connectionStatus) {
          ConnectionStatus.Connected -> Color(0xFF10B981)
          ConnectionStatus.Disconnected -> Color(0xFFDC2626)
          ConnectionStatus.Testing -> Color(0xFFF59E0B)
          ConnectionStatus.Unknown -> Color(0xFF6B7280)
        }

        Text(text = "État du système : $statusText", color = statusColor, style = MaterialTheme.typography.bodySmall)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Préférences d'Affichage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Mode hors-ligne", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Consulter vos données sans connexion", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
          }
          Switch(
            checked = uiState.offlineMode,
            onCheckedChange = viewModel::setOfflineMode
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CurrencySelector(
          selected = uiState.selectedCurrency,
          onSelected = viewModel::selectCurrency
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Maintenance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Version de l'application : ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Point d'accès actuel : ${uiState.apiUrl}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
          onClick = viewModel::clearCache,
          shape = MaterialTheme.shapes.medium,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Réinitialiser le cache local", color = Color(0xFFDC2626))
        }
      }
    }

    SnackbarHost(hostState = snackbarHostState)
  }
}

@Composable
private fun CurrencySelector(
  selected: CurrencyOption,
  onSelected: (CurrencyOption) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  Column {
    Text(text = "Devise d'affichage", style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(6.dp))

    OutlinedTextField(
      value = selected.label,
      onValueChange = {},
      readOnly = true,
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.medium,
      trailingIcon = {
        TextButton(onClick = { expanded = !expanded }) {
          Text("Modifier")
        }
      }
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
      CurrencyOption.values().forEach { option ->
        DropdownMenuItem(
          text = { Text(option.label) },
          onClick = {
            onSelected(option)
            expanded = false
          }
        )
      }
    }
  }
}
