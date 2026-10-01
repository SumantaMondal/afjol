package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ExamCategoryDao
import com.example.data.local.dao.StudyResourceDao
import com.example.data.local.dao.TestAttemptDao
import com.example.data.local.entities.ExamCategoryEntity
import com.example.data.local.entities.StudyResourceEntity
import com.example.data.local.entities.TestAttemptEntity

@Database(
  entities = [
    StudyResourceEntity::class,
    TestAttemptEntity::class,
    ExamCategoryEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class WbTopperDatabase : RoomDatabase() {
  abstract fun studyResourceDao(): StudyResourceDao
  abstract fun testAttemptDao(): TestAttemptDao
  abstract fun examCategoryDao(): ExamCategoryDao

  companion object {
    @Volatile
    private var INSTANCE: WbTopperDatabase? = null

    fun getInstance(context: Context): WbTopperDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WbTopperDatabase::class.java,
          "wbtopper_sql.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    fun get(): WbTopperDatabase? = INSTANCE
  }
}
