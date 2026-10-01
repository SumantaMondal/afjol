package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.StudyMaterial
import com.example.model.UserProfile

@Composable
fun StudyMaterialsScreen(
  materials: List<StudyMaterial>,
  user: UserProfile,
  language: AppLanguage,
  onOpenMaterial: (StudyMaterial) -> Unit,
  onUpgradeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedSubject by remember { mutableStateOf("ALL") }

  val subjects = remember(materials) {
    listOf("ALL") + materials.map { it.subject }.distinct()
  }

  val filteredList = remember(materials, selectedSubject) {
    if (selectedSubject == "ALL") materials else materials.filter { it.subject == selectedSubject }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("study_materials_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = if (language == AppLanguage.BENGALI) "স্টাডি মেটেরিয়াল ও পিডিএফ নোটস" else "Digital Study Library & Notes",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (language == AppLanguage.BENGALI)
            "বিশেষজ্ঞ শিক্ষকদের দ্বারা প্রস্তুতকৃত উচ্চ ফলনশীল হ্যান্ডবুক"
          else
            "High-yield study notes, chapter summaries, and clinical cheat-sheets",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subject Filter Scroll
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          subjects.take(4).forEach { subj ->
            FilterChip(
              selected = selectedSubject == subj,
              onClick = { selectedSubject = subj },
              label = { Text(subj) }
            )
          }
        }
      }
    }

    items(filteredList, key = { it.id }) { material ->
      MaterialCardItem(
        material = material,
        user = user,
        language = language,
        onOpen = { onOpenMaterial(material) },
        onUpgrade = onUpgradeClick
      )
    }
  }
}
