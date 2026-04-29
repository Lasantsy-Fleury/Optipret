package com.optipret.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Loan(
  @SerializedName("num_compte") val numCompte: String,
  @SerializedName("nom_client") val nomClient: String,
  @SerializedName("nom_banque") val nomBanque: String,
  @SerializedName("montant") val montant: Double,
  @SerializedName("date_pret") val datePret: Date,
  @SerializedName("taux_pret") val tauxPret: Double
) {
  fun montantAPayer(): Double = montant * (1 + tauxPret / 100)
}

data class LoanStats(
  @SerializedName("total") val total: Double,
  @SerializedName("min") val min: Double,
  @SerializedName("max") val max: Double
)
