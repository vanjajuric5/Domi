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
        Thread.sleep(500)
        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()

        // 2. IDI NA REGISTRACIJU
        composeTestRule.onNodeWithTag("register_link").performClick()
        Thread.sleep(500)

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

        // 4. ČEKAJ POVRATAK NA LOGIN
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        // 5. PRIJAVI SE
        composeTestRule.onNodeWithTag("email_input").performTextInput(testEmail)
        composeTestRule.onNodeWithTag("password_input").performTextInput(testPass)
        composeTestRule.onNodeWithTag("login_button").performClick()

        // 6. ČEKAJ GLAVNI EKRAN
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("glavni_ekran")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        // 7. ČEKAJ DA SE LISTA UČITA I PRONAĐI REXA
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_card_Rex")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("animal_card_Rex")
            .performScrollTo()
            .performClick()
        Thread.sleep(500)

        // 8. KLIKNI UDOMI ME
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("adopt_button")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("adopt_button")
            .performScrollTo()
            .performClick()
        Thread.sleep(300)

        // 9. ISPUNI FORMU I POŠALJI
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("adoption_message_input")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("adoption_message_input")
            .performTextInput("Želim udomiti Rexa jer imam veliko dvorište.")
        composeTestRule.onNodeWithTag("send_adoption_request_button").performClick()

        // 10. ČEKAJ POVRATAK NA DETALJE — adopt_button se pojavi kad showForm = false
        Thread.sleep(2000)
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("adopt_button")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        // 11. ODJAVA
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("settings_tab")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("settings_tab").performClick()
        Thread.sleep(500)

        composeTestRule.onNodeWithText("Odjavi se").performScrollTo().performClick()

        // 12. ČEKAJ POVRATAK NA LOGIN
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
    }

    @Test
    fun adminFlowTest() {

        // 1. OTVORI LOGIN
        ActivityScenario.launch(LoginActivity::class.java)
        Thread.sleep(500)

        // 2. PRIJAVI SE KAO ADMIN
        composeTestRule.onNodeWithTag("email_input").performTextInput("admin@domi.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("admin123")
        composeTestRule.onNodeWithTag("login_button").performClick()

        // 3. ČEKAJ GLAVNI EKRAN
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("glavni_ekran")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        // 4. ČEKAJ ADD ANIMAL BUTTON I KLIKNI
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("add_animal_button")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("add_animal_button").performClick()
        Thread.sleep(500)

        // 5. ISPUNI FORMU ZA DODAVANJE
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

        // 6. ČEKAJ POVRATAK NA LISTU
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_list")
                .fetchSemanticsNodes(false).isNotEmpty()
        }

        // 7. PROVJERI DA ŽIVOTINJA POSTOJI U LISTI
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("animal_card_$newAnimalName")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("animal_card_$newAnimalName")
            .performScrollTo()
            .assertExists()

        // 8. OBRIŠI ŽIVOTINJU
        composeTestRule.onNode(
            hasContentDescription("Obriši") and
                    hasAnyAncestor(hasTestTag("animal_card_$newAnimalName"))
        ).performClick()

        // 9. POTVRDI BRISANJE U DIJALOGU
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Obriši")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithText("Obriši").performClick()
        Thread.sleep(1000)

        composeTestRule.onNodeWithTag("animal_card_$newAnimalName").assertDoesNotExist()

        // 10. IDI NA ZAHTJEVE
        composeTestRule.onNodeWithTag("requests_tab").performClick()
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("requests_list")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("requests_list").assertIsDisplayed()

        // 11. ODJAVA
        composeTestRule.onNodeWithTag("settings_tab").performClick()
        Thread.sleep(500)
        composeTestRule.onNodeWithText("Odjavi se").performScrollTo().performClick()

        // 12. ČEKAJ POVRATAK NA LOGIN
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes(false).isNotEmpty()
        }
        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
    }
}