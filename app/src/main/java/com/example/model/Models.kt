package com.example.model

enum class AppLanguage {
  ENGLISH,
  BENGALI
}

enum class UserRole {
  STUDENT,
  ADMIN
}

data class UserProfile(
  val id: String = "user_wb_001",
  val name: String = "Priyanka Mukherjee",
  val email: String = "priyanka.nursing@wbtopper.com",
  val phone: String = "+91 98765 43210",
  val role: UserRole = UserRole.STUDENT,
  val selectedCategoryId: String = "jenpas_ug",
  val isPremium: Boolean = false,
  val premiumExpiryDate: String? = null,
  val avatarUrl: String = "",
  val targetExamName: String = "JENPAS-UG 2026"
)

data class ExamCategory(
  val id: String,
  val title: String,
  val subtitle: String,
  val bengaliTitle: String,
  val bengaliSubtitle: String,
  val iconKey: String,
  val bgHex: Long,
  val iconTintHex: Long,
  val badgeText: String? = null,
  val description: String = "",
  val syllabusSummary: List<String> = emptyList(),
  val notificationCount: Int = 0,
  val isActive: Boolean = true,
  val sortOrder: Int = 0
)

data class Question(
  val id: String,
  val questionNumber: Int,
  val questionText: String,
  val questionTextBn: String,
  val options: List<String>,
  val optionsBn: List<String>? = null,
  val correctOptionIndex: Int,
  val explanation: String,
  val explanationBn: String,
  val subject: String,
  val topic: String,
  val isMultiCorrect: Boolean = false,
  val clinicalScenario: String? = null,
  val isAiGenerated: Boolean = false
)

data class MockTest(
  val id: String,
  val categoryId: String,
  val categoryName: String,
  val title: String,
  val titleBn: String,
  val durationMinutes: Int,
  val totalQuestions: Int,
  val totalMarks: Float,
  val positiveMark: Float = 1.0f,
  val negativeMark: Float = 0.25f,
  val isPremium: Boolean = false,
  val difficulty: String = "Medium", // Easy, Medium, Hard
  val attemptsCount: Int = 1420,
  val questions: List<Question> = emptyList()
)

data class TestAttempt(
  val id: String,
  val testId: String,
  val testTitle: String,
  val categoryId: String,
  val timestamp: Long = System.currentTimeMillis(),
  val timeTakenSeconds: Int,
  val userAnswers: Map<Int, Int>, // questionIndex to selectedOptionIndex
  val markedIndices: Set<Int> = emptySet(),
  val score: Float,
  val totalMarks: Float,
  val accuracyPercentage: Float,
  val correctCount: Int,
  val incorrectCount: Int,
  val unattemptedCount: Int,
  val rankEstimated: Int = 42,
  val totalParticipants: Int = 3850
)

enum class StudyResourceType {
  PDF,
  ARTICLE,
  NOTES,
  PYQ,
  SYLLABUS
}

data class StudyResource(
  val id: String = "",
  val examId: String = "",
  val examName: String = "",
  val title: String = "",
  val titleBn: String = "",
  val subject: String = "",
  val topic: String = "",
  val resourceType: StudyResourceType = StudyResourceType.PDF,
  val contentMarkdown: String = "",
  val pdfUrl: String = "",
  val pageCount: Int = 1,
  val fileSizeBytes: Long = 0L,
  val isPremium: Boolean = false,
  val language: String = "English",
  val author: String = "WBTOPPER Faculty",
  val description: String = "",
  val viewCount: Int = 0,
  val downloadCount: Int = 0,
  val tags: List<String> = emptyList(),
  val publishedDate: String = "Recently Added"
) {
  fun toStudyMaterial(): StudyMaterial {
    return StudyMaterial(
      id = id,
      categoryId = examId,
      categoryName = examName,
      title = title,
      titleBn = titleBn,
      subject = subject,
      topic = topic,
      pageCount = pageCount,
      fileSizeBytes = fileSizeBytes,
      isPremium = isPremium,
      contentMarkdown = contentMarkdown,
      description = description,
      language = language,
      viewCount = viewCount,
      downloadCount = downloadCount,
      resourceType = resourceType,
      pdfUrl = pdfUrl,
      author = author
    )
  }
}

data class StudyMaterial(
  val id: String = "",
  val categoryId: String = "",
  val categoryName: String = "",
  val title: String = "",
  val titleBn: String = "",
  val subject: String = "",
  val topic: String = "",
  val pageCount: Int = 1,
  val fileSizeBytes: Long = 0L,
  val isPremium: Boolean = false,
  val contentMarkdown: String = "",
  val description: String = "",
  val language: String = "English",
  val viewCount: Int = 230,
  val downloadCount: Int = 95,
  val resourceType: StudyResourceType = StudyResourceType.PDF,
  val pdfUrl: String = "",
  val author: String = "WBTOPPER Faculty"
) {
  fun toStudyResource(): StudyResource {
    return StudyResource(
      id = id,
      examId = categoryId,
      examName = categoryName,
      title = title,
      titleBn = titleBn,
      subject = subject,
      topic = topic,
      resourceType = resourceType,
      contentMarkdown = contentMarkdown,
      pdfUrl = pdfUrl,
      pageCount = pageCount,
      fileSizeBytes = fileSizeBytes,
      isPremium = isPremium,
      language = language,
      author = author,
      description = description,
      viewCount = viewCount,
      downloadCount = downloadCount
    )
  }
}

data class ExamAlert(
  val id: String,
  val title: String,
  val titleBn: String,
  val date: String,
  val isBreaking: Boolean = false,
  val isOfficialVerified: Boolean = true,
  val categoryId: String? = null,
  val description: String = "",
  val linkText: String = "View Official Notice"
)

data class PaymentOrder(
  val orderId: String,
  val amountInRupees: Int = 199,
  val status: String = "SUCCESS", // SUCCESS, PENDING, FAILED
  val paymentMethod: String = "UPI (Google Pay)",
  val timestamp: Long = System.currentTimeMillis(),
  val planName: String = "WBTOPPER All-Access Premium Pass (1 Year)"
)

data class AdminMetric(
  val totalStudents: Int = 18450,
  val activePremium: Int = 4320,
  val totalMockTests: Int = 128,
  val totalAttempts: Int = 89400,
  val totalStudyMaterials: Int = 245,
  val totalRevenueInr: Long = 859680
)
