package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ExamAlert
import com.example.model.ExamCategory
import com.example.model.UserProfile
import com.example.ui.components.BreakingTicker
import com.example.ui.components.ExamCategoryCard
import com.example.ui.components.FeaturedAlertCard
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.SlateGreyText
import com.example.ui.theme.SoftBlueSecondary

@Composable
fun HomeScreen(
  user: UserProfile,
  language: AppLanguage,
  categories: List<ExamCategory>,
  featuredAlert: ExamAlert,
  alerts: List<ExamAlert>,
  onCategoryClick: (ExamCategory) -> Unit,
  onAlertClick: (ExamAlert) -> Unit,
  modifier: Modifier = Modifier
) {
  val activeCategories = remember(categories) {
    categories.filter { it.isActive }.sortedBy { it.sortOrder }
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen_grid"),
    contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Breaking News Ticker (Red BREAKING badge + scrolling announcement)
    item(span = { GridItemSpan(2) }) {
      BreakingTicker(
        alerts = alerts,
        language = language,
        onClick = onAlertClick
      )
    }

    // 2. Important Announcement Card (Red Bell Icon + Title + Description + View All Alerts)
    item(span = { GridItemSpan(2) }) {
      FeaturedAlertCard(
        alert = featuredAlert,
        language = language,
        onClick = { onAlertClick(featuredAlert) }
      )
    }

    // 3. Section Heading: "Exam Categories" & "Choose your target exam"
    item(span = { GridItemSpan(2) }) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.BENGALI) "পরীক্ষার বিভাগসমূহ" else "Exam Categories",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.2.sp
            ),
            color = DarkNavyText
          )
          Surface(
            color = SoftBlueSecondary.copy(alpha = 0.5f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = "${activeCategories.size} Exams",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = DarkNavyText,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = if (language == AppLanguage.BENGALI) "আপনার লক্ষ্য পরীক্ষা নির্বাচন করুন" else "Choose your target exam to begin preparation",
          style = MaterialTheme.typography.bodySmall,
          color = SlateGreyText
        )
      }
    }

    // 4. Two-Column Exam Category Grid Items (clean, attractive, focus of dashboard)
    items(
      items = activeCategories,
      key = { it.id }
    ) { category ->
      val index = activeCategories.indexOf(category)
      val isLeft = index % 2 == 0
      Box(
        modifier = Modifier
          .padding(
            start = if (isLeft) 16.dp else 4.dp,
            end = if (isLeft) 4.dp else 16.dp
          )
      ) {
        ExamCategoryCard(
          category = category,
          language = language,
          onClick = { onCategoryClick(category) }
        )
      }
    }
  }
}
