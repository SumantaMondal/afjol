package com.example

import android.app.Application
import android.util.Log
import com.example.data.firebase.WbFirestoreService
import com.example.data.local.WbTopperDatabase
import com.google.firebase.FirebaseApp

class WbTopperApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    // 1. Connect and initialize Room SQLite Database
    try {
      WbTopperDatabase.getInstance(this)
      Log.i("WbTopperApplication", "SQLite (Room) Database connected successfully (wbtopper_sql.db).")
    } catch (e: Exception) {
      Log.e("WbTopperApplication", "Failed to connect to local SQL database: ${e.message}", e)
    }

    // 2. Initialize Cloud Firebase / Firestore SDK
    try {
      if (FirebaseApp.getApps(this).isEmpty()) {
        FirebaseApp.initializeApp(this)
      }
      WbFirestoreService.initialize(this)
      Log.i("WbTopperApplication", "Firebase Firestore initialized on application start.")
    } catch (e: Exception) {
      Log.w("WbTopperApplication", "Firebase deferred initialization: ${e.message}")
    }
  }
}
