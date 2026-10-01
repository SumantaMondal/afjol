package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ExamCategory
import com.example.model.StudyResource
import com.example.model.StudyResourceType
import com.example.model.TestAttempt

@Entity(tableName = "study_resources")
data class StudyResourceEntity(
  @PrimaryKey val id: String,
  val examId: String,
  val examName: String,
  val title: String,
  val titleBn: String,
  val subject: String,
  val topic: String,
  val resourceType: String,
  val contentMarkdown: String,
  val pdfUrl: String,
  val pageCount: Int,
  val fileSizeBytes: Long,
  val isPremium: Boolean,
  val language: String,
  val author: String,
  val description: String,
  val viewCount: Int,
  val downloadCount: Int,
  val tagsJson: String,
  val publishedDate: String,
  val isBookmarked: Boolean = false
) {
  fun toStudyResource(): StudyResource {
    val type = try {
      StudyResourceType.valueOf(resourceType)
    } catch (e: Exception) {
      StudyResourceType.PDF
    }
    val tagsList = if (tagsJson.isBlank()) emptyList() else tagsJson.split(",").map { it.trim() }

    return StudyResource(
      id = id,
      examId = examId,
      examName = examName,
      title = title,
      titleBn = titleBn,
      subject = subject,
      topic = topic,
      resourceType = type,
      contentMarkdown = contentMarkdown,
      pdfUrl = pdfUrl,
      pageCount = pageCount,
      fileSizeBytes = fileSizeBytes,
      isPremium = isPremium,
      language = language,
      author = author,
      description = description,
      viewCount = viewCount,
      downloadCount = downloadCount,
      tags = tagsList,
      publishedDate = publishedDate
    )
  }

  companion object {
    fun fromStudyResource(res: StudyResource, isBookmarked: Boolean = false): StudyResourceEntity {
      return StudyResourceEntity(
        id = res.id,
        examId = res.examId,
        examName = res.examName,
        title = res.title,
        titleBn = res.titleBn,
        subject = res.subject,
        topic = res.topic,
        resourceType = res.resourceType.name,
        contentMarkdown = res.contentMarkdown,
        pdfUrl = res.pdfUrl,
        pageCount = res.pageCount,
        fileSizeBytes = res.fileSizeBytes,
        isPremium = res.isPremium,
        language = res.language,
        author = res.author,
        description = res.description,
        viewCount = res.viewCount,
        downloadCount = res.downloadCount,
        tagsJson = res.tags.joinToString(","),
        publishedDate = res.publishedDate,
        isBookmarked = isBookmarked
      )
    }
  }
}

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
  @PrimaryKey val id: String,
  val testId: String,
  val testTitle: String,
  val categoryId: String,
  val timestamp: Long,
  val timeTakenSeconds: Int,
  val score: Float,
  val totalMarks: Float,
  val accuracyPercentage: Float,
  val correctCount: Int,
  val incorrectCount: Int,
  val unattemptedCount: Int,
  val rankEstimated: Int,
  val totalParticipants: Int,
  val answersJson: String
) {
  fun toTestAttempt(): TestAttempt {
    val answersMap = if (answersJson.isBlank()) emptyMap() else {
      answersJson.split(";").mapNotNull { pair ->
        val parts = pair.split(":")
        if (parts.size == 2) {
          val q = parts[0].toIntOrNull()
          val a = parts[1].toIntOrNull()
          if (q != null && a != null) q to a else null
        } else null
      }.toMap()
    }

    return TestAttempt(
      id = id,
      testId = testId,
      testTitle = testTitle,
      categoryId = categoryId,
      timestamp = timestamp,
      timeTakenSeconds = timeTakenSeconds,
      score = score,
      totalMarks = totalMarks,
      accuracyPercentage = accuracyPercentage,
      correctCount = correctCount,
      incorrectCount = incorrectCount,
      unattemptedCount = unattemptedCount,
      rankEstimated = rankEstimated,
      totalParticipants = totalParticipants,
      userAnswers = answersMap
    )
  }

  companion object {
    fun fromTestAttempt(att: TestAttempt): TestAttemptEntity {
      val answersStr = att.userAnswers.map { "${it.key}:${it.value}" }.joinToString(";")
      return TestAttemptEntity(
        id = att.id,
        testId = att.testId,
        testTitle = att.testTitle,
        categoryId = att.categoryId,
        timestamp = att.timestamp,
        timeTakenSeconds = att.timeTakenSeconds,
        score = att.score,
        totalMarks = att.totalMarks,
        accuracyPercentage = att.accuracyPercentage,
        correctCount = att.correctCount,
        incorrectCount = att.incorrectCount,
        unattemptedCount = att.unattemptedCount,
        rankEstimated = att.rankEstimated,
        totalParticipants = att.totalParticipants,
        answersJson = answersStr
      )
    }
  }
}

@Entity(tableName = "exam_categories")
data class ExamCategoryEntity(
  @PrimaryKey val id: String,
  val title: String,
  val subtitle: String,
  val bengaliTitle: String,
  val bengaliSubtitle: String,
  val iconKey: String,
  val bgHex: Long,
  val iconTintHex: Long,
  val badgeText: String?,
  val description: String,
  val sortOrder: Int
) {
  fun toExamCategory(): ExamCategory {
    return ExamCategory(
      id = id,
      title = title,
      subtitle = subtitle,
      bengaliTitle = bengaliTitle,
      bengaliSubtitle = bengaliSubtitle,
      iconKey = iconKey,
      bgHex = bgHex,
      iconTintHex = iconTintHex,
      badgeText = badgeText,
      description = description,
      sortOrder = sortOrder
    )
  }

  companion object {
    fun fromExamCategory(cat: ExamCategory): ExamCategoryEntity {
      return ExamCategoryEntity(
        id = cat.id,
        title = cat.title,
        subtitle = cat.subtitle,
        bengaliTitle = cat.bengaliTitle,
        bengaliSubtitle = cat.bengaliSubtitle,
        iconKey = cat.iconKey,
        bgHex = cat.bgHex,
        iconTintHex = cat.iconTintHex,
        badgeText = cat.badgeText,
        description = cat.description,
        sortOrder = cat.sortOrder
      )
    }
  }
}
