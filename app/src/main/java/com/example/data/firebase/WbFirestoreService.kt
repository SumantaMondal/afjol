package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.ExamAlert
import com.example.model.ExamCategory
import com.example.model.StudyResource
import com.example.model.StudyResourceType
import com.example.model.TestAttempt
import com.example.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object WbFirestoreService {
  private const val TAG = "WbFirestoreService"
  private var firestoreInstance: FirebaseFirestore? = null
  private var isInitialized = false

  fun initialize(context: Context): FirebaseFirestore? {
    if (isInitialized && firestoreInstance != null) {
      return firestoreInstance
    }

    return try {
      if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
      }

      val databaseId = try {
        context.getString(R.string.firestore_database_id)
      } catch (e: Exception) {
        "(default)"
      }

      val db = if (databaseId.isNotEmpty() && databaseId != "(default)") {
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
      } else {
        FirebaseFirestore.getInstance()
      }

      // Configure modern offline caching & persistent data storage
      val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()
      db.firestoreSettings = settings

      firestoreInstance = db
      isInitialized = true
      Log.i(TAG, "Firebase Firestore SDK initialized successfully with database: $databaseId")
      db
    } catch (e: Exception) {
      Log.w(TAG, "Firestore initialization deferred or backend not yet provisioned: ${e.message}")
      null
    }
  }

  fun getFirestore(): FirebaseFirestore? = firestoreInstance

  // Save student user profile
  fun saveUserProfile(user: UserProfile, onComplete: ((Boolean) -> Unit)? = null) {
    val db = firestoreInstance ?: run {
      onComplete?.invoke(false)
      return
    }

    val userMap = hashMapOf(
      "id" to user.id,
      "name" to user.name,
      "email" to user.email,
      "phone" to user.phone,
      "role" to user.role.name,
      "selectedCategoryId" to user.selectedCategoryId,
      "isPremium" to user.isPremium,
      "targetExamName" to user.targetExamName,
      "updatedAt" to FieldValue.serverTimestamp()
    )

    db.collection("users").document(user.id)
      .set(userMap, SetOptions.merge())
      .addOnSuccessListener {
        Log.d(TAG, "User profile saved in Firestore: ${user.id}")
        onComplete?.invoke(true)
      }
      .addOnFailureListener { e ->
        Log.w(TAG, "Failed to save user profile in Firestore: ${e.message}")
        onComplete?.invoke(false)
      }
  }

  // Save test attempt
  fun saveTestAttempt(userId: String, attempt: TestAttempt, onComplete: ((Boolean) -> Unit)? = null) {
    val db = firestoreInstance ?: run {
      onComplete?.invoke(false)
      return
    }

    val attemptMap = hashMapOf(
      "id" to attempt.id,
      "testId" to attempt.testId,
      "testTitle" to attempt.testTitle,
      "categoryId" to attempt.categoryId,
      "timestamp" to attempt.timestamp,
      "timeTakenSeconds" to attempt.timeTakenSeconds,
      "score" to attempt.score,
      "totalMarks" to attempt.totalMarks,
      "accuracyPercentage" to attempt.accuracyPercentage,
      "correctCount" to attempt.correctCount,
      "incorrectCount" to attempt.incorrectCount,
      "unattemptedCount" to attempt.unattemptedCount,
      "rankEstimated" to attempt.rankEstimated,
      "totalParticipants" to attempt.totalParticipants,
      "userAnswers" to attempt.userAnswers.mapKeys { it.key.toString() },
      "createdAt" to FieldValue.serverTimestamp()
    )

    db.collection("users").document(userId)
      .collection("attempts").document(attempt.id)
      .set(attemptMap, SetOptions.merge())
      .addOnSuccessListener {
        Log.d(TAG, "Test attempt saved to Firestore: ${attempt.id}")
        onComplete?.invoke(true)
      }
      .addOnFailureListener { e ->
        Log.w(TAG, "Failed to save test attempt to Firestore: ${e.message}")
        onComplete?.invoke(false)
      }
  }

  // Real-time observation of user test attempts
  fun observeTestAttempts(userId: String): Flow<List<TestAttempt>> = callbackFlow {
    val db = firestoreInstance
    if (db == null) {
      close()
      return@callbackFlow
    }

    val listener = db.collection("users").document(userId)
      .collection("attempts")
      .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w(TAG, "Listen failed for attempts: ${error.message}")
          return@addSnapshotListener
        }

        if (snapshot != null) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              val rawAnswers = doc.get("userAnswers") as? Map<String, Any?> ?: emptyMap()
              val answersMap = rawAnswers.mapNotNull { (k, v) ->
                val qIdx = k.toIntOrNull()
                val ansIdx = (v as? Number)?.toInt()
                if (qIdx != null && ansIdx != null) qIdx to ansIdx else null
              }.toMap()

              TestAttempt(
                id = doc.getString("id") ?: doc.id,
                testId = doc.getString("testId") ?: "",
                testTitle = doc.getString("testTitle") ?: "Mock Test",
                categoryId = doc.getString("categoryId") ?: "",
                timestamp = (doc.get("timestamp") as? Number)?.toLong() ?: System.currentTimeMillis(),
                timeTakenSeconds = (doc.get("timeTakenSeconds") as? Number)?.toInt() ?: 0,
                userAnswers = answersMap,
                score = (doc.get("score") as? Number)?.toFloat() ?: 0f,
                totalMarks = (doc.get("totalMarks") as? Number)?.toFloat() ?: 100f,
                accuracyPercentage = (doc.get("accuracyPercentage") as? Number)?.toFloat() ?: 0f,
                correctCount = (doc.get("correctCount") as? Number)?.toInt() ?: 0,
                incorrectCount = (doc.get("incorrectCount") as? Number)?.toInt() ?: 0,
                unattemptedCount = (doc.get("unattemptedCount") as? Number)?.toInt() ?: 0,
                rankEstimated = (doc.get("rankEstimated") as? Number)?.toInt() ?: 42,
                totalParticipants = (doc.get("totalParticipants") as? Number)?.toInt() ?: 3850
              )
            } catch (ex: Exception) {
              Log.w(TAG, "Error deserializing attempt doc ${doc.id}: ${ex.message}")
              null
            }
          }
          trySend(list)
        }
      }

    awaitClose { listener.remove() }
  }

  // Admin: Save or update exam category in Firestore
  fun saveExamCategory(category: ExamCategory, onComplete: ((Boolean) -> Unit)? = null) {
    val db = firestoreInstance ?: run {
      onComplete?.invoke(false)
      return
    }

    val catMap = hashMapOf(
      "id" to category.id,
      "title" to category.title,
      "subtitle" to category.subtitle,
      "bengaliTitle" to category.bengaliTitle,
      "bengaliSubtitle" to category.bengaliSubtitle,
      "iconKey" to category.iconKey,
      "bgHex" to category.bgHex,
      "iconTintHex" to category.iconTintHex,
      "badgeText" to category.badgeText,
      "description" to category.description,
      "isActive" to category.isActive,
      "sortOrder" to category.sortOrder,
      "updatedAt" to FieldValue.serverTimestamp()
    )

    db.collection("exam_categories").document(category.id)
      .set(catMap, SetOptions.merge())
      .addOnSuccessListener { onComplete?.invoke(true) }
      .addOnFailureListener { onComplete?.invoke(false) }
  }

  // Admin: Save or update announcement alert in Firestore
  fun saveExamAlert(alert: ExamAlert, onComplete: ((Boolean) -> Unit)? = null) {
    val db = firestoreInstance ?: run {
      onComplete?.invoke(false)
      return
    }

    val alertMap = hashMapOf(
      "id" to alert.id,
      "title" to alert.title,
      "titleBn" to alert.titleBn,
      "date" to alert.date,
      "isBreaking" to alert.isBreaking,
      "isOfficialVerified" to alert.isOfficialVerified,
      "categoryId" to alert.categoryId,
      "description" to alert.description,
      "updatedAt" to FieldValue.serverTimestamp()
    )

    db.collection("announcements").document(alert.id)
      .set(alertMap, SetOptions.merge())
      .addOnSuccessListener { onComplete?.invoke(true) }
      .addOnFailureListener { onComplete?.invoke(false) }
  }

  // Save or update Study PDF or Article resource in Firestore
  fun saveStudyResource(resource: StudyResource, onComplete: ((Boolean) -> Unit)? = null) {
    val db = firestoreInstance ?: run {
      onComplete?.invoke(false)
      return
    }

    val resourceMap = hashMapOf(
      "id" to resource.id,
      "examId" to resource.examId,
      "examName" to resource.examName,
      "title" to resource.title,
      "titleBn" to resource.titleBn,
      "subject" to resource.subject,
      "topic" to resource.topic,
      "resourceType" to resource.resourceType.name,
      "contentMarkdown" to resource.contentMarkdown,
      "pdfUrl" to resource.pdfUrl,
      "pageCount" to resource.pageCount,
      "fileSizeBytes" to resource.fileSizeBytes,
      "isPremium" to resource.isPremium,
      "language" to resource.language,
      "author" to resource.author,
      "description" to resource.description,
      "viewCount" to resource.viewCount,
      "downloadCount" to resource.downloadCount,
      "tags" to resource.tags,
      "publishedDate" to resource.publishedDate,
      "updatedAt" to FieldValue.serverTimestamp()
    )

    db.collection("study_resources").document(resource.id)
      .set(resourceMap, SetOptions.merge())
      .addOnSuccessListener {
        Log.d(TAG, "Study resource saved to Firestore: ${resource.id}")
        onComplete?.invoke(true)
      }
      .addOnFailureListener { e ->
        Log.w(TAG, "Failed to save study resource: ${e.message}")
        onComplete?.invoke(false)
      }
  }

  // Real-time observation of study resources by exam category
  fun observeStudyResources(examId: String? = null): Flow<List<StudyResource>> = callbackFlow {
    val db = firestoreInstance
    if (db == null) {
      close()
      return@callbackFlow
    }

    var query: com.google.firebase.firestore.Query = db.collection("study_resources")
    if (!examId.isNullOrEmpty()) {
      query = query.whereEqualTo("examId", examId)
    }

    val listener = query.addSnapshotListener { snapshot, error ->
      if (error != null) {
        Log.w(TAG, "Listen failed for study_resources: ${error.message}")
        return@addSnapshotListener
      }

      if (snapshot != null) {
        val resources = snapshot.documents.mapNotNull { doc ->
          try {
            val typeStr = doc.getString("resourceType") ?: "PDF"
            val type = try {
              StudyResourceType.valueOf(typeStr)
            } catch (e: Exception) {
              StudyResourceType.PDF
            }

            @Suppress("UNCHECKED_CAST")
            val tagsList = doc.get("tags") as? List<String> ?: emptyList()

            StudyResource(
              id = doc.getString("id") ?: doc.id,
              examId = doc.getString("examId") ?: "",
              examName = doc.getString("examName") ?: "",
              title = doc.getString("title") ?: "",
              titleBn = doc.getString("titleBn") ?: "",
              subject = doc.getString("subject") ?: "",
              topic = doc.getString("topic") ?: "",
              resourceType = type,
              contentMarkdown = doc.getString("contentMarkdown") ?: "",
              pdfUrl = doc.getString("pdfUrl") ?: "",
              pageCount = (doc.get("pageCount") as? Number)?.toInt() ?: 1,
              fileSizeBytes = (doc.get("fileSizeBytes") as? Number)?.toLong() ?: 0L,
              isPremium = doc.getBoolean("isPremium") ?: false,
              language = doc.getString("language") ?: "English",
              author = doc.getString("author") ?: "WBTOPPER Faculty",
              description = doc.getString("description") ?: "",
              viewCount = (doc.get("viewCount") as? Number)?.toInt() ?: 0,
              downloadCount = (doc.get("downloadCount") as? Number)?.toInt() ?: 0,
              tags = tagsList,
              publishedDate = doc.getString("publishedDate") ?: "Recently Added"
            )
          } catch (e: Exception) {
            Log.w(TAG, "Error deserializing study resource ${doc.id}: ${e.message}")
            null
          }
        }
        trySend(resources)
      }
    }

    awaitClose { listener.remove() }
  }

  // Increment view count when user opens a study PDF or article
  fun incrementResourceViewCount(resourceId: String) {
    val db = firestoreInstance ?: return
    db.collection("study_resources").document(resourceId)
      .update("viewCount", FieldValue.increment(1))
      .addOnFailureListener { e ->
        Log.w(TAG, "Failed to increment view count for $resourceId: ${e.message}")
      }
  }
}
