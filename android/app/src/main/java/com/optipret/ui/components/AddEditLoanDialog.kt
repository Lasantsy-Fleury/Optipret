package com.optipret.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.optipret.data.model.Loan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLoanDialog(
  initialLoan: Loan?,
  onDismiss: () -> Unit,
  onSave: (Loan) -> Unit
) {
  val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }

  var numCompte by remember { mutableStateOf(initialLoan?.numCompte ?: "") }
  var nomClient by remember { mutableStateOf(initialLoan?.nomClient ?: "") }
  var nomBanque by remember { mutableStateOf(initialLoan?.nomBanque ?: "") }
  var montantText by remember { mutableStateOf(initialLoan?.montant?.toString() ?: "") }
  var datePret by remember { mutableStateOf(initialLoan?.datePret) }
  var tauxText by remember { mutableStateOf(initialLoan?.tauxPret?.toString() ?: "") }
  var showDatePicker by remember { mutableStateOf(false) }

  var errorMessage by remember { mutableStateOf<String?>(null) }

  if (showDatePicker) {
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = datePret?.time
    )

    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            val selectedMillis = datePickerState.selectedDateMillis
            datePret = selectedMillis?.let { Date(it) }
            showDatePicker = false
          },
          shape = MaterialTheme.shapes.medium
        ) {
          Text("Confirmer")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }, shape = MaterialTheme.shapes.medium) {
          Text("Annuler")
        }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = {
          val montant = montantText.toDoubleOrNull()
          val taux = tauxText.toDoubleOrNull()
          val date = datePret

          if (numCompte.isBlank() || nomClient.isBlank() || nomBanque.isBlank() || date == null) {
            errorMessage = "Veuillez renseigner tous les champs obligatoires."
            return@Button
          }
          if (montant == null || montant <= 0) {
            errorMessage = "Le montant doit être supérieur à zéro."
            return@Button
          }
          if (taux == null || taux < 0 || taux > 100) {
            errorMessage = "Le taux d'intérêt doit être compris entre 0% et 100%."
            return@Button
          }

          onSave(
            Loan(
              numCompte = numCompte,
              nomClient = nomClient,
              nomBanque = nomBanque,
              montant = montant,
              datePret = date,
              tauxPret = taux
            )
          )
        },
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
          containerColor = Color(0xFF2563EB)
        ),
        shape = MaterialTheme.shapes.medium
      ) {
        Text("Enregistrer")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, shape = MaterialTheme.shapes.medium) {
        Text("Annuler")
      }
    },
    title = {
      Text(
        text = if (initialLoan == null) "Nouveau dossier de prêt" else "Édition du prêt",
        style = MaterialTheme.typography.headlineSmall
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
          value = numCompte,
          onValueChange = { numCompte = it },
          label = { Text("Référence du compte") },
          singleLine = true,
          shape = MaterialTheme.shapes.medium,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = nomClient,
          onValueChange = { nomClient = it },
          label = { Text("Nom du titulaire") },
          singleLine = true,
          shape = MaterialTheme.shapes.medium,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = nomBanque,
          onValueChange = { nomBanque = it },
          label = { Text("Établissement bancaire") },
          singleLine = true,
          shape = MaterialTheme.shapes.medium,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = montantText,
          onValueChange = { montantText = it },
          label = { Text("Montant du principal") },
          singleLine = true,
          shape = MaterialTheme.shapes.medium,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          suffix = { Text("Ar") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = datePret?.let { dateFormat.format(it) } ?: "",
          onValueChange = {},
          label = { Text("Date d'octroi") },
          singleLine = true,
          readOnly = true,
          shape = MaterialTheme.shapes.medium,
          trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
              Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = "Sélectionner une date"
              )
            }
          },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = tauxText,
          onValueChange = { tauxText = it },
          label = { Text("Taux d'intérêt annuel") },
          singleLine = true,
          shape = MaterialTheme.shapes.medium,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          suffix = { Text("%") },
          modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
          Text(
            text = errorMessage!!,
            color = Color(0xFFDC2626),
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    }
  )
}
