@file:OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
package com.optipret.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.optipret.data.model.Loan
import com.optipret.data.model.LoanStats
import com.optipret.ui.components.AddEditLoanDialog
import com.optipret.ui.components.ErrorState
import com.optipret.ui.components.LoanCard

@Composable
fun HomeScreen(
  loans: List<Loan>,
  stats: LoanStats?,
  isLoading: Boolean,
  errorMessage: String?,
  onAddLoan: (Loan) -> Unit,
  onUpdateLoan: (Loan) -> Unit,
  onDeleteLoan: (Loan) -> Unit,
  onOpenLoan: (Loan) -> Unit,
  onOpenStats: () -> Unit,
  onRefresh: () -> Unit,
  errorPopupMessage: String?,
  onErrorPopupDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showDialog by remember { mutableStateOf(false) }
  var editingLoan by remember { mutableStateOf<Loan?>(null) }
  var searchQuery by remember { mutableStateOf("") }
  var showStatsMenu by remember { mutableStateOf(false) }
  var selectionMode by remember { mutableStateOf(false) }
  var selectedLoanIds by remember { mutableStateOf(setOf<String>()) }
  var deleteTarget by remember { mutableStateOf<Loan?>(null) }
  var showBulkDeleteConfirm by remember { mutableStateOf(false) }
  var successMessage by remember { mutableStateOf<String?>(null) }
  var pendingAction by remember { mutableStateOf<LoanAction?>(null) }

  val pullRefreshState = rememberPullRefreshState(
    refreshing = isLoading,
    onRefresh = onRefresh
  )

  val filteredLoans = if (searchQuery.isBlank()) {
    loans
  } else {
    val normalizedQuery = searchQuery.trim().lowercase()
    loans.filter { loan ->
      loan.nomClient.lowercase().contains(normalizedQuery) ||
        loan.nomBanque.lowercase().contains(normalizedQuery) ||
        loan.numCompte.lowercase().contains(normalizedQuery)
    }
  }

  if (showDialog) {
    AddEditLoanDialog(
      initialLoan = editingLoan,
      onDismiss = {
        showDialog = false
        editingLoan = null
      },
      onSave = { loan ->
        if (editingLoan == null) {
          pendingAction = LoanAction.Add
          onAddLoan(loan)
        } else {
          pendingAction = LoanAction.Update
          onUpdateLoan(loan)
        }
        showDialog = false
        editingLoan = null
      }
    )
  }

  if (pendingAction != null && !isLoading) {
    if (errorMessage == null && errorPopupMessage == null) {
      successMessage = when (pendingAction) {
        LoanAction.Add -> "Le prêt a été ajouté avec succès."
        LoanAction.Update -> "Les modifications ont été enregistrées."
        null -> null
      }
    }
    pendingAction = null
  }

  if (deleteTarget != null) {
    AlertDialog(
      onDismissRequest = { deleteTarget = null },
      title = { Text("Supprimer le prêt") },
      text = { Text("Êtes-vous sûr de vouloir supprimer ce prêt ? Cette action est irréversible.") },
      confirmButton = {
        TextButton(
          onClick = {
            deleteTarget?.let { onDeleteLoan(it) }
            deleteTarget = null
          },
          shape = MaterialTheme.shapes.medium
        ) {
          Text("Supprimer", color = Color(0xFFDC2626))
        }
      },
      dismissButton = {
        TextButton(onClick = { deleteTarget = null }, shape = MaterialTheme.shapes.medium) {
          Text("Annuler")
        }
      }
    )
  }

  if (showBulkDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showBulkDeleteConfirm = false },
      title = { Text("Supprimer la sélection") },
      text = { Text("Voulez-vous supprimer les ${selectedLoanIds.size} prêts sélectionnés ?") },
      confirmButton = {
        TextButton(
          onClick = {
            selectedLoanIds.forEach { id ->
              loans.firstOrNull { it.numCompte == id }?.let { onDeleteLoan(it) }
            }
            selectedLoanIds = emptySet()
            selectionMode = false
            showBulkDeleteConfirm = false
          },
          shape = MaterialTheme.shapes.medium
        ) {
          Text("Supprimer", color = Color(0xFFDC2626))
        }
      },
      dismissButton = {
        TextButton(onClick = { showBulkDeleteConfirm = false }, shape = MaterialTheme.shapes.medium) {
          Text("Annuler")
        }
      }
    )
  }

  if (successMessage != null) {
    AlertDialog(
      onDismissRequest = { successMessage = null },
      text = { Text(successMessage ?: "") },
      confirmButton = {
        TextButton(onClick = { successMessage = null }, shape = MaterialTheme.shapes.medium) {
          Text("Fermer")
        }
      }
    )
  }

  if (errorPopupMessage != null) {
    AlertDialog(
      onDismissRequest = onErrorPopupDismiss,
      title = { Text("Une erreur est survenue") },
      text = { Text(errorPopupMessage) },
      confirmButton = {
        TextButton(onClick = onErrorPopupDismiss, shape = MaterialTheme.shapes.medium) {
          Text("OK")
        }
      }
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pullRefresh(pullRefreshState)
      .padding(16.dp)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Rechercher un client, une banque...") },
        leadingIcon = {
          Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            Icon(
              imageVector = Icons.Outlined.Close,
              contentDescription = "Effacer la recherche",
              modifier = Modifier
                .clickable { searchQuery = "" }
                .padding(4.dp)
            )
          }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      if (selectionMode) {
        val allSelected = filteredLoans.isNotEmpty() && selectedLoanIds.size == filteredLoans.size

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
              checked = allSelected,
              onCheckedChange = { checked ->
                selectedLoanIds = if (checked) {
                  filteredLoans.map { it.numCompte }.toSet()
                } else {
                  emptySet()
                }
              }
            )
            Text(text = "Tout sélectionner", style = MaterialTheme.typography.bodyMedium)
          }

          if (selectedLoanIds.isNotEmpty()) {
            Button(
              onClick = { showBulkDeleteConfirm = true },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
              shape = MaterialTheme.shapes.medium
            ) {
              Text("Supprimer (${selectedLoanIds.size})")
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
      }

      when {
        isLoading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF2563EB))
          }
        }
        errorMessage != null -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ErrorState(message = errorMessage, onAction = onRefresh)
          }
        }
        filteredLoans.isEmpty() -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val emptyMessage = if (searchQuery.isBlank()) {
              "Aucun prêt répertorié pour le moment."
            } else {
              "Aucun résultat pour \"$searchQuery\""
            }
            Text(text = emptyMessage, color = Color(0xFF6B7280), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
          }
        }
        else -> {
          LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredLoans, key = { it.numCompte }) { loan ->
              LoanCard(
                loan = loan,
                onEdit = {
                  editingLoan = it
                  showDialog = true
                },
                onDelete = { deleteTarget = it },
                onOpen = if (selectionMode) {
                  {
                    val isSelected = selectedLoanIds.contains(loan.numCompte)
                    selectedLoanIds = if (isSelected) {
                      selectedLoanIds - loan.numCompte
                    } else {
                      selectedLoanIds + loan.numCompte
                    }
                  }
                } else {
                  onOpenLoan
                },
                onLongPress = {
                  if (!selectionMode) {
                    selectionMode = true
                  }
                  selectedLoanIds = selectedLoanIds + loan.numCompte
                },
                selectionMode = selectionMode,
                isSelected = selectedLoanIds.contains(loan.numCompte),
                onToggleSelected = {
                  val isSelected = selectedLoanIds.contains(loan.numCompte)
                  selectedLoanIds = if (isSelected) {
                    selectedLoanIds - loan.numCompte
                  } else {
                    selectedLoanIds + loan.numCompte
                  }
                }
              )
            }
          }
        }
      }
    }

    PullRefreshIndicator(
      refreshing = isLoading,
      state = pullRefreshState,
      modifier = Modifier.align(Alignment.TopCenter),
      contentColor = Color(0xFF2563EB)
    )

    Column(
      modifier = Modifier.align(Alignment.BottomEnd),
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val fabSize = 56.dp
        val menuWidth = maxWidth - 24.dp
        val menuOffset = -(maxWidth - fabSize - 12.dp) - 8.dp

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
          FloatingActionButton(
            onClick = { showStatsMenu = true },
            containerColor = Color(0xFFE5E7EB),
            contentColor = Color(0xFF374151),
            elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
              defaultElevation = 0.dp
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.size(fabSize)
          ) {
            Icon(imageVector = Icons.Outlined.BarChart, contentDescription = "Aperçu des statistiques")
          }

          DropdownMenu(
            expanded = showStatsMenu,
            onDismissRequest = { showStatsMenu = false },
            offset = DpOffset(x = menuOffset, y = (-8).dp),
            modifier = Modifier.width(menuWidth)
          ) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
              StatsCard(stats = stats)
              Spacer(modifier = Modifier.height(8.dp))
              TextButton(
                onClick = {
                  showStatsMenu = false
                  onOpenStats()
                },
                shape = MaterialTheme.shapes.medium
              ) {
                Text("Afficher l'analyse détaillée")
              }
            }
          }
        }
      }

      FloatingActionButton(
        onClick = {
          editingLoan = null
          showDialog = true
        },
        containerColor = Color(0xFF2563EB),
        contentColor = Color.White,
        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
          defaultElevation = 0.dp
        ),
        shape = MaterialTheme.shapes.medium
      ) {
        Icon(
          painter = painterResource(android.R.drawable.ic_input_add),
          contentDescription = "Nouveau prêt"
        )
      }
    }
  }
}

@Composable
private fun StatsCard(stats: LoanStats?) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      StatItem(label = "Encours total", value = stats?.total ?: 0.0)
      StatItem(label = "Minimum", value = stats?.min ?: 0.0)
      StatItem(label = "Maximum", value = stats?.max ?: 0.0)
    }
  }
}

@Composable
private fun StatItem(label: String, value: Double) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = String.format(java.util.Locale.FRANCE, "%,.0f Ar", value),
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF111827)
    )
  }
}

private enum class LoanAction {
  Add,
  Update
}
