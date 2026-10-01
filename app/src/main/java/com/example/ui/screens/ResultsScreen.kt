package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.TestAttempt
import com.example.model.UserProfile
import com.example.ui.theme.WbGold
import com.example.ui.theme.WbNavy
import com.example.ui.theme.WbRedPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultsScreen(
  user: UserProfile,
  attempts: List<TestAttempt>,
  language: AppLanguage,
  onReviewAttempt: (TestAttempt) -> Unit,
  modifier: Modifier = Modifier
) {
  val avgScore = remember(attempts) {
    if (attempts.isEmpty()) 0f else attempts.map { it.score }.average().toFloat()
  }

  val avgAccuracy = remember(attempts) {
    if (attempts.isEmpty()) 0f else attempts.map { it.accuracyPercentage }.average().toFloat()
  }

  val totalTimeMin = remember(attempts) {
    attempts.sumOf { it.timeTakenSeconds } / 60
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("results_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = if (language == AppLanguage.BENGALI) "আপনার ফলাফল ও অগ্রগতি" else "Your Performance & Results",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Detailed score analytics, accuracy metrics and attempt history",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Overall Analytics Banner
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            MetricColumn(
              label = "Tests Taken",
              value = "${attempts.size}",
              color = WbRedPrimary
            )
            MetricColumn(
              label = "Avg Score",
              value = String.format("%.1f", avgScore),
              color = WbNavy
            )
            MetricColumn(
              label = "Avg Accuracy",
              value = "${String.format("%.0f", avgAccuracy)}%",
              color = Color(0xFF2E7D32)
            )
            MetricColumn(
              label = "Study Time",
              value = "${totalTimeMin}m",
              color = Color(0xFFE65100)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = if (language == AppLanguage.BENGALI) "সাম্প্রতিক টেস্ট প্রচেষ্টা" else "Recent Test Attempts",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    if (attempts.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No tests attempted yet. Choose an exam and start your first mock test!",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      items(attempts, key = { it.id }) { attempt ->
        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val dateString = dateFormat.format(Date(attempt.timestamp))

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = dateString,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Surface(
                color = if (attempt.score >= attempt.totalMarks * 0.6f) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "Rank #${attempt.rankEstimated}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (attempt.score >= attempt.totalMarks * 0.6f) Color(0xFF2E7D32) else Color(0xFFC62828),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = attempt.testTitle,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                  text = "Score: ${String.format("%.2f", attempt.score)} / ${attempt.totalMarks.toInt()}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = WbRedPrimary
                )
                Text(
                  text = "Accuracy: ${String.format("%.0f", attempt.accuracyPercentage)}%",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF2E7D32)
                )
              }

              FilledTonalButton(
                onClick = { onReviewAttempt(attempt) },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Solutions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MetricColumn(label: String, value: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
    Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}
