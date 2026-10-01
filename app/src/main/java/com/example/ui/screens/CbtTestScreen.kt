package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CbtTestScreen(
  mockTest: MockTest,
  currentQuestionIndex: Int,
  userAnswers: Map<Int, Int>,
  markedForReview: Set<Int>,
  remainingSeconds: Int,
  language: AppLanguage,
  onSelectQuestion: (Int) -> Unit,
  onAnswerOption: (Int) -> Unit,
  onClearResponse: () -> Unit,
  onToggleMarkForReview: () -> Unit,
  onSaveAndNext: () -> Unit,
  onPrevious: () -> Unit,
  onSubmitTest: () -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showSubmitConfirmation by remember { mutableStateOf(false) }
  var showExitConfirmation by remember { mutableStateOf(false) }
  var showPaletteModal by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState()

  BackHandler {
    showExitConfirmation = true
  }

  val totalQuestions = mockTest.questions.size
  val currentQuestion = mockTest.questions.getOrNull(currentQuestionIndex) ?: return

  // Format timer
  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val formattedTimer = String.format("%02d:%02d", minutes, seconds)
  val isTimeUrgent = remainingSeconds <= 300 // Last 5 mins

  Scaffold(
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = mockTest.categoryName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SkyBluePrimary
              )
              Text(
                text = if (language == AppLanguage.BENGALI) mockTest.titleBn else mockTest.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            // Countdown Timer Pill
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isTimeUrgent) AlertRed.copy(alpha = 0.15f) else Color(0xFFDCFCE7),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isTimeUrgent) AlertRed else SuccessGreen
              )
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = "Timer",
                  tint = if (isTimeUrgent) AlertRed else SuccessGreen,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = formattedTimer,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  color = if (isTimeUrgent) AlertRed else SuccessGreen
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Palette Toggle Button
            IconButton(
              onClick = { showPaletteModal = true },
              modifier = Modifier.testTag("palette_toggle_button")
            ) {
              Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = "Question Palette",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // Question Status Summary Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(SoftBlueContainer)
              .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              StatChipLegend("Answered", userAnswers.size, SuccessGreen)
              StatChipLegend("Review", markedForReview.size, Color(0xFF7B1FA2))
              StatChipLegend("Left", totalQuestions - userAnswers.size, SlateGreyText)
            }

            FilledTonalButton(
              onClick = { showSubmitConfirmation = true },
              modifier = Modifier.height(30.dp),
              contentPadding = PaddingValues(horizontal = 8.dp),
              colors = ButtonDefaults.filledTonalButtonColors(containerColor = SkyBluePrimary)
            ) {
              Text("Submit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }
    },
    bottomBar = {
      // Bottom Controls: Previous, Mark for Review, Clear, Save & Next
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Previous button
          OutlinedButton(
            onClick = onPrevious,
            enabled = currentQuestionIndex > 0,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text("Previous", fontSize = 12.sp)
          }

          // Mark For Review
          val isMarked = markedForReview.contains(currentQuestionIndex)
          IconButton(
            onClick = onToggleMarkForReview,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isMarked) Color(0xFFF3E5F5) else MaterialTheme.colorScheme.surfaceVariant)
              .testTag("mark_for_review_button")
          ) {
            Icon(
              imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = "Mark for review",
              tint = if (isMarked) Color(0xFF7B1FA2) else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Clear response
          if (userAnswers.containsKey(currentQuestionIndex)) {
            TextButton(
              onClick = onClearResponse,
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Clear", color = AlertBreakingRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          // Save & Next
          Button(
            onClick = onSaveAndNext,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.testTag("save_and_next_button")
          ) {
            Text(
              text = if (currentQuestionIndex == totalQuestions - 1) "Review All" else "Save & Next",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // Question Header Card
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            color = Color(0xFFE8F5E9),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "+${mockTest.positiveMark}",
              color = Color(0xFF2E7D32),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Surface(
            color = Color(0xFFFFEBEE),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "-${mockTest.negativeMark}",
              color = AlertBreakingRed,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "${currentQuestion.subject} • ${currentQuestion.topic}",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      // Clinical scenario box if present
      if (currentQuestion.clinicalScenario != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, WbNavy.copy(alpha = 0.3f))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = WbNavy, modifier = Modifier.size(20.dp))
            Column {
              Text(
                text = "Clinical Case Scenario:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = WbNavy
              )
              Text(
                text = currentQuestion.clinicalScenario,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Question Text in English & Bengali
      Text(
        text = currentQuestion.questionText,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.SemiBold,
          lineHeight = 22.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )

      if (currentQuestion.questionTextBn.isNotBlank() && currentQuestion.questionTextBn != currentQuestion.questionText) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = currentQuestion.questionTextBn,
          style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 20.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Options List
      val currentSelectedOption = userAnswers[currentQuestionIndex]
      val optionLabels = listOf("A", "B", "C", "D")

      currentQuestion.options.forEachIndexed { optIndex, optionText ->
        val isSelected = currentSelectedOption == optIndex
        val optionBn = currentQuestion.optionsBn?.getOrNull(optIndex)

        Surface(
          onClick = { onAnswerOption(optIndex) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .testTag("option_${currentQuestionIndex}_$optIndex"),
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) SkyBluePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Option circle badge (A, B, C, D)
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isSelected) SkyBluePrimary else MaterialTheme.colorScheme.surfaceVariant),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = optionLabels.getOrElse(optIndex) { "${optIndex + 1}" },
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = optionText,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              if (optionBn != null && optionBn.isNotBlank() && optionBn != optionText) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = optionBn,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            RadioButton(
              selected = isSelected,
              onClick = { onAnswerOption(optIndex) },
              colors = RadioButtonDefaults.colors(selectedColor = SkyBluePrimary)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }

  // Question Navigation Palette Bottom Sheet
  if (showPaletteModal) {
    ModalBottomSheet(
      onDismissRequest = { showPaletteModal = false },
      sheetState = sheetState
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Question Palette",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          PaletteLegendItem("Answered", Color(0xFF2E7D32))
          PaletteLegendItem("Not Answered", Color(0xFFB0BEC5))
          PaletteLegendItem("Marked", Color(0xFF7B1FA2))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
          columns = GridCells.Fixed(5),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
        ) {
          items(totalQuestions) { qIdx ->
            val isAnswered = userAnswers.containsKey(qIdx)
            val isMarked = markedForReview.contains(qIdx)
            val isCurrent = qIdx == currentQuestionIndex

            val bgColor = when {
              isMarked -> Color(0xFF7B1FA2)
              isAnswered -> Color(0xFF2E7D32)
              else -> Color(0xFFECEFF1)
            }

            val textColor = if (isAnswered || isMarked) Color.White else Color.Black

            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .border(
                  width = if (isCurrent) 2.5.dp else 0.dp,
                  color = if (isCurrent) SkyBluePrimary else Color.Transparent,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                  onSelectQuestion(qIdx)
                  showPaletteModal = false
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${qIdx + 1}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textColor
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            showPaletteModal = false
            showSubmitConfirmation = true
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
        ) {
          Text("Proceed to Final Submit", fontWeight = FontWeight.Bold)
        }
      }
    }
  }

  // Submit Confirmation Dialog
  if (showSubmitConfirmation) {
    AlertDialog(
      onDismissRequest = { showSubmitConfirmation = false },
      title = {
        Text("Submit Test Confirmation", fontWeight = FontWeight.Bold)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("Are you sure you want to finish and submit your test?")
          Spacer(modifier = Modifier.height(6.dp))
          Text("• Answered: ${userAnswers.size} / $totalQuestions", color = SuccessGreen, fontWeight = FontWeight.Bold)
          Text("• Marked for Review: ${markedForReview.size}", color = Color(0xFF7B1FA2), fontWeight = FontWeight.Bold)
          Text("• Unattempted: ${totalQuestions - userAnswers.size}", color = AlertRed, fontWeight = FontWeight.Bold)
          Text("• Time Remaining: $formattedTimer", fontWeight = FontWeight.Medium)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showSubmitConfirmation = false
            onSubmitTest()
          },
          colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Submit Now")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSubmitConfirmation = false }) {
          Text("Continue Test")
        }
      }
    )
  }

  // Exit Confirmation Dialog
  if (showExitConfirmation) {
    AlertDialog(
      onDismissRequest = { showExitConfirmation = false },
      title = { Text("Quit Test?", fontWeight = FontWeight.Bold) },
      text = { Text("Your ongoing attempt will not be scored if you quit now.") },
      confirmButton = {
        Button(
          onClick = {
            showExitConfirmation = false
            onExit()
          },
          colors = ButtonDefaults.buttonColors(containerColor = AlertBreakingRed)
        ) {
          Text("Quit Test")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExitConfirmation = false }) {
          Text("Resume")
        }
      }
    )
  }
}

@Composable
private fun StatChipLegend(label: String, count: Int, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Text("$label: $count", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
  }
}

@Composable
private fun PaletteLegendItem(label: String, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Box(
      modifier = Modifier
        .size(14.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(color)
    )
    Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
  }
}
