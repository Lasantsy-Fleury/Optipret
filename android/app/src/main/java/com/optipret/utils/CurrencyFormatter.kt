package com.optipret.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class CurrencyOption(val label: String, val suffix: String) {
  ARIARY("Ariary", "Ariary")
}

object CurrencyFormatter {
  private val formatter = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))

  fun format(amount: Double, currency: CurrencyOption): String {
    return "${formatter.format(amount)} ${currency.suffix}"
  }
}
