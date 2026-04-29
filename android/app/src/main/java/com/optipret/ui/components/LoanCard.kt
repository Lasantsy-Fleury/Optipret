package com.optipret.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.optipret.data.model.Loan
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun LoanCard(
  loan: Loan,
  onEdit: (Loan) -> Unit,
  onDelete: (Loan) -> Unit,
  onOpen: (Loan) -> Unit,
  onLongPress: () -> Unit,
  selectionMode: Boolean,
  isSelected: Boolean,
  onToggleSelected: () -> Unit,
  modifier: Modifier = Modifier
) {
  val actionWidth = 108.dp
  val actionWidthPx = with(LocalDensity.current) { actionWidth.toPx() }
  val swipeState = rememberSwipeableState(initialValue = 0)
  val anchors = mapOf(0f to 0, -actionWidthPx to 1)

  Box(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .width(actionWidth)
        .padding(end = 12.dp),
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.CenterVertically
    ) {
      ActionButton(
        icon = Icons.Outlined.Edit,
        contentDescription = "Modifier",
        containerColor = Color(0xFFEFF6FF),
        contentColor = Color(0xFF2563EB),
        onClick = { onEdit(loan) }
      )
      Spacer(modifier = Modifier.width(8.dp))
      ActionButton(
        icon = Icons.Outlined.Delete,
        contentDescription = "Supprimer",
        containerColor = Color(0xFFFEF2F2),
        contentColor = Color(0xFFDC2626),
        onClick = { onDelete(loan) }
      )
    }

    val swipeModifier = if (selectionMode) {
      Modifier
    } else {
      Modifier.swipeable(
        state = swipeState,
        anchors = anchors,
        thresholds = { _, _ -> FractionalThreshold(0.3f) },
        orientation = Orientation.Horizontal
      )
    }

    Card(
      modifier = Modifier
        .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
        .fillMaxWidth()
        .then(swipeModifier)
        .combinedClickable(
          onClick = { onOpen(loan) },
          onLongClick = { onLongPress() }
        ),
      shape = MaterialTheme.shapes.medium,
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
      elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = loan.nomClient,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF111827)
            )
            Text(
              text = loan.nomBanque,
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF6B7280)
            )
          }

          if (selectionMode) {
            Checkbox(
              checked = isSelected,
              onCheckedChange = { onToggleSelected() }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Column {
            Text(
              text = "Capital emprunté",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF6B7280)
            )
            Text(
              text = String.format(java.util.Locale.FRANCE, "%,.0f Ar", loan.montant),
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFF111827)
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "Total à rembourser",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF6B7280)
            )
            Text(
              text = String.format(java.util.Locale.FRANCE, "%,.0f Ar", loan.montantAPayer()),
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF2563EB)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ActionButton(
  icon: ImageVector,
  contentDescription: String,
  containerColor: Color,
  contentColor: Color,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = MaterialTheme.shapes.medium,
    color = containerColor,
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
    modifier = Modifier.size(40.dp)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = contentColor
      )
    }
  }
}
