package com.example

import com.example.data.WbTopperRepository
import com.example.model.ExamCategory
import com.example.model.MockTest
import com.example.model.Question
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testCategoriesSeedCount() {
    val repo = WbTopperRepository()
    val categories = repo.categories.value
    assertTrue("Should seed at least 15 exam categories", categories.size >= 15)
    assertNotNull("Should have JENPAS-UG category", categories.find { it.id == "jenpas_ug" })
    assertNotNull("Should have ANM-GNM category", categories.find { it.id == "anm_gnm" })
    assertNotNull("Should have WB CHO category", categories.find { it.id == "wb_cho" })
    assertNotNull("Should have NORCET category", categories.find { it.id == "norcet" })
  }

  @Test
  fun testScoringFormula() {
    val positiveMark = 1.0f
    val negativeMark = 0.25f

    val totalCorrect = 8
    val totalIncorrect = 2

    val score = (totalCorrect * positiveMark) - (totalIncorrect * negativeMark)
    assertEquals(7.5f, score, 0.001f)

    val accuracy = (totalCorrect.toFloat() / (totalCorrect + totalIncorrect)) * 100f
    assertEquals(80.0f, accuracy, 0.001f)
  }
}
