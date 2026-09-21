package com.example

import com.example.ui.screens.AiVoiceProfile
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Anime Studio AI models and voice profiles.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAiVoiceProfileCategories() {
    val profiles = listOf(
      AiVoiceProfile("male_1", "Deep Shonen Hero", "Male", "Hero", 0.88f, 1.10f, "Test", "Desc"),
      AiVoiceProfile("female_1", "Sweet Kawaii Heroine", "Female", "Heroine", 1.38f, 1.10f, "Test", "Desc"),
      AiVoiceProfile("child_1", "Kawaii Chibi Mascot Spirit", "Child", "Mascot", 1.82f, 1.22f, "Test", "Desc")
    )
    val males = profiles.filter { it.gender.equals("Male", ignoreCase = true) }
    val females = profiles.filter { it.gender.equals("Female", ignoreCase = true) }
    val children = profiles.filter { it.gender.equals("Child", ignoreCase = true) }

    assertEquals(1, males.size)
    assertEquals(1, females.size)
    assertEquals(1, children.size)
    assertTrue(males.first().defaultPitch < 1.0f)
    assertTrue(females.first().defaultPitch > 1.0f)
    assertTrue(children.first().defaultPitch > 1.5f)
  }
}
