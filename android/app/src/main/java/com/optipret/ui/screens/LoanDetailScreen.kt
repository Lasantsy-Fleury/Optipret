@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.optipret.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.optipret.data.model.Loan
import com.optipret.ui.components.AddEditLoanDialog
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LoanDetailScreen(
  loan: Loan,
  onEdit: (Loan) -> Unit,
  onDelete: (Loan) -> Unit,
  onNavigateBack: () -> Unit,
  isLoading: Boolean,
  errorMessage: String?,
  errorPopupMessage: String?,
  onErrorPopupDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showDialog by remember { mutableStateOf(false) }
  var showDeleteConfirm by remember { mutableStateOf(false) }
  var successMessage by remember { mutableStateOf<String?>(null) }
  var pendingUpdate by remember { mutableStateOf(false) }
  val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

  if (showDialog) {
    AddEditLoanDialog(
      initialLoan = loan,
      onDismiss = { showDialog = false },
      onSave = {
        pendingUpdate = true
        onEdit(it)
        showDialog = false
      }
    )
  }

  if (pendingUpdate && !isLoading) {
    if (errorMessage == null && errorPopupMessage == null) {
      successMessage = "Pret modifie avec succes"
    }
    pendingUpdate = false
  }

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text("Confirmer la suppression") },
      text = { Text("Voulez-vous supprimer ce pret ?") },
      confirmButton = {
        TextButton(
          onClick = {
            onDelete(loan)
            showDeleteConfirm = false
            onNavigateBack()
          },
          shape = MaterialTheme.shapes.medium
        ) {
          Text("Supprimer")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }, shape = MaterialTheme.shapes.medium) {
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
          Text("OK")
        }
      }
    )
  }

  if (errorPopupMessage != null) {
    AlertDialog(
      onDismissRequest = onErrorPopupDismiss,
      text = { Text(errorPopupMessage) },
      confirmButton = {
        TextButton(onClick = onErrorPopupDismiss, shape = MaterialTheme.shapes.medium) {
          Text("OK")
        }
      }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = loan.nomClient,
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF111827)
    )
    Text(
      text = loan.nomBanque,
      style = MaterialTheme.typography.bodyMedium,
      color = Color(0xFF6B7280)
    )

    Spacer(modifier = Modifier.height(16.dp))

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
        DetailRow(label = "Numero de compte", value = loan.numCompte)
        DetailRow(label = "Montant pret", value = "${loan.montant} Ariary")
        DetailRow(label = "Montant a payer", value = "${loan.montantAPayer()} Ariary")
        DetailRow(label = "Taux", value = "${loan.tauxPret} %")
        DetailRow(label = "Date pret", value = dateFormat.format(loan.datePret))
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.weight(1f)
      ) {
        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Modifier")
      }
      Button(
        onClick = {
          showDeleteConfirm = true
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.weight(1f)
      ) {
        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Supprimer")
      }
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Column {
    Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF111827)
    )
  }
}
