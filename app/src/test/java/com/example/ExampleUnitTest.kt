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

  @Test
  fun testCountryCodeProviderGlobalAccess() {
    val countries = com.example.data.model.CountryCodeProvider.countries
    assertTrue("Should have over 40 global countries", countries.size >= 40)

    // Test finding default country
    assertEquals("+91", com.example.data.model.CountryCodeProvider.defaultCountry.dialCode)

    // Test finding by dial code
    val us = com.example.data.model.CountryCodeProvider.findByDialCode("+1")
    assertNotNull(us)
    assertEquals("US", us?.isoCode)

    val jp = com.example.data.model.CountryCodeProvider.findByDialCode("+81")
    assertNotNull(jp)
    assertEquals("Japan", jp?.name)

    val gb = com.example.data.model.CountryCodeProvider.findByDialCode("+44")
    assertNotNull(gb)
    assertEquals("United Kingdom", gb?.name)

    // Test searching by query
    val searchResults = com.example.data.model.CountryCodeProvider.search("Japan")
    assertTrue(searchResults.any { it.dialCode == "+81" })

    val codeSearch = com.example.data.model.CountryCodeProvider.search("+49")
    assertTrue(codeSearch.any { it.name == "Germany" })
  }

  @Test
  fun testSavedScriptEntityRoomData() {
    val entity = com.example.data.db.SavedScriptEntity(
      id = "test_script_1",
      title = "Solo Hunter Resurrection",
      originalPrompt = "Shadow hunter with glowing blue eyes",
      inputSourceType = "Prompt",
      genre = "Shonen Fantasy",
      artStyle = "Manhwa Webtoon",
      language = "Hindi",
      synopsis = "A hunter awakens shadow monarch aura",
      scriptJson = "{}"
    )

    assertEquals("test_script_1", entity.id)
    assertEquals("Solo Hunter Resurrection", entity.title)
    assertEquals("Manhwa Webtoon", entity.artStyle)
    assertTrue(entity.createdAt > 0)
  }
}
