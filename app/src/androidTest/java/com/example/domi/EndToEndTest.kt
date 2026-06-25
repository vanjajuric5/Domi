package com.example.domi

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Before
    fun setUp() {
        // Briši bazu prije svakog testa da nema konflikta
        ApplicationProvider.getApplicationContext<android.app.Application>()
            .deleteDatabase("domi_app.db")
    }

    @Test
    fun userFullFlowTest() {
        ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(1000)
        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("register_link").performClick()
        Thread.sleep(500)

        val timestamp = System.currentTimeMillis()
        val testEmail = "user$timestamp@test.com"
        val testPass = "pass123"

        composeTestRule.onNodeWithTag("name_input").performTextInput("Test Korisnik")
        composeTestRule.onNodeWithTag("email_input").performTextInput(testEmail)
        composeTestRule.onNodeWithTag("password_input").performTextInput(testPass)
        composeTestRule.onNodeWithTag("phone_input").performTextInput("091234567")
        composeTestRule.onNodeWithTag("city_input").performTextInput("Zagreb")
        composeTestRule.onNodeWithTag("register_button").performScrollTo().performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        composeTestRule.onNodeWithTag("email_input").performTextInput(testEmail)
        composeTestRule.onNodeWithTag("password_input").performTextInput(testPass)
        composeTestRule.onNodeWithTag("login_button").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("glavni_ekran")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_card_Rex")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("animal_card_Rex")
            .performScrollTo()
            .performClick()
        Thread.sleep(500)

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("adopt_button")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("adopt_button")
            .performScrollTo()
            .performClick()
        Thread.sleep(300)

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("adoption_message_input")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("adoption_message_input")
            .performTextInput("Želim udomiti Rexa jer imam veliko dvorište.")
        composeTestRule.onNodeWithTag("send_adoption_request_button").performClick()
        Thread.sleep(3000)

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("settings_tab")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("settings_tab").performClick()
        Thread.sleep(500)
        composeTestRule.onNodeWithText("Odjavi se").performScrollTo().performClick()

        Thread.sleep(3000)
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
    }

    @Test
    fun adminFlowTest() {
        ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("email_input").performTextInput("admin@domi.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("admin123")
        composeTestRule.onNodeWithTag("login_button").performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("glavni_ekran")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("add_animal_button")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("add_animal_button").performClick()
        Thread.sleep(500)

        val newAnimalName = "Test Pas ${System.currentTimeMillis()}"
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_name_input")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("animal_name_input").performTextInput(newAnimalName)
        composeTestRule.onNodeWithTag("animal_breed_input").performTextInput("Zlatni retriver")
        composeTestRule.onNodeWithTag("animal_age_input").performTextInput("2 godine")
        composeTestRule.onNodeWithTag("animal_description_input")
            .performScrollTo()
            .performTextInput("Ovo je testni opis.")
        composeTestRule.onNodeWithTag("add_animal_submit_button")
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_list")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_card_$newAnimalName")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("animal_card_$newAnimalName")
            .performScrollTo()
            .assertExists()

        composeTestRule.onNode(
            hasContentDescription("Obriši") and
                    hasAnyAncestor(hasTestTag("animal_card_$newAnimalName"))
        ).performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Obriši")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithText("Obriši").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithTag("animal_card_$newAnimalName").assertDoesNotExist()

        composeTestRule.onNodeWithTag("requests_tab").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("requests_list")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("requests_list").assertIsDisplayed()

        composeTestRule.onNodeWithTag("settings_tab").performClick()
        Thread.sleep(500)
        composeTestRule.onNodeWithText("Odjavi se").performScrollTo().performClick()

        Thread.sleep(3000)
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
    }
}