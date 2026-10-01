package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.components.SubscriptionDialog
import com.example.ui.components.WbTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SkyBluePrimary
import com.example.viewmodel.MainTab
import com.example.viewmodel.ScreenState
import com.example.viewmodel.WbTopperViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: WbTopperViewModel = viewModel()
      val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

      MyApplicationTheme(darkTheme = isDarkTheme) {
        WbTopperApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun WbTopperApp(viewModel: WbTopperViewModel) {
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
  val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
  val categories by viewModel.categories.collectAsStateWithLifecycle()
  val featuredAlert by viewModel.featuredAlert.collectAsStateWithLifecycle()
  val alerts by viewModel.alerts.collectAsStateWithLifecycle()
  val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
  val studyMaterials by viewModel.studyMaterials.collectAsStateWithLifecycle()
  val testAttempts by viewModel.testAttempts.collectAsStateWithLifecycle()
  val adminMetrics by viewModel.adminMetrics.collectAsStateWithLifecycle()
  val paymentOrders by viewModel.paymentOrders.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val showPaywall by viewModel.showPaywall.collectAsStateWithLifecycle()
  val paywallReason by viewModel.paywallReason.collectAsStateWithLifecycle()
  val isPaymentProcessing by viewModel.isPaymentProcessing.collectAsStateWithLifecycle()
  val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()

  // Active Test State
  val activeTest by viewModel.activeTest.collectAsStateWithLifecycle()
  val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
  val userAnswers by viewModel.userAnswers.collectAsStateWithLifecycle()
  val markedForReview by viewModel.markedForReview.collectAsStateWithLifecycle()
  val timeRemainingSeconds by viewModel.timeRemainingSeconds.collectAsStateWithLifecycle()
  val latestResult by viewModel.latestResult.collectAsStateWithLifecycle()

  // Active Material & AI State
  val activeStudyMaterial by viewModel.activeStudyMaterial.collectAsStateWithLifecycle()
  val aiDrafts by viewModel.aiDraftQuestions.collectAsStateWithLifecycle()
  val isGeneratingAi by viewModel.isGeneratingAi.collectAsStateWithLifecycle()
  val aiStatus by viewModel.aiStatusMessage.collectAsStateWithLifecycle()

  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(userMessage) {
    userMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearFeedback()
    }
  }

  // Handle Android Back Navigation gracefully
  BackHandler(enabled = currentScreen != ScreenState.MAIN_TABS) {
    viewModel.navigateBack()
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      if (currentScreen == ScreenState.MAIN_TABS) {
        WbTopBar(
          user = currentUser,
          language = currentLanguage,
          isDarkTheme = isDarkTheme,
          onToggleLanguage = { viewModel.toggleLanguage() },
          onToggleTheme = { viewModel.toggleTheme() },
          onProfileClick = { viewModel.navigateToTab(MainTab.PROFILE) },
          onNotificationClick = {
            viewModel.showFeedback("Latest alert: ${featuredAlert.title}")
          }
        )
      }
    },
    bottomBar = {
      if (currentScreen == ScreenState.MAIN_TABS) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 8.dp,
          modifier = Modifier.navigationBarsPadding().testTag("bottom_nav_bar")
        ) {
          NavigationBarItem(
            selected = activeTab == MainTab.HOME,
            onClick = { viewModel.navigateToTab(MainTab.HOME) },
            icon = {
              Icon(
                imageVector = if (activeTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                contentDescription = "Home"
              )
            },
            label = {
              Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "হোম" else "Home",
                fontSize = 11.sp,
                fontWeight = if (activeTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SkyBluePrimary,
              selectedTextColor = SkyBluePrimary,
              indicatorColor = SkyBluePrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_home")
          )

          NavigationBarItem(
            selected = activeTab == MainTab.MOCK_TESTS,
            onClick = { viewModel.navigateToTab(MainTab.MOCK_TESTS) },
            icon = {
              Icon(
                imageVector = if (activeTab == MainTab.MOCK_TESTS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                contentDescription = "Mock Tests"
              )
            },
            label = {
              Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "মক টেস্ট" else "Mock Tests",
                fontSize = 11.sp,
                fontWeight = if (activeTab == MainTab.MOCK_TESTS) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SkyBluePrimary,
              selectedTextColor = SkyBluePrimary,
              indicatorColor = SkyBluePrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_mock_tests")
          )

          NavigationBarItem(
            selected = activeTab == MainTab.STUDY_MATERIALS,
            onClick = { viewModel.navigateToTab(MainTab.STUDY_MATERIALS) },
            icon = {
              Icon(
                imageVector = if (activeTab == MainTab.STUDY_MATERIALS) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                contentDescription = "Study"
              )
            },
            label = {
              Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "নোটস" else "Study Notes",
                fontSize = 11.sp,
                fontWeight = if (activeTab == MainTab.STUDY_MATERIALS) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SkyBluePrimary,
              selectedTextColor = SkyBluePrimary,
              indicatorColor = SkyBluePrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_study")
          )

          NavigationBarItem(
            selected = activeTab == MainTab.RESULTS,
            onClick = { viewModel.navigateToTab(MainTab.RESULTS) },
            icon = {
              Icon(
                imageVector = if (activeTab == MainTab.RESULTS) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                contentDescription = "Results"
              )
            },
            label = {
              Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "ফলাফল" else "Results",
                fontSize = 11.sp,
                fontWeight = if (activeTab == MainTab.RESULTS) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SkyBluePrimary,
              selectedTextColor = SkyBluePrimary,
              indicatorColor = SkyBluePrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_results")
          )

          NavigationBarItem(
            selected = activeTab == MainTab.PROFILE,
            onClick = { viewModel.navigateToTab(MainTab.PROFILE) },
            icon = {
              Icon(
                imageVector = if (activeTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = "Profile"
              )
            },
            label = {
              Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "প্রোফাইল" else "Profile",
                fontSize = 11.sp,
                fontWeight = if (activeTab == MainTab.PROFILE) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = SkyBluePrimary,
              selectedTextColor = SkyBluePrimary,
              indicatorColor = SkyBluePrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_profile")
          )
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        ScreenState.MAIN_TABS -> {
          when (activeTab) {
            MainTab.HOME -> {
              HomeScreen(
                user = currentUser,
                language = currentLanguage,
                categories = categories,
                featuredAlert = featuredAlert,
                alerts = alerts,
                onCategoryClick = { viewModel.openExamDetail(it) },
                onAlertClick = { alert ->
                  viewModel.showFeedback("Official Alert: ${alert.title}")
                }
              )
            }

            MainTab.MOCK_TESTS -> {
              MockTestsScreen(
                mockTests = mockTests,
                user = currentUser,
                language = currentLanguage,
                onStartTest = { viewModel.startMockTest(it) },
                onUpgradeClick = {
                  viewModel.showPaywallWithReason("Unlock Premium Tests for All Bengal Medical Exams")
                }
              )
            }

            MainTab.STUDY_MATERIALS -> {
              StudyMaterialsScreen(
                materials = studyMaterials,
                user = currentUser,
                language = currentLanguage,
                onOpenMaterial = { viewModel.openStudyMaterial(it) },
                onUpgradeClick = {
                  viewModel.showPaywallWithReason("Access High-Yield PDF Study Library & Question Banks")
                }
              )
            }

            MainTab.RESULTS -> {
              ResultsScreen(
                user = currentUser,
                attempts = testAttempts,
                language = currentLanguage,
                onReviewAttempt = { attempt ->
                  viewModel.viewAttemptResult(attempt)
                }
              )
            }

            MainTab.PROFILE -> {
              ProfileScreen(
                user = currentUser,
                language = currentLanguage,
                isDarkTheme = isDarkTheme,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onToggleTheme = { viewModel.toggleTheme() },
                onUpgradeClick = {
                  viewModel.showPaywallWithReason("Join 4,300+ Medical & Nursing Aspirants on WBTOPPER PRO")
                },
                onNavigateToMockTests = { viewModel.navigateToTab(MainTab.MOCK_TESTS) },
                onNavigateToResults = { viewModel.navigateToTab(MainTab.RESULTS) },
                onNavigateToStudyNotes = { viewModel.navigateToTab(MainTab.STUDY_MATERIALS) },
                onOpenPaymentHistory = {
                  viewModel.showFeedback("Payment history: ${paymentOrders.size} transactions on record.")
                },
                onLogout = { viewModel.logoutStudent() },
                onOpenAdminAuth = { viewModel.openAdminAuth() },
                onEditProfile = { name, phone -> viewModel.updateStudentProfile(name, phone) }
              )
            }
          }
        }

        ScreenState.EXAM_DETAIL -> {
          selectedCategory?.let { category ->
            ExamDetailScreen(
              category = category,
              user = currentUser,
              language = currentLanguage,
              mockTests = mockTests,
              studyMaterials = studyMaterials,
              alerts = alerts,
              onBack = { viewModel.navigateBack() },
              onStartTest = { viewModel.startMockTest(it) },
              onOpenMaterial = { viewModel.openStudyMaterial(it) },
              onUpgradeClick = {
                viewModel.showPaywallWithReason("Unlock All Mock Tests & Study Materials for ${category.title}")
              }
            )
          }
        }

        ScreenState.CBT_TEST -> {
          activeTest?.let { test ->
            CbtTestScreen(
              mockTest = test,
              currentQuestionIndex = currentQuestionIndex,
              userAnswers = userAnswers,
              markedForReview = markedForReview,
              remainingSeconds = timeRemainingSeconds,
              language = currentLanguage,
              onSelectQuestion = { viewModel.selectQuestionIndex(it) },
              onAnswerOption = { viewModel.answerCurrentQuestion(it) },
              onClearResponse = { viewModel.clearCurrentAnswer() },
              onToggleMarkForReview = { viewModel.toggleMarkForReview() },
              onSaveAndNext = { viewModel.saveAndNext() },
              onPrevious = { viewModel.goToPreviousQuestion() },
              onSubmitTest = { viewModel.submitActiveTest() },
              onExit = { viewModel.exitTestWithoutSubmission() }
            )
          }
        }

        ScreenState.TEST_RESULT -> {
          latestResult?.let { result ->
            val correspondingTest = mockTests.find { it.id == result.testId } ?: activeTest
            TestResultScreen(
              attempt = result,
              mockTest = correspondingTest,
              language = currentLanguage,
              onReattempt = {
                correspondingTest?.let { viewModel.startMockTest(it) }
              },
              onBackToHome = { viewModel.navigateBack() }
            )
          }
        }

        ScreenState.PDF_VIEWER -> {
          activeStudyMaterial?.let { material ->
            PdfViewerScreen(
              material = material,
              language = currentLanguage,
              onBack = { viewModel.navigateBack() },
              onBookmark = {
                viewModel.showFeedback("Bookmarked '${material.title}'")
              }
            )
          }
        }

        ScreenState.ADMIN_DASHBOARD -> {
          AdminDashboardScreen(
            metrics = adminMetrics,
            categories = categories,
            mockTests = mockTests,
            studyMaterials = studyMaterials,
            featuredAlert = featuredAlert,
            user = currentUser,
            paymentOrders = paymentOrders,
            aiDrafts = aiDrafts,
            isGeneratingAi = isGeneratingAi,
            aiStatus = aiStatus,
            onBack = { viewModel.navigateBack() },
            onGenerateAiQuestions = { exam, subj, topic, diff, count ->
              viewModel.generateAiQuestions(exam, subj, topic, diff, count)
            },
            onPublishDrafts = { title, catId, duration ->
              viewModel.publishDraftQuestionsToNewTest(title, catId, duration)
            },
            onRemoveDraft = { viewModel.removeDraftQuestion(it) },
            onAddCategory = { viewModel.addNewCategory(it) },
            onDeleteCategory = { viewModel.deleteCategory(it) },
            onUpdateAlert = { title, titleBn, desc ->
              viewModel.updateFeaturedAlert(title, titleBn, desc)
            },
            onToggleStudentPremium = { isPrem ->
              viewModel.toggleStudentPremiumByAdmin(isPrem)
            }
          )
        }

        ScreenState.STUDENT_AUTH -> {
          AuthScreen(
            categories = categories,
            language = currentLanguage,
            isLoading = isAuthLoading,
            onLogin = { email, pass -> viewModel.loginStudent(email, pass) },
            onSignup = { name, email, pass, phone, examId -> viewModel.signupStudent(name, email, pass, phone, examId) },
            onGoogleSignIn = { viewModel.googleSignInStudent() },
            onForgotPassword = { email -> viewModel.showFeedback("Password reset link sent to $email") },
            onDismiss = { viewModel.navigateBack() }
          )
        }

        ScreenState.ADMIN_AUTH -> {
          AdminAuthScreen(
            isLoading = isAuthLoading,
            onAuthenticateAdmin = { adminId, pass -> viewModel.authenticateAdmin(adminId, pass) },
            onBack = { viewModel.navigateBack() }
          )
        }
      }

      // Razorpay Subscription Modal
      if (showPaywall) {
        SubscriptionDialog(
          reason = paywallReason,
          language = currentLanguage,
          isProcessing = isPaymentProcessing,
          onDismiss = { viewModel.dismissPaywall() },
          onPaySuccess = { method ->
            viewModel.executeRazorpayPayment(method)
          }
        )
      }
    }
  }
}
