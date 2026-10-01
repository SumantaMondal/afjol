package com.example.data

import android.util.Log
import com.example.data.firebase.WbFirestoreService
import com.example.data.local.WbTopperDatabase
import com.example.data.local.entities.ExamCategoryEntity
import com.example.data.local.entities.StudyResourceEntity
import com.example.data.local.entities.TestAttemptEntity
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WbTopperRepository {

  private val _currentUser = MutableStateFlow(
    UserProfile(
      id = "wb_user_101",
      name = "Priyanka Mukherjee",
      email = "priyanka.nursing@wbtopper.com",
      phone = "+91 98301 23456",
      role = UserRole.STUDENT,
      selectedCategoryId = "jenpas_ug",
      isPremium = false,
      targetExamName = "JENPAS-UG 2026"
    )
  )
  val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

  private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  private val _isDarkTheme = MutableStateFlow(false)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  private val _featuredAlert = MutableStateFlow(
    ExamAlert(
      id = "alert_01",
      title = "JENPAS-UG 2026 Admit Card Released",
      titleBn = "JENPAS-UG ২০২৬ অ্যাডমিট কার্ড প্রকাশিত হয়েছে",
      date = "Updated Today, 10:30 AM",
      isBreaking = true,
      isOfficialVerified = true,
      categoryId = "jenpas_ug",
      description = "West Bengal Joint Entrance Examinations Board (WBJEEB) has released the admit cards for JENPAS-UG 2026. Candidates can download using application number and date of birth.",
      linkText = "Download Official Admit Card"
    )
  )
  val featuredAlert: StateFlow<ExamAlert> = _featuredAlert.asStateFlow()

  private val _alerts = MutableStateFlow<List<ExamAlert>>(
    listOf(
      ExamAlert(
        id = "alert_01",
        title = "JENPAS-UG 2026 Admit Card Released",
        titleBn = "JENPAS-UG ২০২৬ অ্যাডমিট কার্ড প্রকাশিত হয়েছে",
        date = "Today",
        isBreaking = true,
        isOfficialVerified = true,
        categoryId = "jenpas_ug",
        description = "WBJEEB has released the official admit card. Exam will be held across all WB districts."
      ),
      ExamAlert(
        id = "alert_02",
        title = "ANM & GNM 2026 Application Form Correction Window Open",
        titleBn = "ANM এবং GNM ২০২৬ আবেদন সংশোধন উইন্ডো চালু হয়েছে",
        date = "Yesterday",
        isBreaking = false,
        isOfficialVerified = true,
        categoryId = "anm_gnm",
        description = "Correction window for WBJEEB ANM-GNM examination is active until 15th July 5:00 PM."
      ),
      ExamAlert(
        id = "alert_03",
        title = "WB CHO 2026 District-Wise Merit List Published",
        titleBn = "WB CHO ২০২৬ জেলাভিত্তিক মেধা তালিকা প্রকাশিত",
        date = "2 days ago",
        isBreaking = false,
        isOfficialVerified = true,
        categoryId = "wb_cho",
        description = "National Health Mission West Bengal published the list of shortlisted candidates for CPCH course."
      ),
      ExamAlert(
        id = "alert_04",
        title = "NORCET 2026 Stage 1 City Intimation Slip Active",
        titleBn = "NORCET ২০২৬ স্টেজ ১ পরীক্ষার শহর তথ্য প্রকাশ",
        date = "3 days ago",
        isBreaking = false,
        isOfficialVerified = true,
        categoryId = "norcet",
        description = "AIIMS New Delhi has published the preliminary exam city allocation for Nursing Officer recruitment."
      )
    )
  )
  val alerts: StateFlow<List<ExamAlert>> = _alerts.asStateFlow()

  private val _categories = MutableStateFlow<List<ExamCategory>>(
    listOf(
      ExamCategory(
        id = "jenpas_ug",
        title = "JENPAS-UG 2026",
        subtitle = "Nursing & Paramedical Courses",
        bengaliTitle = "জেনপাস-ইউজি ২০২৬",
        bengaliSubtitle = "নার্সিং ও প্যারামেডিক্যাল কোর্স",
        iconKey = "medical_services",
        bgHex = 0xFFE0F2F1, // Pastel Mint
        iconTintHex = 0xFF00796B,
        badgeText = "Admit Card Out",
        description = "Joint Entrance Test for B.Sc. Nursing, BPT, BMLT, B.Sc. CPT, OTT, PT, and other paramedical courses conducted by WBJEEB.",
        syllabusSummary = listOf("Biology (40 Marks)", "Physics (20 Marks)", "Chemistry (20 Marks)", "English (10 Marks)", "Logical Reasoning (10 Marks)"),
        notificationCount = 3,
        sortOrder = 1
      ),
      ExamCategory(
        id = "anm_gnm",
        title = "ANM & GNM 2026",
        subtitle = "Nursing Entrance Examination",
        bengaliTitle = "এএনএম ও জিএনএম ২০২৬",
        bengaliSubtitle = "নার্সিং প্রবেশিকা পরীক্ষা",
        iconKey = "local_hospital",
        bgHex = 0xFFE3F2FD, // Pastel Blue
        iconTintHex = 0xFF1976D2,
        badgeText = "Correction Window",
        description = "Common entrance exam for admission into 2-year ANM(R) and 3-year GNM courses across West Bengal government & private nursing institutes.",
        syllabusSummary = listOf("Life Science (50 Marks)", "Physical Science (25 Marks)", "Basic English (15 Marks)", "Mathematics (10 Marks)", "General Knowledge (10 Marks)", "Logical Reasoning (5 Marks)"),
        notificationCount = 2,
        sortOrder = 2
      ),
      ExamCategory(
        id = "culet",
        title = "CULET 2026",
        subtitle = "Calcutta University Law",
        bengaliTitle = "কিউলেট ২০২৬",
        bengaliSubtitle = "কলকাতা বিশ্ববিদ্যালয় আইন",
        iconKey = "gavel",
        bgHex = 0xFFFCE4EC, // Pastel Pink
        iconTintHex = 0xFFC2185B,
        badgeText = "Syllabus Live",
        description = "Entrance exam for admission to 5-Year B.A. LL.B. Degree programmes in Calcutta University and its affiliated colleges.",
        syllabusSummary = listOf("General English (30 Marks)", "General Knowledge & Current Affairs (30 Marks)", "Reasoning & Analytical Ability (20 Marks)", "Elementary Mathematics (20 Marks)"),
        notificationCount = 1,
        sortOrder = 3
      ),
      ExamCategory(
        id = "jeca",
        title = "JECA 2026",
        subtitle = "Joint Entrance for MCA",
        bengaliTitle = "জেকা ২০২৬",
        bengaliSubtitle = "মাস্টার অব কম্পিউটার অ্যাপ্লিকেশন",
        iconKey = "computer",
        bgHex = 0xFFF3E5F5, // Pastel Purple
        iconTintHex = 0xFF7B1FA2,
        badgeText = "Exam Date Soon",
        description = "State-level common entrance examination for admission into Master of Computer Application (MCA) courses in West Bengal.",
        syllabusSummary = listOf("Mathematics (100 Marks)", "Computer Aptitude (100 Marks)"),
        notificationCount = 1,
        sortOrder = 4
      ),
      ExamCategory(
        id = "jelet",
        title = "JELET 2026",
        subtitle = "Lateral Entry Engineering",
        bengaliTitle = "জিলেট ২০২৬",
        bengaliSubtitle = "ল্যাটারাল এন্ট্রি ইঞ্জিনিয়ারিং",
        iconKey = "engineering",
        bgHex = 0xFFFFF8E1, // Pastel Amber
        iconTintHex = 0xFFFFA000,
        badgeText = "Free Test",
        description = "Joint Entrance Examination for Lateral Entry into 2nd year (3rd semester) of Degree level Engineering & Technology courses.",
        syllabusSummary = listOf("Mathematics (40 Marks)", "Physics (30 Marks)", "Chemistry (20 Marks)", "Fundamentals of Electrical & Electronics (10 Marks)"),
        notificationCount = 0,
        sortOrder = 5
      ),
      ExamCategory(
        id = "jemscn",
        title = "JEMScN 2026",
        subtitle = "M.Sc Nursing Entrance",
        bengaliTitle = "জেমএসসিএন ২০২৬",
        bengaliSubtitle = "এম.এসসি নার্সিং প্রবেশিকা",
        iconKey = "school",
        bgHex = 0xFFE8F5E9, // Pastel Green
        iconTintHex = 0xFF388E3C,
        badgeText = "Mock Series",
        description = "State-level entrance exam for admission into M.Sc Nursing courses in government and private colleges in West Bengal.",
        syllabusSummary = listOf("Medical Surgical Nursing (30)", "Community Health Nursing (25)", "Obstetrics & Gynaecological Nursing (25)", "Child Health & Mental Health (20)"),
        notificationCount = 1,
        sortOrder = 6
      ),
      ExamCategory(
        id = "wb_cho",
        title = "WB CHO 2026",
        subtitle = "Community Health Officer",
        bengaliTitle = "ডব্লিউবি সিএইচও ২০২৬",
        bengaliSubtitle = "কমিউনিটি হেলথ অফিসার",
        iconKey = "health_and_safety",
        bgHex = 0xFFE0F7FA, // Pastel Cyan
        iconTintHex = 0xFF0097A7,
        badgeText = "Merit List Out",
        description = "Recruitment by West Bengal State Health & Family Welfare Samiti for Community Health Officers (CHO) under National Health Mission (NHM).",
        syllabusSummary = listOf("Maternal & Child Health (30)", "Communicable & Non-Communicable Diseases (25)", "National Health Programmes (20)", "Basic Nursing & Pharmacology (15)", "General Knowledge & Computer (10)"),
        notificationCount = 4,
        sortOrder = 7
      ),
      ExamCategory(
        id = "norcet",
        title = "NORCET 2026",
        subtitle = "AIIMS Nursing Officer",
        bengaliTitle = "নরসেট ২০২৬",
        bengaliSubtitle = "এইমস নার্সিং অফিসার রিক্রুটমেন্ট",
        iconKey = "vaccines",
        bgHex = 0xFFFBE9E7, // Pastel Orange
        iconTintHex = 0xFFD84315,
        badgeText = "Stage 1 Active",
        description = "Nursing Officer Recruitment Common Eligibility Test conducted by AIIMS New Delhi for recruitment across all premier AIIMS hospitals in India.",
        syllabusSummary = listOf("Nursing Subject Domains (80% Weightage)", "General Knowledge, Aptitude & Basic English (20% Weightage)"),
        notificationCount = 2,
        sortOrder = 8
      ),
      ExamCategory(
        id = "wbhrb_staff_nurse",
        title = "WBHRB Staff Nurse",
        subtitle = "Grade II Recruitment",
        bengaliTitle = "ডব্লিউবিএইচআরবি স্টাফ নার্স",
        bengaliSubtitle = "গ্রেড ২ নিয়োগ পরীক্ষা",
        iconKey = "badge",
        bgHex = 0xFFE8EAF6, // Pastel Indigo
        iconTintHex = 0xFF303F9F,
        badgeText = "Question Bank",
        description = "West Bengal Health Recruitment Board exam for Staff Nurse Grade II vacancies across WB State Health Services.",
        syllabusSummary = listOf("Anatomy & Physiology", "Fundamentals of Nursing", "Medical-Surgical Nursing", "Pharmacology & Microbiology", "Psychiatric & Community Health"),
        notificationCount = 1,
        sortOrder = 9
      ),
      ExamCategory(
        id = "wbhrb_pharmacist",
        title = "WBHRB Pharmacist",
        subtitle = "Grade III Recruitment",
        bengaliTitle = "ডব্লিউবিএইচআরবি ফার্মাসিস্ট",
        bengaliSubtitle = "গ্রেড ৩ নিয়োগ পরীক্ষা",
        iconKey = "medication",
        bgHex = 0xFFFFF3E0,
        iconTintHex = 0xFFE65100,
        badgeText = "PYQ Available",
        description = "Recruitment examination for Pharmacist Grade III under Directorate of Health Services, Government of West Bengal.",
        syllabusSummary = listOf("Pharmaceutics", "Pharmacology & Toxicology", "Pharmaceutical Chemistry", "Pharmacognosy", "Drug Store & Business Management"),
        notificationCount = 0,
        sortOrder = 10
      ),
      ExamCategory(
        id = "rrb_nursing",
        title = "RRB Nursing Supdt.",
        subtitle = "Railway Recruitment Board",
        bengaliTitle = "আরআরবি নার্সিং সুপারিন্টেন্ডেন্ট",
        bengaliSubtitle = "রেলওয়ে রিক্রুটমেন্ট বোর্ড",
        iconKey = "directions_railway",
        bgHex = 0xFFEDE7F6,
        iconTintHex = 0xFF512DA8,
        badgeText = "Central Govt",
        description = "Indian Railways paramedical recruitment for Nursing Superintendent positions across all Railway zones.",
        syllabusSummary = listOf("Professional Nursing Ability (70)", "General Awareness (10)", "General Arithmetic & Reasoning (10)", "General Science (10)"),
        notificationCount = 1,
        sortOrder = 11
      ),
      ExamCategory(
        id = "rrb_pharmacist",
        title = "RRB Pharmacist",
        subtitle = "Railway Paramedical",
        bengaliTitle = "আরআরবি ফার্মাসিস্ট",
        bengaliSubtitle = "রেলওয়ে প্যারামেডিক্যাল",
        iconKey = "pill",
        bgHex = 0xFFF1F8E9,
        iconTintHex = 0xFF558B2F,
        badgeText = "High Vacancy",
        description = "Indian Railways Paramedical Staff recruitment examination for qualified Pharmacists.",
        syllabusSummary = listOf("Pharmacy Professional Topics (70)", "General Awareness & Science (30)"),
        notificationCount = 0,
        sortOrder = 12
      ),
      ExamCategory(
        id = "neet_ug",
        title = "NEET UG",
        subtitle = "Medical Entrance (MBBS/BDS)",
        bengaliTitle = "নিট ইউজি",
        bengaliSubtitle = "মেডিক্যাল প্রবেশিকা",
        iconKey = "biotech",
        bgHex = 0xFFFFEBEE,
        iconTintHex = 0xFFC62828,
        badgeText = "720 Marks",
        description = "National Eligibility cum Entrance Test conducted by NTA for admission to undergraduate medical and dental programs across India.",
        syllabusSummary = listOf("Biology - Botany & Zoology (360)", "Physics (180)", "Chemistry (180)"),
        notificationCount = 2,
        sortOrder = 13
      ),
      ExamCategory(
        id = "wbssc_group_cd",
        title = "WBSSC Group C & D",
        subtitle = "School Service Commission",
        bengaliTitle = "ডব্লিউবিএসএসসি গ্রুপ সি ও ডি",
        bengaliSubtitle = "স্কুল সার্ভিস কমিশন",
        iconKey = "menu_book",
        bgHex = 0xFFF9FBE7,
        iconTintHex = 0xFF827717,
        badgeText = "Preparation Live",
        description = "Non-teaching staff recruitment in recognized non-government aided schools in West Bengal.",
        syllabusSummary = listOf("General Knowledge", "Current Affairs", "General English", "Arithmetic"),
        notificationCount = 1,
        sortOrder = 14
      ),
      ExamCategory(
        id = "jepbn",
        title = "JEPBN 2026",
        subtitle = "Post Basic B.Sc Nursing",
        bengaliTitle = "জেপিবিন ২০২৬",
        bengaliSubtitle = "পোস্ট বেসিক বি.এসসি নার্সিং",
        iconKey = "workspace_premium",
        bgHex = 0xFFE1F5FE,
        iconTintHex = 0xFF0277BD,
        badgeText = "Degree Nursing",
        description = "West Bengal Joint Entrance for admission to Post Basic B.Sc Nursing degree courses for registered GNM nurses.",
        syllabusSummary = listOf("Anatomy, Physiology & Microbiology", "Community Health & Midwifery", "Medical-Surgical & Pediatric Nursing"),
        notificationCount = 0,
        sortOrder = 15
      )
    )
  )
  val categories: StateFlow<List<ExamCategory>> = _categories.asStateFlow()

  private val _mockTests = MutableStateFlow<List<MockTest>>(createInitialMockTests())
  val mockTests: StateFlow<List<MockTest>> = _mockTests.asStateFlow()

  private val _studyMaterials = MutableStateFlow<List<StudyMaterial>>(createInitialStudyMaterials())
  val studyMaterials: StateFlow<List<StudyMaterial>> = _studyMaterials.asStateFlow()

  private val _testAttempts = MutableStateFlow<List<TestAttempt>>(createSampleAttempts())
  val testAttempts: StateFlow<List<TestAttempt>> = _testAttempts.asStateFlow()

  private val _adminMetrics = MutableStateFlow(AdminMetric())
  val adminMetrics: StateFlow<AdminMetric> = _adminMetrics.asStateFlow()

  private val _paymentOrders = MutableStateFlow<List<PaymentOrder>>(
    listOf(
      PaymentOrder(
        orderId = "order_rzp_wb8912",
        amountInRupees = 199,
        status = "SUCCESS",
        paymentMethod = "UPI (Google Pay)",
        timestamp = System.currentTimeMillis() - 86400000L * 2
      )
    )
  )
  val paymentOrders: StateFlow<List<PaymentOrder>> = _paymentOrders.asStateFlow()

  init {
    try {
      _studyMaterials.value.forEach { mat ->
        WbFirestoreService.saveStudyResource(mat.toStudyResource())
      }
    } catch (_: Exception) {}

    // Populate local SQL (Room SQLite) database
    WbTopperDatabase.get()?.let { db ->
      CoroutineScope(Dispatchers.IO).launch {
        try {
          val resourceEntities = _studyMaterials.value.map {
            StudyResourceEntity.fromStudyResource(it.toStudyResource())
          }
          db.studyResourceDao().insertResources(resourceEntities)

          val categoryEntities = _categories.value.map {
            ExamCategoryEntity.fromExamCategory(it)
          }
          db.examCategoryDao().insertCategories(categoryEntities)
          Log.d("WbTopperRepository", "Seeded local SQL database with initial resources and categories")
        } catch (e: Exception) {
          Log.w("WbTopperRepository", "Error syncing to local SQL: ${e.message}")
        }
      }
    }
  }

  // Actions
  fun toggleLanguage() {
    _currentLanguage.update {
      if (it == AppLanguage.ENGLISH) AppLanguage.BENGALI else AppLanguage.ENGLISH
    }
  }

  fun toggleTheme() {
    _isDarkTheme.update { !it }
  }

  fun switchUserRole(role: UserRole) {
    _currentUser.update { it.copy(role = role) }
  }

  fun setSelectedCategory(categoryId: String) {
    val category = _categories.value.find { it.id == categoryId }
    _currentUser.update {
      it.copy(
        selectedCategoryId = categoryId,
        targetExamName = category?.title ?: it.targetExamName
      )
    }
  }

  fun updateStudentProfile(name: String, phone: String) {
    _currentUser.update { it.copy(name = name, phone = phone) }
    WbFirestoreService.saveUserProfile(_currentUser.value)
  }

  fun setAuthenticatedUser(name: String, email: String, phone: String, targetExamId: String) {
    val category = _categories.value.find { it.id == targetExamId }
    val updatedUser = _currentUser.value.copy(
      name = name,
      email = email,
      phone = phone,
      selectedCategoryId = targetExamId,
      targetExamName = category?.title ?: _currentUser.value.targetExamName,
      role = UserRole.STUDENT
    )
    _currentUser.value = updatedUser
    WbFirestoreService.saveUserProfile(updatedUser)
  }

  fun logoutStudent() {
    _currentUser.update {
      UserProfile(
        id = "student_guest",
        name = "Student Aspirant",
        email = "student@wbtopper.com",
        phone = "+91 98000 00000",
        role = UserRole.STUDENT,
        selectedCategoryId = "jenpas_ug",
        isPremium = false,
        targetExamName = "JENPAS-UG 2026"
      )
    }
  }

  fun upgradeToPremium(paymentMethod: String = "UPI (Google Pay)"): PaymentOrder {
    val order = PaymentOrder(
      orderId = "order_rzp_" + (1000..9999).random(),
      amountInRupees = 199,
      status = "SUCCESS",
      paymentMethod = paymentMethod,
      timestamp = System.currentTimeMillis(),
      planName = "WBTOPPER All-Access Premium Pass (1 Year)"
    )
    _paymentOrders.update { listOf(order) + it }
    _currentUser.update {
      it.copy(
        isPremium = true,
        premiumExpiryDate = "30-Sep-2027"
      )
    }
    WbFirestoreService.saveUserProfile(_currentUser.value)
    _adminMetrics.update {
      it.copy(
        activePremium = it.activePremium + 1,
        totalRevenueInr = it.totalRevenueInr + 199
      )
    }
    return order
  }

  fun submitTestAttempt(attempt: TestAttempt) {
    _testAttempts.update { listOf(attempt) + it }
    _adminMetrics.update {
      it.copy(totalAttempts = it.totalAttempts + 1)
    }
    // 1. Sync to Cloud Firestore
    WbFirestoreService.saveTestAttempt(_currentUser.value.id, attempt)

    // 2. Persist to local SQL (Room SQLite) database
    WbTopperDatabase.get()?.let { db ->
      CoroutineScope(Dispatchers.IO).launch {
        try {
          db.testAttemptDao().insertAttempt(TestAttemptEntity.fromTestAttempt(attempt))
        } catch (e: Exception) {
          Log.w("WbTopperRepository", "Failed to cache attempt in SQL: ${e.message}")
        }
      }
    }
  }

  // Admin Operations
  fun addCategory(category: ExamCategory) {
    _categories.update { it + category }
    WbFirestoreService.saveExamCategory(category)
    WbTopperDatabase.get()?.let { db ->
      CoroutineScope(Dispatchers.IO).launch {
        try {
          db.examCategoryDao().insertCategory(ExamCategoryEntity.fromExamCategory(category))
        } catch (_: Exception) {}
      }
    }
  }

  fun updateCategory(updated: ExamCategory) {
    _categories.update { list ->
      list.map { if (it.id == updated.id) updated else it }
    }
  }

  fun deleteCategory(categoryId: String) {
    _categories.update { list -> list.filterNot { it.id == categoryId } }
  }

  fun updateFeaturedAlert(title: String, titleBn: String, description: String) {
    _featuredAlert.update {
      it.copy(
        title = title,
        titleBn = titleBn,
        description = description,
        date = "Just Now"
      )
    }
  }

  fun addMockTest(mockTest: MockTest) {
    _mockTests.update { listOf(mockTest) + it }
    _adminMetrics.update { it.copy(totalMockTests = it.totalMockTests + 1) }
  }

  fun addStudyMaterial(material: StudyMaterial) {
    _studyMaterials.update { listOf(material) + it }
    _adminMetrics.update { it.copy(totalStudyMaterials = it.totalStudyMaterials + 1) }
  }

  fun toggleStudentPremiumByAdmin(userId: String, isPremium: Boolean) {
    _currentUser.update { it.copy(isPremium = isPremium) }
  }

  companion object {
    private fun createInitialMockTests(): List<MockTest> {
      return listOf(
        MockTest(
          id = "test_jenpas_01",
          categoryId = "jenpas_ug",
          categoryName = "JENPAS-UG 2026",
          title = "JENPAS-UG Full-Length Mock Test 1 (All Bengal Free)",
          titleBn = "জেনপাস-ইউজি সম্পূর্ণ মক টেস্ট ১ (বিনামূল্যে)",
          durationMinutes = 90,
          totalQuestions = 10,
          totalMarks = 10.0f,
          positiveMark = 1.0f,
          negativeMark = 0.25f,
          isPremium = false,
          difficulty = "Medium",
          attemptsCount = 4210,
          questions = getSampleJenpasQuestions()
        ),
        MockTest(
          id = "test_jenpas_02",
          categoryId = "jenpas_ug",
          categoryName = "JENPAS-UG 2026",
          title = "JENPAS-UG Premium Clinical & Biology Challenger 2",
          titleBn = "জেনপাস-ইউজি প্রিমিয়াম ক্লিনিক্যাল ও বায়োলজি টেস্ট ২",
          durationMinutes = 90,
          totalQuestions = 10,
          totalMarks = 10.0f,
          positiveMark = 1.0f,
          negativeMark = 0.25f,
          isPremium = true,
          difficulty = "Hard",
          attemptsCount = 1890,
          questions = getSampleJenpasQuestions()
        ),
        MockTest(
          id = "test_anm_01",
          categoryId = "anm_gnm",
          categoryName = "ANM & GNM 2026",
          title = "ANM & GNM Life Science & General Knowledge Free Test",
          titleBn = "এএনএম ও জিএনএম জীবন বিজ্ঞান ও সাধারণ জ্ঞান টেস্ট",
          durationMinutes = 60,
          totalQuestions = 10,
          totalMarks = 10.0f,
          positiveMark = 1.0f,
          negativeMark = 0.25f,
          isPremium = false,
          difficulty = "Easy",
          attemptsCount = 6840,
          questions = getSampleAnmQuestions()
        ),
        MockTest(
          id = "test_cho_01",
          categoryId = "wb_cho",
          categoryName = "WB CHO 2026",
          title = "WB CHO NHM Community Health Protocol Practice Test",
          titleBn = "ডব্লিউবি সিএইচও এনএইচএম স্বাস্থ্য প্রটোকল মক টেস্ট",
          durationMinutes = 60,
          totalQuestions = 10,
          totalMarks = 10.0f,
          positiveMark = 1.0f,
          negativeMark = 0.25f,
          isPremium = false,
          difficulty = "Medium",
          attemptsCount = 3120,
          questions = getSampleChoQuestions()
        ),
        MockTest(
          id = "test_norcet_01",
          categoryId = "norcet",
          categoryName = "NORCET 2026",
          title = "NORCET AIIMS Nursing Officer Clinical Scenario Test",
          titleBn = "নরসেট এইমস নার্সিং অফিসার ক্লিনিক্যাল মক টেস্ট",
          durationMinutes = 90,
          totalQuestions = 10,
          totalMarks = 10.0f,
          positiveMark = 1.0f,
          negativeMark = 0.33f,
          isPremium = true,
          difficulty = "Hard",
          attemptsCount = 2430,
          questions = getSampleNorcetQuestions()
        )
      )
    }

    private fun getSampleJenpasQuestions(): List<Question> {
      return listOf(
        Question(
          id = "q_j1",
          questionNumber = 1,
          questionText = "Which hormone stimulates the secretion of milk from mammary glands after childbirth?",
          questionTextBn = "সন্তান প্রসবের পর স্তনগ্রন্থি থেকে দুগ্ধ ক্ষরণে কোন হরমোন উদ্দীপনা জোগায়?",
          options = listOf("Prolactin", "Oxytocin", "Estrogen", "Progesterone"),
          optionsBn = listOf("প্রোল্যাকটিন", "অক্সিটোসিন", "ইস্ট্রোজেন", "প্রোজেস্টেরন"),
          correctOptionIndex = 0,
          explanation = "Prolactin, synthesized by the anterior pituitary gland, stimulates milk production. (Oxytocin causes milk ejection/letdown reflex).",
          explanationBn = "অ্যান্টেরিয়র পিটুইটারি থেকে নিঃসৃত প্রোল্যাকটিন দুধ তৈরিতে সাহায্য করে। অক্সিটোসিন দুধ নির্গমনে (letdown reflex) সাহায্য করে।",
          subject = "Biology",
          topic = "Human Reproduction & Endocrine",
          clinicalScenario = "A 24-year-old primigravida mother has delivered a healthy baby and presents on day 2 asking how lactation begins."
        ),
        Question(
          id = "q_j2",
          questionNumber = 2,
          questionText = "What is the normal glomerular filtration rate (GFR) in a healthy human adult?",
          questionTextBn = "একজন সুস্থ প্রাপ্তবয়স্ক মানুষের স্বাভাবিক গ্লোমেরুলার পরিস্রাবণ হার (GFR) কত?",
          options = listOf("125 mL/min", "180 mL/min", "60 mL/min", "250 mL/min"),
          optionsBn = listOf("১২৫ মিলি/মিনিট", "১৮০ মিলি/মিনিট", "৬০ মিলি/মিনিট", "২৫০ মিলি/মিনিট"),
          correctOptionIndex = 0,
          explanation = "In healthy adults, normal GFR is approximately 125 mL/min (which totals about 180 Liters per day).",
          explanationBn = "স্বস্থ প্রাপ্তবয়স্কদের স্বাভাবিক GFR প্রায় ১২৫ মিলি/মিনিট (দিনে প্রায় ১৮০ লিটার)।",
          subject = "Biology",
          topic = "Excretory Products & Elimination"
        ),
        Question(
          id = "q_j3",
          questionNumber = 3,
          questionText = "Which cranial nerve is primarily responsible for the parasympathetic control of heart rate and digestive organs?",
          questionTextBn = "কোন করোটিক স্নায়ু মূলত হৃৎস্পন্দন এবং পাচনতন্ত্রের প্যারাসিমপ্যাথেটিক নিয়ন্ত্রণের জন্য দায়ী?",
          options = listOf("Trigeminal Nerve (CN V)", "Vagus Nerve (CN X)", "Facial Nerve (CN VII)", "Glossopharyngeal Nerve (CN IX)"),
          optionsBn = listOf("ট্রাইজেমিনাল (CN V)", "ভেগাস স্নায়ু (CN X)", "ফ্যাসিয়াল (CN VII)", "গ্লসোফ্যারিঞ্জিয়াল (CN IX)"),
          correctOptionIndex = 1,
          explanation = "The Vagus nerve (Cranial Nerve X) provides the chief parasympathetic innervation to the heart, lungs, and gastrointestinal tract.",
          explanationBn = "ভেগাস নার্ভ (১০ম করোটিক স্নায়ু) হৃৎপিণ্ড, ফুসফুস এবং পাচনতন্ত্রে প্যারাসিমপ্যাথেটিক নিয়ন্ত্রণ প্রদান করে।",
          subject = "Biology",
          topic = "Neural Control & Coordination"
        ),
        Question(
          id = "q_j4",
          questionNumber = 4,
          questionText = "What is the pH of human arterial blood under normal physiological conditions?",
          questionTextBn = "স্বাভাবিক শারীরবৃত্তীয় অবস্থায় মানুষের ধমনীর রক্তের pH কত?",
          options = listOf("6.85 – 7.00", "7.35 – 7.45", "7.55 – 7.65", "8.00 – 8.20"),
          optionsBn = listOf("৬.৮৫ – ৭.০০", "৭.৩৫ – ৭.৪৫", "৭.৫৫ – ৭.৬৫", "৮.০০ – ৮.২০"),
          correctOptionIndex = 1,
          explanation = "Normal arterial blood pH ranges strictly between 7.35 and 7.45. Any deviation below 7.35 is acidosis, and above 7.45 is alkalosis.",
          explanationBn = "ধমনীর রক্তের স্বাভাবিক pH মান ৭.৩৫ থেকে ৭.৪৫-এর মধ্যে থাকে।",
          subject = "Physiology / Chemistry",
          topic = "Body Fluids & Acid-Base Balance"
        ),
        Question(
          id = "q_j5",
          questionNumber = 5,
          questionText = "Which vitamin is synthesized by bacterial flora in the human large intestine and is vital for blood coagulation factors II, VII, IX, and X?",
          questionTextBn = "কোন ভিটামিন বৃহদন্ত্রের ব্যাকটেরিয়া দ্বারা তৈরি হয় এবং রক্ত জমাট বাঁধার ফ্যাক্টর II, VII, IX, X-এর জন্য প্রয়োজনীয়?",
          options = listOf("Vitamin K", "Vitamin D", "Vitamin C", "Vitamin B12"),
          optionsBn = listOf("ভিটামিন কে", "ভিটামিন ডি", "ভিটামিন সি", "ভিটামিন বি১২"),
          correctOptionIndex = 0,
          explanation = "Vitamin K is synthesized by gut bacteria (E. coli) and is essential for hepatic synthesis of clotting factors II (prothrombin), VII, IX, and X.",
          explanationBn = "ভিটামিন কে অন্ত্রের স্বাভাবিক ফ্লোরা দ্বারা সংশ্লেষিত হয় এবং লিভারে ক্লটিং ফ্যাক্টর তৈরিতে অপরিহার্য।",
          subject = "Biology",
          topic = "Biomolecules & Nutrition"
        ),
        Question(
          id = "q_j6",
          questionNumber = 6,
          questionText = "In clinical practice, which solution is considered isotonic to human red blood cells?",
          questionTextBn = "চিকিৎসা বিজ্ঞানে মানুষের লোহিত রক্তকণিকার সাথে কোন দ্রবণটি আইসোটনিক?",
          options = listOf("0.9% Normal Saline (NaCl)", "5% Normal Saline", "0.45% Saline", "Distilled Water"),
          optionsBn = listOf("০.৯% নরমাল স্যালাইন", "৫% সোডিয়াম ক্লোরাইড", "০.৪৫% স্যালাইন", "পাতিত জল"),
          correctOptionIndex = 0,
          explanation = "0.9% NaCl (Normal Saline) has an osmolarity (~308 mOsm/L) close to human plasma, preventing hemolysis or crenation of RBCs.",
          explanationBn = "০.৯% নরমাল স্যালাইন রক্তের সাথে সম-অভিস্রবণীয় (আইসোটনিক), যা আরবিসি ভাঙতে দেয় না।",
          subject = "Chemistry / Nursing",
          topic = "Solutions & IV Fluid Management"
        ),
        Question(
          id = "q_j7",
          questionNumber = 7,
          questionText = "Which part of the human brain controls vital vegetative functions such as respiration, cardiac center, and blood pressure?",
          questionTextBn = "মানুষের মস্তিষ্কের কোন অংশ শ্বাসক্রিয়া, হৃদস্পন্দন ও রক্তচাপের মতো অত্যাবশ্যক ক্রিয়া নিয়ন্ত্রণ করে?",
          options = listOf("Cerebellum", "Medulla Oblongata", "Hypothalamus", "Cerebrum"),
          optionsBn = listOf("সেরিবেলাম", "মেডুলা অবলংগাটা", "হাইপোথ্যালামাস", "সেরিব্রাম"),
          correctOptionIndex = 1,
          explanation = "The Medulla Oblongata contains the cardiovascular center, medullary rhythmicity area for breathing, and vasomotor centers.",
          explanationBn = "মেডুলা অবলংগাটা কার্ডিয়াক ও রেসপিরেটরি নিয়ন্ত্রণের কেন্দ্রস্থল।",
          subject = "Biology",
          topic = "Human Nervous System"
        ),
        Question(
          id = "q_j8",
          questionNumber = 8,
          questionText = "Select the antonym for the word 'BENIGN' commonly used in oncology and medicine:",
          questionTextBn = "'BENIGN' শব্দের বিপরীতার্থক শব্দ কোনটি?",
          options = listOf("Malignant", "Gentle", "Harmless", "Curable"),
          optionsBn = listOf("ম্যালিগন্যান্ট (Malignant)", "কোমল (Gentle)", "ক্ষতিহীন (Harmless)", "নিরাময়যোগ্য (Curable)"),
          correctOptionIndex = 0,
          explanation = "In clinical terminology, 'Benign' means non-cancerous/mild, whereas 'Malignant' indicates invasive, cancerous tumor growth.",
          explanationBn = "'Benign' মানে অহিংস বা নিরীহ টিউমার, যার বিপরীত শব্দ হলো 'Malignant' (ক্যান্সারযুক্ত)।",
          subject = "English",
          topic = "Vocabulary & Medical Antonyms"
        ),
        Question(
          id = "q_j9",
          questionNumber = 9,
          questionText = "If A is taller than B, but shorter than C; and D is taller than E but shorter than B, who is the tallest among them all?",
          questionTextBn = "যদি A, B-এর চেয়ে লম্বা কিন্তু C-এর চেয়ে খাটো হয়; এবং D, E-এর চেয়ে লম্বা কিন্তু B-এর চেয়ে খাটো হয়, তবে তাদের মধ্যে সবচেয়ে লম্বা কে?",
          options = listOf("C", "A", "B", "D"),
          optionsBn = listOf("C", "A", "B", "D"),
          correctOptionIndex = 0,
          explanation = "Ordering: C > A > B > D > E. Therefore, C is the tallest person.",
          explanationBn = "ক্রম: C > A > B > D > E। সুতরাং C সবার চেয়ে লম্বা।",
          subject = "Logical Reasoning",
          topic = "Ranking & Ordering"
        ),
        Question(
          id = "q_j10",
          questionNumber = 10,
          questionText = "A patient receiving IV Potassium Chloride (KCl) infusion must NEVER receive it via which route?",
          questionTextBn = "পটাশিয়াম ক্লোরাইড (KCl) ইনফিউশন গ্রহণকারী রোগীকে কখনই কোন উপায়ে তা দেওয়া যাবে না?",
          options = listOf("Rapid IV Push / Bolus", "Slow IV infusion diluted in 500 mL saline", "Piggyback via infusion pump", "Central venous line infusion"),
          optionsBn = listOf("দ্রুত আইভি পুশ / বোলাস (Rapid IV Push)", "ধীর গতির আইভি স্যালাইন মিশ্রণ", "পাম্পের মাধ্যমে নিয়ন্ত্রণ", "সেন্ট্রাল লাইন ইনফিউশন"),
          correctOptionIndex = 0,
          explanation = "IV Potassium Chloride given as an IV push causes instantaneous fatal cardiac arrest. It must always be diluted and given slowly.",
          explanationBn = "পটাশিয়াম ক্লোরাইড সরাসরি শিরায় পুশ (IV Push) করলে সাথে সাথে হার্ট বন্ধ হয়ে মারাত্মক মৃত্যু ঘটতে পারে। এটি সর্বদা ধীরে ধীরে ডাইলিউশন করে দিতে হয়।",
          subject = "Nursing Foundations",
          topic = "Pharmacology & Patient Safety",
          clinicalScenario = "Staff nurse is preparing IV medications for an acute hypokalemic patient."
        )
      )
    }

    private fun getSampleAnmQuestions(): List<Question> {
      return listOf(
        Question(
          id = "q_anm_1",
          questionNumber = 1,
          questionText = "What is the duration of an uncomplicated normal human pregnancy (gestational period)?",
          questionTextBn = "মানুষের স্বাভাবিক গর্ভধারণ কাল (Gestational period) কত দিন?",
          options = listOf("280 days (40 weeks)", "250 days", "300 days", "365 days"),
          optionsBn = listOf("২৮০ দিন (৪০ সপ্তাহ)", "২৫০ দিন", "৩০০ দিন", "৩৬৫ দিন"),
          correctOptionIndex = 0,
          explanation = "Normal human pregnancy lasts about 280 days or 40 weeks calculated from the first day of the last menstrual period (LMP).",
          explanationBn = "শেষ মাসিকের প্রথম দিন থেকে গণনা করলে স্বাভাবিক গর্ভধারণের মেয়াদ প্রায় ২৮০ দিন বা ৪০ সপ্তাহ।",
          subject = "Life Science",
          topic = "Human Reproduction"
        ),
        Question(
          id = "q_anm_2",
          questionNumber = 2,
          questionText = "Which cell organelle is universally known as the 'Powerhouse of the Cell'?",
          questionTextBn = "কোষের 'শক্তিঘর' (Powerhouse of the cell) কাকে বলা হয়?",
          options = listOf("Mitochondria", "Ribosome", "Golgi Body", "Lysosome"),
          optionsBn = listOf("মাইটোকনড্রিয়া", "রাইবোজোম", "গলগি বস্তু", "লাইসোজোম"),
          correctOptionIndex = 0,
          explanation = "Mitochondria produce cellular energy in the form of ATP via aerobic respiration.",
          explanationBn = "মাইটোকনড্রিয়ায় শ্বসনের মাধ্যমে এটিপি (ATP) শক্তি উৎপন্ন হয়, তাই একে কোষের শক্তিঘর বলে।",
          subject = "Life Science",
          topic = "Cell Biology"
        ),
        Question(
          id = "q_anm_3",
          questionNumber = 3,
          questionText = "Deficiency of Vitamin C leads to which clinical condition?",
          questionTextBn = "ভিটামিন সি-এর অভাবে কোন রোগ হয়?",
          options = listOf("Scurvy", "Rickets", "Beriberi", "Night Blindness"),
          optionsBn = listOf("স্কার্ভি", "রিকেট", "বেরিবেরি", "রাতকানা"),
          correctOptionIndex = 0,
          explanation = "Vitamin C (Ascorbic acid) deficiency leads to Scurvy, characterized by bleeding gums and delayed wound healing.",
          explanationBn = "ভিটামিন সি-এর অভাবে মাড়ি দিয়ে রক্ত পড়া ও স্কার্ভি রোগ হয়।",
          subject = "Life Science",
          topic = "Vitamins & Deficiency Diseases"
        ),
        Question(
          id = "q_anm_4",
          questionNumber = 4,
          questionText = "Which blood group is known as the 'Universal Donor'?",
          questionTextBn = "কোন রক্তের গ্রুপকে 'সর্বজনীন দাতা' (Universal Donor) বলা হয়?",
          options = listOf("O negative (O -ve)", "AB positive (AB +ve)", "A positive", "B negative"),
          optionsBn = listOf("ও নেগেটিভ (O -ve)", "এবি পজিটিভ (AB +ve)", "এ পজিটিভ", "বি নেগেটিভ"),
          correctOptionIndex = 0,
          explanation = "O negative blood has neither A, B antigens nor Rh antigen on RBCs, making it safe to donate to all blood groups.",
          explanationBn = "O নেগেটিভ রক্তে কোনো A, B বা Rh অ্যান্টিজেন থাকে না, তাই যে কাউকে এটি দেওয়া যায়।",
          subject = "Life Science",
          topic = "Circulatory System & Blood"
        ),
        Question(
          id = "q_anm_5",
          questionNumber = 5,
          questionText = "Who was the first Chief Minister of West Bengal?",
          questionTextBn = "পশ্চিমবঙ্গের প্রথম মুখ্যমন্ত্রী কে ছিলেন?",
          options = listOf("Dr. Prafulla Chandra Ghosh", "Dr. Bidhan Chandra Roy", "Jyoti Basu", "Ajoy Mukherjee"),
          optionsBn = listOf("ড. প্রফুল্ল চন্দ্র ঘোষ", "ড. বিধান চন্দ্র রায়", "জ্যোতি বসু", "অজয় মুখোপাধ্যায়"),
          correctOptionIndex = 0,
          explanation = "Dr. Prafulla Chandra Ghosh was the first Chief Minister of West Bengal in 1947.",
          explanationBn = "১৯৪৭ সালে স্বাধীন ভারতের পশ্চিমবঙ্গের প্রথম মুখ্যমন্ত্রী ছিলেন ড. প্রফুল্ল চন্দ্র ঘোষ।",
          subject = "General Knowledge",
          topic = "West Bengal Heritage & History"
        )
      )
    }

    private fun getSampleChoQuestions(): List<Question> {
      return listOf(
        Question(
          id = "q_cho_1",
          questionNumber = 1,
          questionText = "Under the National Immunization Schedule (NIS) in India, at what age is the Pentavalent vaccine administered to infants?",
          questionTextBn = "ভারতে জাতীয় টিকাদান কর্মসূচি অনুযায়ী পেন্টাভ্যালেন্ট টিকা কোন কোন বয়সে দেওয়া হয়?",
          options = listOf("6, 10, and 14 weeks", "At birth, 1 month, and 6 months", "9 months and 16 months", "At 2, 4, and 6 months"),
          optionsBn = listOf("৬, ১০ এবং ১৪ সপ্তাহে", "জন্মলগ্নে, ১ মাস ও ৬ মাসে", "৯ মাস ও ১৬ মাসে", "২, ৪ ও ৬ মাসে"),
          correctOptionIndex = 0,
          explanation = "Pentavalent vaccine (Diphtheria, Pertussis, Tetanus, Hepatitis B, and Hib) is given at 6, 10, and 14 weeks of age.",
          explanationBn = "পেন্টাভ্যালেন্ট ভ্যাকসিন শিশুর ৬, ১০ ও ১৪ সপ্তাহ বয়সে ০.৫ মিলি ইন্ট্রামাসকুলার হিসেবে দেওয়া হয়।",
          subject = "Community Health Nursing",
          topic = "National Immunization Schedule"
        ),
        Question(
          id = "q_cho_2",
          questionNumber = 2,
          questionText = "What is the recommended dosage of elemental iron and folic acid for pregnant women under the Anemia Mukt Bharat initiative?",
          questionTextBn = "অ্যানিমিয়া মুক্ত ভারত প্রকল্পের অধীনে গর্ভবতী মহিলাদের প্রতিদিন কত পরিমাণ আয়রন ও ফলিক অ্যাসিডের ট্যাবলেট দেওয়া হয়?",
          options = listOf("60 mg elemental iron + 500 mcg folic acid daily for 180 days", "100 mg iron once weekly", "20 mg iron daily", "10 mg iron monthly"),
          optionsBn = listOf("৬০ মিলিগ্রাম আয়রন + ৫০০ মাইক্রোগ্রাম ফলিক অ্যাসিড প্রতিদিন ১৮০ দিন", "১০০ মিলিগ্রাম সপ্তাহে একবার", "২০ মিলিগ্রাম দৈনিক", "১০ মিলিগ্রাম মাসে একবার"),
          correctOptionIndex = 0,
          explanation = "Under Anemia Mukt Bharat, pregnant women are prescribed 1 IFA tablet containing 60 mg elemental iron and 500 mcg folic acid daily starting after the 1st trimester for at least 180 days.",
          explanationBn = "প্রথম ত্রৈমাসিক শেষের পর প্রতিদিন ১টি লাল রঙের আইএফএ ট্যাবলেট (৬০ মিলিগ্রাম আয়রন ও ৫০০ মাইক্রোগ্রাম ফলিক অ্যাসিড) কমপক্ষে ১৮০ দিন খেতে হয়।",
          subject = "Community Health",
          topic = "Maternal Health Programmes"
        )
      )
    }

    private fun getSampleNorcetQuestions(): List<Question> {
      return listOf(
        Question(
          id = "q_nor_1",
          questionNumber = 1,
          questionText = "A patient with traumatic head injury shows a Glasgow Coma Scale (GCS) score of 7. What is the immediate nursing priority?",
          questionTextBn = "মাথায় গুরুতর আঘাত পাওয়া রোগীর গ্লাসগো কোমা স্কেল (GCS) স্কোর ৭। নার্সিং দলের তাৎক্ষণিক অগ্রাধিকার কোনটি?",
          options = listOf("Airway protection and intubation preparation", "Immediate physical therapy", "Administer oral sedatives", "Discharge with oral analgesics"),
          optionsBn = listOf("বায়ুপথ সুরক্ষিত করা ও ইনটিউবেশনের প্রস্তুতি", "শারীরিক ব্যায়াম", "ঘুমের ওষুধ দেওয়া", "ছুটি দিয়ে দেওয়া"),
          correctOptionIndex = 0,
          explanation = "A GCS score <= 8 indicates severe brain injury and loss of protective airway reflexes ('GCS less than 8, intubate').",
          explanationBn = "GCS স্কোর ৮ বা তার কম হলে রোগীর এয়ারওয়ে বা শ্বাসপথ সুরক্ষিত থাকে না, অবিলম্বে এন্ডোট্র্যাকিয়াল ইনটিউবেশন প্রয়োজন।",
          subject = "Medical-Surgical Nursing",
          topic = "Neurology & Emergency Triage",
          clinicalScenario = "In the trauma ICU, a 30-year-old motor vehicle accident victim arrives unconscious with shallow breathing."
        )
      )
    }

    private fun createInitialStudyMaterials(): List<StudyMaterial> {
      return listOf(
        StudyMaterial(
          id = "pdf_01",
          categoryId = "jenpas_ug",
          categoryName = "JENPAS-UG 2026",
          title = "JENPAS-UG High-Yield Biology & Human Physiology Quick Revision Notes",
          titleBn = "জেনপাস-ইউজি জীববিদ্যা ও মানব শারীরবিদ্যা দ্রুত রিভিশন নোটস",
          subject = "Biology",
          topic = "Human Physiology & Genetics",
          pageCount = 48,
          fileSizeBytes = 4200000,
          isPremium = false,
          description = "Master key NCERT concepts, high-frequency diagrams, endocrine hormones, and previous 5 years repeat concepts for JENPAS-UG.",
          language = "English & Bengali",
          contentMarkdown = """
# JENPAS-UG High-Yield Biology & Physiology
**Target Examination**: JENPAS-UG 2026 (Nursing & Paramedical)
**Publisher**: WBTOPPER Editorial Board

---

## 1. Endocrine System & Hormone Actions
- **Pituitary Gland**: Located in sella turcica of sphenoid bone.
  - *Growth Hormone (GH)*: Excess causes Acromegaly in adults, Gigantism in children.
  - *Prolactin (PRL)*: Stimulates milk synthesis in alveolar mammary tissue.
  - *Oxytocin (OT)*: Produced in hypothalamus, stored in posterior pituitary. Causes uterine contractions during labor and milk ejection (let-down reflex).
  - *Antidiuretic Hormone (ADH/Vasopressin)*: Stimulates water reabsorption at Distal Convoluted Tubule (DCT) and Collecting Duct. Deficiency causes **Diabetes Insipidus** (polyuria without hyperglycemia).

---

## 2. Cardiovascular Physiology & Circulation
- Normal Cardiac Output = Stroke Volume (70 mL) × Heart Rate (72 bpm) ≈ **5 Liters/min**.
- Blood Pressure: Systolic 120 mmHg / Diastolic 80 mmHg.
- **Heart Sounds**:
  - *S1 (LUBB)*: Closure of AV valves (Tricuspid & Mitral).
  - *S2 (DUPP)*: Closure of Semilunar valves (Aortic & Pulmonary).
- **Pacemaker of Heart**: SA Node (Sinoatrial node) generating 70-75 impulses/min.

---

## 3. Excretory System Pearls
- Functional unit: **Nephron** (approx. 1 million per kidney).
- GFR = 125 mL/min = 180 Liters/day.
- 99% of filtrate is reabsorbed through PCT, Loop of Henle, DCT.
- Podocytes are found in the visceral layer of Bowman's Capsule creating filtration slits.

---

## 4. Genetics & Molecular Biology Quick Points
- DNA replication is **semi-conservative** (demonstrated by Meselson & Stahl using 15N).
- Start Codon: **AUG** (codes for Methionine).
- Stop Codons: **UAA, UAG, UGA**.
- Klinefelter's Syndrome: 47, XXY (tall stature, gynecomastia, sterile male).
- Turner's Syndrome: 45, XO (short stature, webbed neck, sterile female).
          """.trimIndent()
        ),
        StudyMaterial(
          id = "pdf_02",
          categoryId = "anm_gnm",
          categoryName = "ANM & GNM 2026",
          title = "ANM & GNM 2026 Complete Life Science Chapter-Wise Guide (Bengali & English)",
          titleBn = "এএনএম ও জিএনএম ২০২৬ সম্পূর্ণ জীবন বিজ্ঞান অধ্যায়ভিত্তিক গাইড",
          subject = "Life Science",
          topic = "Class 9 & 10 Life Science Syllabus",
          pageCount = 65,
          fileSizeBytes = 5800000,
          isPremium = false,
          description = "Covers cell division, genetics, plant & animal hormones, senses, and nutrition tailored for ANM & GNM West Bengal candidates.",
          language = "Bengali & English",
          contentMarkdown = """
# এএনএম ও জিএনএম ২০২৬ জীবন বিজ্ঞান হ্যান্ডবুক
**WBTOPPER এক্সক্লুসিভ স্টাডি ম্যাটেরিয়াল**

---

## ১. কোষ ও কোষ বিভাজন (Cell & Cell Division)
- মাইটোসিস কোষ বিভাজনের পর্যায়: প্রোফেজ, মেটাফেজ, অ্যানাফেজ, টেলোফেজ।
- মেটাফেজ দশায় ক্রোমোজোমগুলি বিষুব অঞ্চলে বিন্যস্ত থাকে এবং ক্রোমোজোমের গঠন স্পষ্টভাবে অনুবীক্ষণ যন্ত্রে দেখা যায়।
- মায়োসিস বিভাজনকে হ্রাস বিভাজন (Reductional Division) বলা হয়, কারণ এতে অপত্য কোষে ক্রোমোজোম সংখ্যা অর্ধেক হয়ে যায়।

---

## ২. মানব দেহের রক্ত ও সংবহনতন্ত্র
- রক্তের সাধারণ উপাদান: রক্তরস (৫৫%) এবং রক্তকণিকা (৪৫%)।
- লোহিত রক্তকণিকা (RBC): গড় আয়ু ১২০ দিন। হিমোগ্লোবিনের উপস্থিতির জন্য লাল হয়।
- শ্বেত রক্তকণিকা (WBC): দেহের রোগ প্রতিরোধ ক্ষমতা তৈরি করে।
- অনুচক্রিকা (Platelet): রক্ত জমাট বাঁধতে সাহায্য করে (ক্লটিং টাইম ৩-৮ মিনিট)।

---

## ৩. গুরুত্বপূর্ণ ভিটামিন ও অভাবজনিত রোগ
- **ভিটামিন A** (রেটিনল): অভাবজনিত রোগ রাতকানা ও জেরোপথ্যালমিয়া।
- **ভিটামিন D** (ক্যালসিফেরল): অভাবে শিশুদের রিকেট ও প্রাপ্তবয়স্কদের অস্টিওম্যালাসিয়া।
- **ভিটামিন E** (টোকোফেরল): বন্ধ্যাত্ব প্রতিরোধক ভিটামিন।
- **ভিটামিন K** (ফাইলোকুইনন): রক্ত জমাট বাঁধার জন্য অপরিহার্য।
- **ভিটামিন B1** (থায়ামিন): অভাবে বেরিবেরি রোগ হয়।
- **ভিটামিন C** (অ্যাসকরবিক অ্যাসিড): অভাবে স্কার্ভি ও মাড়ি দিয়ে রক্ত পড়ে।
          """.trimIndent()
        ),
        StudyMaterial(
          id = "pdf_03",
          categoryId = "wb_cho",
          categoryName = "WB CHO 2026",
          title = "WB CHO NHM Community Health Officer Handbook & National Health Programmes",
          titleBn = "ডব্লিউবি সিএইচও এনএইচএম কমিউনিটি হেলথ অফিসার হ্যান্ডবুক",
          subject = "Community Health",
          topic = "Health & Wellness Centers (Ayushman Arogya Mandir)",
          pageCount = 82,
          fileSizeBytes = 7100000,
          isPremium = true,
          description = "Full coverage of Maternal & Child Health, Ayushman Bharat, NCD Screening, Communicable Disease Surveillance, and West Bengal Swasthya Sathi.",
          language = "English",
          contentMarkdown = """
# WB Community Health Officer (CHO) Protocol Manual
**NHM West Bengal & Health & Wellness Center (Ayushman Arogya Mandir) Focus**

---

## 1. Roles & Responsibilities of CHO
- Clinical head of Sub-Health Center / Ayushman Arogya Mandir.
- Delivery of 12 Comprehensive Primary Health Care (CPHC) packages.
- Screening for 5 Common Non-Communicable Diseases (NCDs): Hypertension, Diabetes, Oral Cancer, Breast Cancer, Cervical Cancer.
- Teleconsultation linkage via e-Sanjeevani portal with Medical College specialists.

---

## 2. Maternal Health Indicators & Protocols
- Minimum recommended Antenatal Care (ANC) visits: **4 Visits** (Pradhan Mantri Surakshit Matritva Abhiyan - PMSMA on 9th of every month).
- Dangerous signs in pregnancy: Severe headache with blurred vision (pre-eclampsia), bleeding per vagina, persistent fever, reduced fetal movements.
- Postnatal Care (PNC) home visits: 1st, 3rd, 7th, 14th, 21st, 28th, and 42nd day for home deliveries.

---

## 3. Cold Chain Management for Vaccines
- Ideal storage temperature for vaccines: **+2°C to +8°C**.
- Most heat-sensitive vaccine: Oral Polio Vaccine (OPV).
- Most freeze-sensitive vaccines: Hepatitis B, Td, Pentavalent (Never freeze; test using Shake Test).
          """.trimIndent()
        ),
        StudyMaterial(
          id = "pdf_04",
          categoryId = "norcet",
          categoryName = "NORCET 2026",
          title = "NORCET AIIMS High-Yield Pharmacology, Drug Calculations & Clinical Pearls",
          titleBn = "নরসেট এইমস ফার্মাকোলজি ও ড্রাগ ক্যালকুলেশন হ্যান্ডবুক",
          subject = "Pharmacology",
          topic = "Critical Care Nursing",
          pageCount = 54,
          fileSizeBytes = 4900000,
          isPremium = true,
          description = "Emergency drug antidotes, pediatric dose calculations, arterial blood gas (ABG) interpretation formulas, and ICU ventilator modes.",
          language = "English",
          contentMarkdown = """
# NORCET AIIMS Nursing Officer Pearls
**High-Yield Clinical Pharmacology & Calculations**

---

## 1. Universal Drug Antidotes Cheat-Sheet
- **Heparin**: Protamine Sulfate
- **Warfarin**: Vitamin K (Phytonadione)
- **Opioids (Morphine, Fentanyl)**: Naloxone (Narcan)
- **Acetaminophen (Paracetamol)**: N-Acetylcysteine (NAC)
- **Benzodiazepines**: Flumazenil
- **Digoxin**: Digibind (Digoxin Immune Fab)
- **Magnesium Sulfate**: 10% Calcium Gluconate
- **Beta-Blockers**: Glucagon

---

## 2. IV Flow Rate Formula
Formula: Flow Rate (drops/min) = [Total Volume (mL) * Drop Factor] / Total Time (minutes)
- Standard Microdrip set factor = 60 drops/mL.
- Standard Macrodrip set factor = 15 or 20 drops/mL.

---

## 3. Arterial Blood Gas (ABG) Master Key
- pH: 7.35 – 7.45
- PaCO2: 35 – 45 mmHg (Respiratory parameter)
- HCO3: 22 – 26 mEq/L (Metabolic parameter)
- *ROME Rule*: Respiratory Opposite, Metabolic Equal.
          """.trimIndent()
        )
      )
    }

    private fun createSampleAttempts(): List<TestAttempt> {
      return listOf(
        TestAttempt(
          id = "att_01",
          testId = "test_jenpas_01",
          testTitle = "JENPAS-UG Full-Length Mock Test 1 (All Bengal Free)",
          categoryId = "jenpas_ug",
          timestamp = System.currentTimeMillis() - 86400000L * 3,
          timeTakenSeconds = 1450,
          userAnswers = mapOf(0 to 0, 1 to 0, 2 to 1, 3 to 1, 4 to 0, 5 to 0, 6 to 1, 7 to 0, 8 to 0, 9 to 1),
          score = 8.75f,
          totalMarks = 10.0f,
          accuracyPercentage = 90.0f,
          correctCount = 9,
          incorrectCount = 1,
          unattemptedCount = 0,
          rankEstimated = 28,
          totalParticipants = 4210
        )
      )
    }
  }
}
