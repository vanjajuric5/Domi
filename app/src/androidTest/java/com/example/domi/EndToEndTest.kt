package com.example.domi

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun userFullFlowTest() {
        // 1. OTVORI LOGIN
        ActivityScenario.launch(LoginActivity::class.java)
        
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen").fetchSemanticsNodes().isNotEmpty()
        }

        // 2. IDI NA REGISTRACIJU
        composeTestRule.onNodeWithTag("register_link").performClick()

        // 3. REGISTRIRAJ KORISNIKA
        val timestamp = System.currentTimeMillis()
        val testEmail = "user$timestamp@test.com"
        val testPass = "pass123"

        composeTestRule.onNodeWithTag("name_input").performTextInput("Test Korisnik")
        composeTestRule.onNodeWithTag("email_input").performTextInput(testEmail)
        composeTestRule.onNodeWithTag("password_input").performTextInput(testPass)
        composeTestRule.onNodeWithTag("phone_input").performTextInput("091234567")
        composeTestRule.onNodeWithTag("city_input").performTextInput("Zagreb")
        
        composeTestRule.onNodeWithTag("register_button").performScrollTo().performClick()

        // 4. ČEKAJ POVRATAK NA LOGIN I PRIJAVI SE
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("email_input").performTextReplacement(testEmail)
        composeTestRule.onNodeWithTag("password_input").performTextReplacement(testPass)
        composeTestRule.onNodeWithTag("login_button").performClick()

        // 5. ČEKAJ GLAVNI EKRAN I POGLEDAJ DETALJE
        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodesWithTag("glavni_ekran").fetchSemanticsNodes().isNotEmpty()
        }

        // Pronađi bilo koju karticu (npr. Rexa) i klikni na nju
        composeTestRule.onNodeWithTag("animal_list")
            .performScrollToNode(hasTestTag("animal_card_Rex"))
        composeTestRule.onNodeWithTag("animal_card_Rex").performClick()

        // Provjeri da smo na detaljima
        composeTestRule.onNodeWithText("O ljubimcu").assertIsDisplayed()

        // 6. ODJAVA
        composeTestRule.onNodeWithTag("back_button").performClick()
        composeTestRule.onNodeWithTag("settings_tab").performClick()
        composeTestRule.onNodeWithText("Odjavi se").performClick()

        // ČEKAJ POVRATAK NA LOGIN
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun adminFlowTest() {
        // 1. OTVORI LOGIN I PRIJAVI SE KAO ADMIN
        ActivityScenario.launch(LoginActivity::class.java)
        
        composeTestRule.onNodeWithTag("email_input").performTextInput("admin@domi.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("admin123")
        composeTestRule.onNodeWithTag("login_button").performClick()

        // 2. ČEKAJ GLAVNI EKRAN
        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodesWithTag("glavni_ekran").fetchSemanticsNodes().isNotEmpty()
        }

        // 3. DODAJ NOVU ŽIVOTINJU
        composeTestRule.onNodeWithTag("add_animal_button").performClick()
        
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithTag("animal_name_input").fetchSemanticsNodes().isNotEmpty()
        }
        
        val newName = "Testko"
        composeTestRule.onNodeWithTag("animal_name_input").performTextInput(newName)
        composeTestRule.onNodeWithTag("animal_breed_input").performTextInput("Pasmina")
        composeTestRule.onNodeWithTag("animal_age_input").performTextInput("1 godina")
        
        composeTestRule.onNodeWithTag("add_animal_submit_button").performScrollTo().performClick()

        // 4. PROVJERI DA SE VRATIO NA LISTU
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_list").fetchSemanticsNodes().isNotEmpty()
        }
        
        // 5. ZAHTJEVI I ODJAVA
        composeTestRule.onNodeWithTag("requests_tab").performClick()
        composeTestRule.onNodeWithTag("requests_list").assertExists()
        
        composeTestRule.onNodeWithTag("settings_tab").performClick()
        composeTestRule.onNodeWithText("Odjavi se").performClick()
    }
}
