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
    fun fullAppFlow_comprehensiveTest() {
        // 1. PRIJAVA
        ActivityScenario.launch(LoginActivity::class.java)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("email_input").performTextInput("korisnik@email.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("lozinka123")
        composeTestRule.onNodeWithTag("login_button").performClick()

        // 2. Čekanje na glavni ekran
        composeTestRule.waitUntil(timeoutMillis = 20_000) {
            composeTestRule
                .onAllNodesWithTag("glavni_ekran")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("glavni_ekran").assertIsDisplayed()

        // 3. INTERAKCIJA S LISTOM
        composeTestRule.onNodeWithTag("glavni_ekran")
            .performScrollToNode(hasText("Rex"))

        composeTestRule.onNodeWithText("Rex").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Rex").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mješanac, 5 godina").assertIsDisplayed()
        composeTestRule.onNodeWithText("UDOMI ME").assertIsDisplayed()

        // 4. POVRATAK NA LISTU
        // contentDescription je na child Iconu — koristimo testTag umjesto contentDescription
        composeTestRule.onNodeWithTag("back_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag("glavni_ekran")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 5. POSTAVKE
        // Isto — koristimo testTag koji je na samom IconButtonu
        composeTestRule.onNodeWithTag("settings_button").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Ime Prezime").assertIsDisplayed()

        // 6. PREKIDAČI
        composeTestRule.onNodeWithTag("night_mode_toggle").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("notifications_toggle").performClick()
        composeTestRule.waitForIdle()

        // 7. ODJAVA
        composeTestRule.onNodeWithText("Odjavi se").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
    }
}