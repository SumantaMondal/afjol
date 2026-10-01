package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.MockTest
import com.example.model.Question
import com.example.model.TestAttempt
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
  attempt: TestAttempt,
  mockTest: MockTest?,
  language: AppLanguage,
  onReattempt: () -> Unit,
  onBackToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackToHome() }

  var filterChoice by remember { mutableStateOf("ALL") } // ALL, INCORRECT, CORRECT

  val questions = mockTest?.questions ?: emptyList()
  val filteredQuestions = remember(questions, attempt, filterChoice) {
    when (filterChoice) {
      "INCORRECT" -> questions.filterIndexed { index, q ->
        val userAns = attempt.userAnswers[index]
        userAns != null && userAns != q.correctOptionIndex
      }
      "CORRECT" -> questions.filterIndexed { index, q ->
        attempt.userAnswers[index] == q.correctOptionIndex
      }
      else -> questions
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (language == AppLanguage.BENGALI) "ফলাফল ও বিশ্লেষণ" else "Performance & Solutions",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = DarkNavyText
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackToHome, modifier = Modifier.testTag("result_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Home", tint = DarkNavyText)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = CardWhite
        )
      )
    },
    bottomBar = {
      Surface(
        color = CardWhite,
        tonalElevation = 6.dp,
        modifier = Modifier.navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Back to Home", color = DarkNavyText)
          }

          Button(
            onClick = onReattempt,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
          ) {
            Text("Re-Attempt Test", color = Color.White)
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 1. Score Summary Banner Card (Sky Blue / Deep Navy)
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (attempt.score >= attempt.totalMarks * 0.6f) Color(0xFF0F766E) else DarkNavyText
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = attempt.testTitle,
              fontSize = 13.sp,
              color = Color.White.copy(alpha = 0.85f),
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Score Display
            Row(
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = String.format("%.2f", attempt.score),
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Text(
                text = " / ${attempt.totalMarks.toInt()} Marks",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Accuracy: ${String.format("%.1f", attempt.accuracyPercentage)}%",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
              Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Rank: #${attempt.rankEstimated} / ${attempt.totalParticipants}",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      // 2. Metrics Grid: Correct, Incorrect, Unattempted, Time
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Correct",
            value = "${attempt.correctCount}",
            color = SuccessGreen,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Incorrect",
            value = "${attempt.incorrectCount}",
            color = AlertRed,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Skipped",
            value = "${attempt.unattemptedCount}",
            color = SlateGreyText,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Time",
            value = "${attempt.timeTakenSeconds / 60}m ${attempt.timeTakenSeconds % 60}s",
            color = SkyBluePrimary,
            modifier = Modifier.weight(1f)
          )
        }
        Spacer(modifier = Modifier.height(18.dp))
      }

      // 3. Question Filter Tabs
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
          Text(
            text = if (language == AppLanguage.BENGALI) "প্রশ্নভিত্তিক সমাধান ও ব্যাখ্যা" else "Question-wise Solutions & Rationale",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = DarkNavyText
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = filterChoice == "ALL",
              onClick = { filterChoice = "ALL" },
              label = { Text("All (${questions.size})") }
            )
            FilterChip(
              selected = filterChoice == "INCORRECT",
              onClick = { filterChoice = "INCORRECT" },
              label = { Text("Incorrect (${attempt.incorrectCount})") }
            )
            FilterChip(
              selected = filterChoice == "CORRECT",
              onClick = { filterChoice = "CORRECT" },
              label = { Text("Correct (${attempt.correctCount})") }
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
      }

      // 4. Questions List with Explanations
      itemsIndexed(filteredQuestions) { fIndex, question ->
        val origIndex = questions.indexOf(question)
        val userAns = attempt.userAnswers[origIndex]
        val isCorrect = userAns == question.correctOptionIndex
        val isSkipped = userAns == null

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardWhite),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
              isCorrect -> SuccessGreen.copy(alpha = 0.5f)
              isSkipped -> BorderSoftBlue
              else -> AlertRed.copy(alpha = 0.5f)
            }
          )
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Header status
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Question ${origIndex + 1} • ${question.subject}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SlateGreyText
              )
              Surface(
                color = when {
                  isCorrect -> Color(0xFFDCFCE7)
                  isSkipped -> SoftBlueContainer
                  else -> Color(0xFFFEE2E2)
                },
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = when {
                    isCorrect -> "CORRECT (+${mockTest?.positiveMark ?: 1.0f})"
                    isSkipped -> "NOT ATTEMPTED"
                    else -> "INCORRECT (-${mockTest?.negativeMark ?: 0.25f})"
                  },
                  color = when {
                    isCorrect -> SuccessGreen
                    isSkipped -> SlateGreyText
                    else -> AlertRed
                  },
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question Text
            Text(
              text = question.questionText,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = DarkNavyText
            )

            if (question.questionTextBn.isNotBlank() && question.questionTextBn != question.questionText) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = question.questionTextBn,
                fontSize = 12.sp,
                color = SlateGreyText
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Options with Highlights
            val labels = listOf("A", "B", "C", "D")
            question.options.forEachIndexed { oIdx, optText ->
              val isThisCorrect = oIdx == question.correctOptionIndex
              val isThisSelectedByUser = userAns == oIdx

              val optBg = when {
                isThisCorrect -> Color(0xFFDCFCE7)
                isThisSelectedByUser -> Color(0xFFFEE2E2)
                else -> SoftBlueContainer
              }

              val optBorder = when {
                isThisCorrect -> SuccessGreen
                isThisSelectedByUser -> AlertRed
                else -> Color.Transparent
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = optBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, optBorder),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(
                    text = "${labels.getOrElse(oIdx) { "${oIdx + 1}" }}.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DarkNavyText
                  )
                  Text(
                    text = optText,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    color = DarkNavyText
                  )
                  if (isThisCorrect) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                  } else if (isThisSelectedByUser) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clinical / Academic Explanation
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SoftBlueContainer)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(Icons.Default.Lightbulb, contentDescription = null, tint = WbGold, modifier = Modifier.size(16.dp))
                  Text(
                    text = if (language == AppLanguage.BENGALI) "ব্যাখ্যা ও সমাধান:" else "Detailed Explanation:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DarkNavyText
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = question.explanation,
                  fontSize = 12.sp,
                  lineHeight = 17.sp,
                  color = DarkNavyText
                )
                if (question.explanationBn.isNotBlank() && question.explanationBn != question.explanation) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = question.explanationBn,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = SlateGreyText
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = color)
      Text(text = title, fontSize = 10.sp, color = SlateGreyText)
    }
  }
}
