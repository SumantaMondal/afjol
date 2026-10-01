package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.StudyMaterial
import com.example.ui.theme.WbRedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
  material: StudyMaterial,
  language: AppLanguage,
  onBack: () -> Unit,
  onBookmark: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var fontSizeMultiplier by remember { mutableFloatStateOf(1.0f) }
  var isBookmarked by remember { mutableStateOf(false) }
  var downloadSimulated by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = if (language == AppLanguage.BENGALI) material.titleBn else material.title,
              maxLines = 1,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${material.subject} • ${material.pageCount} Pages",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("pdf_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          // Font size adjust
          IconButton(onClick = {
            fontSizeMultiplier = if (fontSizeMultiplier >= 1.3f) 0.9f else fontSizeMultiplier + 0.15f
          }) {
            Icon(Icons.Default.FormatSize, contentDescription = "Font Size")
          }

          // Bookmark
          IconButton(onClick = {
            isBookmarked = !isBookmarked
            onBookmark()
          }) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (isBookmarked) WbRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Download
          IconButton(onClick = { downloadSimulated = true }) {
            Icon(
              imageVector = if (downloadSimulated) Icons.Default.CheckCircle else Icons.Default.Download,
              contentDescription = "Download",
              tint = if (downloadSimulated) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
    ) {
      if (downloadSimulated) {
        Surface(
          color = Color(0xFFE8F5E9),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
            Text("PDF saved to your local offline library.", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
          }
        }
      }

      // Document Paper Card Layout
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = material.contentMarkdown,
            fontSize = (14 * fontSizeMultiplier).sp,
            lineHeight = (22 * fontSizeMultiplier).sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.SansSerif
          )
        }
      }

      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}
