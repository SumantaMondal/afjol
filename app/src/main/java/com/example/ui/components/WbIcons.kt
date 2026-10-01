package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

object WbIcons {
  fun getCategoryIcon(key: String): ImageVector {
    return when (key) {
      "medical_services" -> Icons.Default.MedicalServices
      "local_hospital" -> Icons.Default.LocalHospital
      "gavel" -> Icons.Default.Gavel
      "computer" -> Icons.Default.Computer
      "engineering" -> Icons.Default.Engineering
      "school" -> Icons.Default.School
      "health_and_safety" -> Icons.Default.HealthAndSafety
      "vaccines" -> Icons.Default.Vaccines
      "badge" -> Icons.Default.Badge
      "medication" -> Icons.Default.Medication
      "directions_railway" -> Icons.Default.DirectionsRailway
      "pill" -> Icons.Default.Medication
      "biotech" -> Icons.Default.Biotech
      "menu_book" -> Icons.Default.MenuBook
      "workspace_premium" -> Icons.Default.WorkspacePremium
      else -> Icons.Default.School
    }
  }
}
