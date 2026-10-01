package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.ExamCategoryEntity
import com.example.data.local.entities.StudyResourceEntity
import com.example.data.local.entities.TestAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyResourceDao {
  @Query("SELECT * FROM study_resources WHERE examId = :examId ORDER BY title ASC")
  fun getResourcesByExam(examId: String): Flow<List<StudyResourceEntity>>

  @Query("SELECT * FROM study_resources ORDER BY viewCount DESC")
  fun getAllResources(): Flow<List<StudyResourceEntity>>

  @Query("SELECT * FROM study_resources WHERE isBookmarked = 1")
  fun getBookmarkedResources(): Flow<List<StudyResourceEntity>>

  @Query("SELECT * FROM study_resources WHERE id = :id LIMIT 1")
  suspend fun getResourceById(id: String): StudyResourceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResources(resources: List<StudyResourceEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResource(resource: StudyResourceEntity)

  @Query("UPDATE study_resources SET isBookmarked = :bookmarked WHERE id = :id")
  suspend fun updateBookmark(id: String, bookmarked: Boolean)

  @Query("UPDATE study_resources SET viewCount = viewCount + 1 WHERE id = :id")
  suspend fun incrementViewCount(id: String)

  @Query("DELETE FROM study_resources WHERE id = :id")
  suspend fun deleteResourceById(id: String)
}

@Dao
interface TestAttemptDao {
  @Query("SELECT * FROM test_attempts ORDER BY timestamp DESC")
  fun getAllAttempts(): Flow<List<TestAttemptEntity>>

  @Query("SELECT * FROM test_attempts WHERE categoryId = :categoryId ORDER BY timestamp DESC")
  fun getAttemptsByCategory(categoryId: String): Flow<List<TestAttemptEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAttempt(attempt: TestAttemptEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllAttempts(attempts: List<TestAttemptEntity>)

  @Query("DELETE FROM test_attempts WHERE id = :id")
  suspend fun deleteAttemptById(id: String)
}

@Dao
interface ExamCategoryDao {
  @Query("SELECT * FROM exam_categories ORDER BY sortOrder ASC")
  fun getAllCategories(): Flow<List<ExamCategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<ExamCategoryEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: ExamCategoryEntity)

  @Query("DELETE FROM exam_categories WHERE id = :id")
  suspend fun deleteCategoryById(id: String)
}
