package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.WbIcons
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
  category: ExamCategory,
  user: UserProfile,
  language: AppLanguage,
  mockTests: List<MockTest>,
  studyMaterials: List<StudyMaterial>,
  alerts: List<ExamAlert>,
  onBack: () -> Unit,
  onStartTest: (MockTest) -> Unit,
  onOpenMaterial: (StudyMaterial) -> Unit,
  onUpgradeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  // Exactly 3 tabs requested: 1. Mock Tests, 2. Study Notes, 3. Alerts (NO OVERVIEW TAB)
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    if (language == AppLanguage.BENGALI) "মক টেস্ট" else "Mock Tests",
    if (language == AppLanguage.BENGALI) "স্টাডি নোটস" else "Study Notes",
    if (language == AppLanguage.BENGALI) "বিজ্ঞপ্তি ও তারিখ" else "Alerts"
  )

  val examTests = remember(mockTests, category) {
    mockTests.filter { it.categoryId == category.id }
  }

  val examMaterials = remember(studyMaterials, category) {
    studyMaterials.filter { it.categoryId == category.id }
  }

  val examAlerts = remember(alerts, category) {
    alerts.filter { it.categoryId == category.id || it.isBreaking }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = if (language == AppLanguage.BENGALI) category.bengaliTitle else category.title,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = DarkNavyText
            )
            if (user.isPremium) {
              Surface(
                color = WbGoldContainer,
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WbGold)
              ) {
                Text(
                  text = "PRO",
                  color = Color(0xFFB78103),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("exam_detail_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavyText)
          }
        },
        actions = {
          IconButton(onClick = { /* notification */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Alerts", tint = SlateGreyText)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = CardWhite
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // 1. Exam Banner (matching Screenshot 2 specification)
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = CardWhite
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // Exam-specific Icon in soft sky blue box
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(SoftBlueSecondary.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = WbIcons.getCategoryIcon(category.iconKey),
                  contentDescription = category.title,
                  tint = SkyBluePrimary,
                  modifier = Modifier.size(28.dp)
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                if (category.badgeText != null) {
                  Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = category.badgeText,
                      color = SkyBluePrimary,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                  text = if (language == AppLanguage.BENGALI) category.bengaliTitle else category.title,
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                  color = DarkNavyText
                )
                Text(
                  text = if (language == AppLanguage.BENGALI) category.bengaliSubtitle else category.subtitle,
                  style = MaterialTheme.typography.bodyMedium,
                  color = SlateGreyText
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Short Description & Official notification link
            Text(
              text = category.description,
              style = MaterialTheme.typography.bodySmall,
              color = SlateGreyText,
              lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
              color = SoftBlueContainer,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Text(
                  text = "Official 2026 Examination Notice Verified",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = DarkNavyText
                )
              }
            }
          }
        }
      }

      // 2. Tab Navigation (Only 3 Tabs: Mock Tests, Study Notes, Alerts)
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          modifier = Modifier.fillMaxWidth(),
          containerColor = CardWhite,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = SkyBluePrimary
            )
          }
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 13.sp,
                  color = if (selectedTab == index) SkyBluePrimary else SlateGreyText
                )
              }
            )
          }
        }
        Spacer(modifier = Modifier.height(12.dp))
      }

      // 3. Tab Contents
      when (selectedTab) {
        0 -> {
          // Tab 1: Mock Tests
          if (examTests.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Mock tests are being published by the admin.",
                  color = SlateGreyText
                )
              }
            }
          } else {
            items(examTests) { test ->
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

        1 -> {
          // Tab 2: Study Notes
          if (examMaterials.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Study notes & PDF chapters are being uploaded.",
                  color = SlateGreyText
                )
              }
            }
          } else {
            items(examMaterials) { material ->
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

        2 -> {
          // Tab 3: Alerts
          items(examAlerts) { alert ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = CardWhite),
              border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Icon(
                  imageVector = if (alert.isBreaking) Icons.Default.Campaign else Icons.Default.Notifications,
                  contentDescription = null,
                  tint = if (alert.isBreaking) AlertRed else SkyBluePrimary,
                  modifier = Modifier.size(24.dp)
                )
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = alert.date,
                      fontSize = 11.sp,
                      color = SlateGreyText
                    )
                    if (alert.isOfficialVerified) {
                      Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = "Official Verified",
                          color = SuccessGreen,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                      }
                    }
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = if (language == AppLanguage.BENGALI) alert.titleBn else alert.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkNavyText
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = alert.description,
                    fontSize = 12.sp,
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
fun TestCardItem(
  test: MockTest,
  user: UserProfile,
  language: AppLanguage,
  onStartTest: () -> Unit,
  onUpgrade: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isLocked = test.isPremium && !user.isPremium

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("test_card_${test.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (test.isPremium) WbGold.copy(alpha = 0.4f) else BorderSoftBlue
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = if (test.isPremium) WbGoldContainer else Color(0xFFDCFCE7),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = if (test.isPremium) "PREMIUM TEST" else "FREE TEST",
            color = if (test.isPremium) Color(0xFFB78103) else SuccessGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Surface(
          color = SoftBlueContainer,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = test.difficulty,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkNavyText,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (language == AppLanguage.BENGALI) test.titleBn else test.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = DarkNavyText
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = SlateGreyText)
          Text("${test.totalQuestions} Questions", fontSize = 11.sp, color = SlateGreyText)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = SlateGreyText)
          Text("${test.durationMinutes} Mins", fontSize = 11.sp, color = SlateGreyText)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Icon(Icons.Default.StarOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = SlateGreyText)
          Text("${test.totalMarks.toInt()} Marks", fontSize = 11.sp, color = SlateGreyText)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = { if (isLocked) onUpgrade() else onStartTest() },
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("start_test_button_${test.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isLocked) WbGold else SkyBluePrimary
        )
      ) {
        if (isLocked) {
          Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Unlock with Premium (₹199)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
        } else {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (language == AppLanguage.BENGALI) "মক টেস্ট শুরু করুন" else "Start CBT Test", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
        }
      }
    }
  }
}

@Composable
fun MaterialCardItem(
  material: StudyMaterial,
  user: UserProfile,
  language: AppLanguage,
  onOpen: () -> Unit,
  onUpgrade: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isLocked = material.isPremium && !user.isPremium

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("material_card_${material.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(50.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(if (material.isPremium) WbGoldContainer else SoftBlueSecondary.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PictureAsPdf,
          contentDescription = "PDF Icon",
          tint = if (material.isPremium) Color(0xFFB78103) else SkyBluePrimary,
          modifier = Modifier.size(28.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            color = SoftBlueContainer,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = material.subject,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = DarkNavyText,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
          if (material.isPremium) {
            Surface(
              color = WbGoldContainer,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "PREMIUM",
                color = Color(0xFFB78103),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = if (language == AppLanguage.BENGALI) material.titleBn else material.title,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          maxLines = 2,
          color = DarkNavyText
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "${material.pageCount} Pages • ${material.language}",
          fontSize = 11.sp,
          color = SlateGreyText
        )
      }

      FilledTonalButton(
        onClick = { if (isLocked) onUpgrade() else onOpen() },
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = SoftBlueSecondary.copy(alpha = 0.6f)
        )
      ) {
        if (isLocked) {
          Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = DarkNavyText)
        } else {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp), tint = SkyBluePrimary)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            if (language == AppLanguage.BENGALI) "পড়ুন" else "Read",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SkyBluePrimary
          )
        }
      }
    }
  }
}
