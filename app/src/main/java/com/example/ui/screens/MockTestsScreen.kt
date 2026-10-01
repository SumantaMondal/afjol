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
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.MockTest
import com.example.model.UserProfile

@Composable
fun MockTestsScreen(
  mockTests: List<MockTest>,
  user: UserProfile,
  language: AppLanguage,
  onStartTest: (MockTest) -> Unit,
  onUpgradeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var filterType by remember { mutableStateOf("ALL") } // ALL, FREE, PREMIUM, TARGET

  val filteredTests = remember(mockTests, filterType, user.selectedCategoryId) {
    when (filterType) {
      "FREE" -> mockTests.filter { !it.isPremium }
      "PREMIUM" -> mockTests.filter { it.isPremium }
      "TARGET" -> mockTests.filter { it.categoryId == user.selectedCategoryId }
      else -> mockTests
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("mock_tests_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = if (language == AppLanguage.BENGALI) "মক টেস্ট সিরিজ" else "Mock Test Series",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (language == AppLanguage.BENGALI)
            "বাস্তব সিবিটি পরীক্ষার পরিবেশে প্রস্তুতি নিন"
          else
            "Practice in realistic CBT examination environment with negative marking",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = filterType == "ALL",
            onClick = { filterType = "ALL" },
            label = { Text("All (${mockTests.size})") }
          )
          FilterChip(
            selected = filterType == "FREE",
            onClick = { filterType = "FREE" },
            label = { Text("Free Tests") }
          )
          FilterChip(
            selected = filterType == "PREMIUM",
            onClick = { filterType = "PREMIUM" },
            label = { Text("Premium PRO") }
          )
          FilterChip(
            selected = filterType == "TARGET",
            onClick = { filterType = "TARGET" },
            label = { Text("My Exam") }
          )
        }
      }
    }

    items(filteredTests, key = { it.id }) { test ->
      TestCardItem(
        test = test,
        user = user,
        language = language,
        onStartTest = { onStartTest(test) },
        onUpgrade = onUpgradeClick
      )
    }
  }
}
