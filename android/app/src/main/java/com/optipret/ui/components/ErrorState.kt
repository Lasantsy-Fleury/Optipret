package com.optipret.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ErrorState(
  message: String,
  modifier: Modifier = Modifier,
  actionLabel: String = "Reessayer",
  onAction: (() -> Unit)? = null
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier.size(64.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(64.dp)) {
        drawCircle(Color(0xFFDC2626))
      }
      Text(
        text = "!",
        color = Color.White,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = message,
      color = Color(0xFFDC2626),
      style = MaterialTheme.typography.bodyMedium
    )

    if (onAction != null) {
      Spacer(modifier = Modifier.height(8.dp))
      TextButton(onClick = onAction) {
        Text(text = actionLabel)
      }
    }
  }
}
