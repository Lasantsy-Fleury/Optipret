package com.optipret.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun BarChart(
  items: List<StatBarItem>,
  modifier: Modifier = Modifier,
  showLegend: Boolean = true
) {
  val maxValue = items.maxOfOrNull { it.value } ?: 0.0
  val axisColor = Color(0xFFE5E7EB)
  val density = LocalDensity.current
  val labelTextSize = with(density) { 12.sp.toPx() }
  val labelOffset = with(density) { 6.dp.toPx() }

  Canvas(modifier = modifier) {
    val chartHeight = size.height * 0.75f
    val chartWidth = size.width
    val barWidth = chartWidth / (items.size * 2f)
    val startY = size.height * 0.1f

    val labelPaint = Paint().apply {
      color = Color(0xFF111827).toArgb()
      textSize = labelTextSize
      textAlign = Paint.Align.CENTER
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      isAntiAlias = true
    }

    drawLine(
      color = axisColor,
      start = Offset(0f, startY + chartHeight),
      end = Offset(chartWidth, startY + chartHeight),
      strokeWidth = 2f
    )

    items.forEachIndexed { index, item ->
      val ratio = if (maxValue == 0.0) 0f else (item.value / maxValue).toFloat()
      val barHeight = chartHeight * ratio
      val left = (index * 2 + 1) * barWidth
      val top = startY + chartHeight - barHeight

      drawRect(
        color = Color(item.color),
        topLeft = Offset(left, top),
        size = Size(barWidth, barHeight)
      )

      if (item.value > 0.0) {
        drawIntoCanvas { canvas ->
          canvas.nativeCanvas.drawText(
            formatValue(item.value),
            left + barWidth / 2f,
            top - labelOffset,
            labelPaint
          )
        }
      }
    }
  }

  if (showLegend) {
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      items.forEach { item ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = item.label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B7280))
          Text(
            text = formatValue(item.value),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
          )
        }
      }
    }
  }
}

@Composable
fun PieChart(
  items: List<StatPieItem>,
  modifier: Modifier = Modifier,
  showLegend: Boolean = true
) {
  val total = items.sumOf { it.value }
  val density = LocalDensity.current
  val labelTextSize = with(density) { 11.sp.toPx() }
  val labelSpacing = with(density) { 10.dp.toPx() }
  val lineLength = with(density) { 16.dp.toPx() }

  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Canvas(modifier = Modifier.size(220.dp)) {
      val diameter = min(size.width, size.height)
      val radius = diameter / 2f
      var startAngle = -90f

      val labelPaint = Paint().apply {
        color = Color(0xFF111827).toArgb()
        textSize = labelTextSize
        textAlign = Paint.Align.LEFT
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
      }

      items.forEach { item ->
        val sweep = if (total == 0.0) 0f else (item.value / total * 360f).toFloat()
        drawArc(
          color = Color(item.color),
          startAngle = startAngle,
          sweepAngle = sweep,
          useCenter = true
        )

        if (item.value > 0.0 && sweep > 0f) {
          val midAngle = Math.toRadians((startAngle + sweep / 2f).toDouble())
          val startX = center.x + cos(midAngle).toFloat() * (radius * 0.6f)
          val startY = center.y + sin(midAngle).toFloat() * (radius * 0.6f)
          val endX = center.x + cos(midAngle).toFloat() * (radius + lineLength)
          val endY = center.y + sin(midAngle).toFloat() * (radius + lineLength)
          val textX = endX + if (cos(midAngle) >= 0f) labelSpacing else -labelSpacing
          val textAlign = if (cos(midAngle) >= 0f) Paint.Align.LEFT else Paint.Align.RIGHT

          drawLine(
            color = Color(0xFF9CA3AF),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2f
          )

          drawIntoCanvas { canvas ->
            labelPaint.textAlign = textAlign
            canvas.nativeCanvas.drawText(
              formatValue(item.value),
              textX,
              endY,
              labelPaint
            )
          }
        }

        startAngle += sweep
      }

      drawCircle(
        color = Color.White,
        radius = diameter * 0.25f,
        center = center
      )
    }

    if (showLegend) {
      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .padding(end = 8.dp)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
            ) {
              Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(color = Color(item.color))
              }
            }
            Text(text = "${item.label}: ${formatValue(item.value)}", color = Color(0xFF6B7280))
          }
        }
      }
    }
  }
}

private fun formatValue(value: Double): String {
  return "${value.toInt()} Ariary"
}

data class StatBarItem(val label: String, val value: Double, val color: Long)

data class StatPieItem(val label: String, val value: Double, val color: Long)
