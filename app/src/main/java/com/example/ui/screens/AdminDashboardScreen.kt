package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.ui.components.FeaturedAlertCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  metrics: AdminMetric,
  categories: List<ExamCategory>,
  mockTests: List<MockTest>,
  studyMaterials: List<StudyMaterial>,
  featuredAlert: ExamAlert,
  user: UserProfile,
  paymentOrders: List<PaymentOrder>,
  aiDrafts: List<Question>,
  isGeneratingAi: Boolean,
  aiStatus: String?,
  onBack: () -> Unit,
  onGenerateAiQuestions: (exam: String, subject: String, topic: String, diff: String, count: Int) -> Unit,
  onPublishDrafts: (title: String, catId: String, duration: Int) -> Unit,
  onRemoveDraft: (Int) -> Unit,
  onAddCategory: (ExamCategory) -> Unit,
  onDeleteCategory: (String) -> Unit,
  onUpdateAlert: (title: String, titleBn: String, desc: String) -> Unit,
  onToggleStudentPremium: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    "Overview",
    "Categories",
    "Mock Tests",
    "Question Bank",
    "AI Generator",
    "Study Materials",
    "Students & Access",
    "Payments (Razorpay)",
    "Announcements"
  )

  var showAddCategoryDialog by remember { mutableStateOf(false) }
  var showCreateTestDialog by remember { mutableStateOf(false) }
  var showEditAlertDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "WBTOPPER Admin Control Hub",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = DarkNavyText
            )
            Text(
              text = "Full-Stack Educational Management System",
              fontSize = 11.sp,
              color = SlateGreyText
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavyText)
          }
        },
        actions = {
          FilledTonalButton(
            onClick = onBack,
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = SoftBlueSecondary.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp), tint = DarkNavyText)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Student View", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkNavyText)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
      )
    },
    containerColor = BackgroundBlueWhite,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Light Sky Blue Tab Navigation
      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = CardWhite,
        contentColor = SkyBluePrimary,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = SkyBluePrimary
          )
        }
      ) {
        tabs.forEachIndexed { index, tabName ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = tabName,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (selectedTab == index) SkyBluePrimary else SlateGreyText
              )
            }
          )
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        when (selectedTab) {
          0 -> {
            // 1. Dashboard Overview
            item {
              Text("Platform Key Metrics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
            }
            item {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard("Total Students", "${metrics.totalStudents}", SkyBluePrimary, Modifier.weight(1f))
                AdminMetricCard("Active PRO Users", "${metrics.activePremium}", Color(0xFFB78103), Modifier.weight(1f))
              }
            }
            item {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard("Total Mock Tests", "${mockTests.size}", SuccessGreen, Modifier.weight(1f))
                AdminMetricCard("Test Attempts", "${metrics.totalAttempts}", AlertRed, Modifier.weight(1f))
              }
            }
            item {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard("Study Notes PDF", "${studyMaterials.size}", Color(0xFF0284C7), Modifier.weight(1f))
                AdminMetricCard("Razorpay Revenue", "₹${metrics.totalRevenueInr}", Color(0xFF7C3AED), Modifier.weight(1f))
              }
            }
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text("Quick Actions", fontWeight = FontWeight.Bold, color = DarkNavyText)
                  Spacer(modifier = Modifier.height(10.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                      onClick = { selectedTab = 4 },
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(10.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                    ) {
                      Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Gemini AI Quiz", fontSize = 12.sp)
                    }

                    OutlinedButton(
                      onClick = { showAddCategoryDialog = true },
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Add Exam", fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }

          1 -> {
            // 2. Exam Category Management
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Exam Categories (${categories.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
                Button(
                  onClick = { showAddCategoryDialog = true },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Add New Category")
                }
              }
            }

            items(categories) { cat ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(cat.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkNavyText)
                    Text(cat.subtitle, fontSize = 11.sp, color = SlateGreyText)
                    if (cat.badgeText != null) {
                      Spacer(modifier = Modifier.height(2.dp))
                      Text("Status: ${cat.badgeText}", fontSize = 10.sp, color = SkyBluePrimary, fontWeight = FontWeight.SemiBold)
                    }
                  }
                  IconButton(onClick = { onDeleteCategory(cat.id) }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = AlertRed)
                  }
                }
              }
            }
          }

          2 -> {
            // 3. Mock Test Management
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Mock Tests Series (${mockTests.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
                Button(
                  onClick = { showCreateTestDialog = true },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("New Mock Test")
                }
              }
            }

            items(mockTests) { test ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Surface(
                      color = if (test.isPremium) WbGoldContainer else Color(0xFFDCFCE7),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = if (test.isPremium) "PREMIUM" else "FREE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (test.isPremium) Color(0xFFB78103) else SuccessGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }

                    Text("${test.durationMinutes} Mins • ${test.totalMarks.toInt()} Marks", fontSize = 11.sp, color = SlateGreyText)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(test.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkNavyText)
                  Text("Exam: ${test.categoryName} • ${test.totalQuestions} Questions", fontSize = 12.sp, color = SlateGreyText)
                }
              }
            }
          }

          3 -> {
            // 4. Question Bank
            item {
              Text("Question Bank Database", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
            }
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text("Question Bank Statistics", fontWeight = FontWeight.Bold, color = DarkNavyText)
                  Spacer(modifier = Modifier.height(6.dp))
                  Text("• Total Master Questions: ${mockTests.sumOf { it.questions.size }}")
                  Text("• Clinical Scenario Questions: 48 verified items")
                  Text("• Bengali & English Bilingual items: 100%")
                  Text("• Negative marking support: 0.25 and 0.33")
                }
              }
            }
          }

          4 -> {
            // 5. AI Question Generator
            item {
              AiGeneratorCard(
                categories = categories,
                isGenerating = isGeneratingAi,
                statusMessage = aiStatus,
                onGenerate = onGenerateAiQuestions
              )
            }

            if (aiDrafts.isNotEmpty()) {
              item {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    "AI Generated Drafts (${aiDrafts.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkNavyText
                  )
                  Button(
                    onClick = {
                      onPublishDrafts("AI High-Yield Clinical Mock Test", categories.firstOrNull()?.id ?: "jenpas_ug", 60)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish to App", fontSize = 12.sp)
                  }
                }
              }

              itemsIndexed(aiDrafts) { index, question ->
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = CardWhite),
                  border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text("Draft Question #${index + 1} (${question.subject})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavyText)
                      IconButton(onClick = { onRemoveDraft(index) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = AlertRed)
                      }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(question.questionText, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarkNavyText)
                    Spacer(modifier = Modifier.height(6.dp))
                    question.options.forEachIndexed { oIdx, opt ->
                      val isCorrect = oIdx == question.correctOptionIndex
                      Text(
                        text = "${oIdx + 1}. $opt ${if (isCorrect) "✓ (Correct)" else ""}",
                        fontSize = 12.sp,
                        color = if (isCorrect) SuccessGreen else SlateGreyText,
                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Rationale: ${question.explanation}",
                      fontSize = 11.sp,
                      color = SlateGreyText
                    )
                  }
                }
              }
            }
          }

          5 -> {
            // 6. PDF & Study Materials
            item {
              Text("Digital Study Materials Library (${studyMaterials.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
            }
            items(studyMaterials) { mat ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = SkyBluePrimary, modifier = Modifier.size(28.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(mat.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkNavyText)
                    Text("${mat.subject} • ${mat.pageCount} Pages • ${if (mat.isPremium) "PRO" else "FREE"}", fontSize = 11.sp, color = SlateGreyText)
                  }
                }
              }
            }
          }

          6 -> {
            // 7. Students & Access Management
            item {
              Text("Student Accounts & Permissions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
            }
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text("Current Active Student", fontWeight = FontWeight.Bold, color = DarkNavyText)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text("Name: ${user.name}", fontSize = 12.sp, color = DarkNavyText)
                  Text("Email: ${user.email}", fontSize = 12.sp, color = SlateGreyText)
                  Text("Target: ${user.targetExamName}", fontSize = 12.sp, color = SlateGreyText)
                  Text(
                    text = "Subscription: ${if (user.isPremium) "PRO UNLOCKED" else "FREE STUDENT"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (user.isPremium) SuccessGreen else AlertRed
                  )

                  Spacer(modifier = Modifier.height(10.dp))
                  Button(
                    onClick = { onToggleStudentPremium(!user.isPremium) },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (user.isPremium) AlertRed else SuccessGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(if (user.isPremium) "Revoke Premium Entitlement" else "Grant Free Premium Entitlement")
                  }
                }
              }
            }
          }

          7 -> {
            // 8. Razorpay Payment Management
            item {
              Text("Razorpay Verified Transactions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
            }
            items(paymentOrders) { order ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(order.orderId, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkNavyText)
                    Text(order.paymentMethod, fontSize = 11.sp, color = SlateGreyText)
                    Text("Gateway: Razorpay Test Mode", fontSize = 10.sp, color = SkyBluePrimary)
                  }
                  Text("₹${order.amountInRupees}", fontWeight = FontWeight.ExtraBold, color = SuccessGreen, fontSize = 16.sp)
                }
              }
            }
          }

          8 -> {
            // 9. Announcements & Alerts Management
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Live Announcements Banner", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = DarkNavyText)
                Button(
                  onClick = { showEditAlertDialog = true },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                  Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Edit Live Banner")
                }
              }
            }
            item {
              FeaturedAlertCard(
                alert = featuredAlert,
                language = AppLanguage.ENGLISH,
                onClick = { showEditAlertDialog = true }
              )
            }
          }
        }
      }
    }
  }

  // Add Category Dialog
  if (showAddCategoryDialog) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var bengaliTitle by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddCategoryDialog = false },
      title = { Text("Add Exam Category", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Exam Name (e.g. WBJEE 2026)") }, singleLine = true)
          OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Short Subtitle") }, singleLine = true)
          OutlinedTextField(value = bengaliTitle, onValueChange = { bengaliTitle = it }, label = { Text("Bengali Name") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (title.isNotBlank()) {
              onAddCategory(
                ExamCategory(
                  id = "cat_" + System.currentTimeMillis(),
                  title = title,
                  subtitle = subtitle.ifBlank { "Competitive Examination" },
                  bengaliTitle = bengaliTitle.ifBlank { title },
                  bengaliSubtitle = subtitle,
                  iconKey = "school",
                  bgHex = 0xFFE0F2FE,
                  iconTintHex = 0xFF0284C7,
                  badgeText = "New Exam",
                  description = "Preparation course for $title."
                )
              )
              showAddCategoryDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
        ) {
          Text("Add Category")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Edit Alert Banner Dialog
  if (showEditAlertDialog) {
    var alertTitle by remember { mutableStateOf(featuredAlert.title) }
    var alertTitleBn by remember { mutableStateOf(featuredAlert.titleBn) }
    var alertDesc by remember { mutableStateOf(featuredAlert.description) }

    AlertDialog(
      onDismissRequest = { showEditAlertDialog = false },
      title = { Text("Update Live Announcement Banner", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = alertTitle, onValueChange = { alertTitle = it }, label = { Text("Headline (English)") })
          OutlinedTextField(value = alertTitleBn, onValueChange = { alertTitleBn = it }, label = { Text("Headline (Bengali)") })
          OutlinedTextField(value = alertDesc, onValueChange = { alertDesc = it }, label = { Text("Description") }, maxLines = 3)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateAlert(alertTitle, alertTitleBn, alertDesc)
            showEditAlertDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
        ) {
          Text("Publish Update")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditAlertDialog = false }) { Text("Cancel") }
      }
    )
  }
}

@Composable
fun AiGeneratorCard(
  categories: List<ExamCategory>,
  isGenerating: Boolean,
  statusMessage: String?,
  onGenerate: (exam: String, subject: String, topic: String, diff: String, count: Int) -> Unit
) {
  var selectedExam by remember { mutableStateOf("JENPAS-UG 2026") }
  var subject by remember { mutableStateOf("Human Physiology & Nursing") }
  var topic by remember { mutableStateOf("Endocrine & Clinical Scenarios") }
  var difficulty by remember { mutableStateOf("Medium") }
  var count by remember { mutableIntStateOf(5) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SkyBluePrimary)
        Text(
          text = "Gemini AI Question Generator",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = DarkNavyText
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Generate exam-aligned MCQs with 4 options, clinical rationales and Bengali translations using Gemini 3.5 Flash.",
        fontSize = 11.sp,
        color = SlateGreyText
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = selectedExam,
        onValueChange = { selectedExam = it },
        label = { Text("Target Exam") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = subject,
        onValueChange = { subject = it },
        label = { Text("Subject (e.g. Anatomy, Pharmacology)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = topic,
        onValueChange = { topic = it },
        label = { Text("Topic (e.g. Critical Care, Drug Doses)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Questions: $count", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkNavyText)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(3, 5, 10).forEach { c ->
            FilterChip(
              selected = count == c,
              onClick = { count = c },
              label = { Text("$c") }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = { onGenerate(selectedExam, subject, topic, difficulty, count) },
        enabled = !isGenerating,
        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("gemini_generate_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
      ) {
        if (isGenerating) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Generating via Gemini...", color = Color.White, fontWeight = FontWeight.Bold)
        } else {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Generate $count MCQs with AI", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }

      if (statusMessage != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(statusMessage, fontSize = 11.sp, color = SkyBluePrimary, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@Composable
private fun AdminMetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CardWhite),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = title, fontSize = 11.sp, color = SlateGreyText)
    }
  }
}
