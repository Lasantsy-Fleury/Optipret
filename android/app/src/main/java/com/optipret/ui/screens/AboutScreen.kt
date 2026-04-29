package com.optipret.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // App Logo or Icon placeholder
        Surface(
            modifier = Modifier.size(100.dp),
            color = Color(0xFF2563EB).copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.large
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "O",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Optiprêt",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Optimisez la gestion de vos engagements financiers avec une clarté absolue.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color(0xFF374151),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        AboutSection(
            title = "Notre Mission",
            content = "Optiprêt a été conçu pour simplifier le suivi de vos prêts bancaires. Nous croyons que la transparence financière est la clé d'une gestion saine de son patrimoine."
        )

        Spacer(modifier = Modifier.height(24.dp))

        AboutSection(
            title = "Fonctionnalités Clés",
            content = "• Centralisation de tous vos prêts bancaires\n" +
                      "• Calcul automatique des intérêts et montants totaux\n" +
                      "• Analyses statistiques détaillées et visualisations graphiques\n" +
                      "• Interface intuitive conforme aux standards de design modernes"
        )

        Spacer(modifier = Modifier.height(40.dp))

        HorizontalDivider(color = Color(0xFFE5E7EB))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "© 2026 Optiprêt Inc. Tous droits réservés.",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF9CA3AF)
        )
        
        TextButton(onClick = { /* Handle Privacy Policy */ }) {
            Text("Politique de confidentialité", color = Color(0xFF2563EB))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AboutSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF4B5563),
            lineHeight = 20.sp
        )
    }
}
