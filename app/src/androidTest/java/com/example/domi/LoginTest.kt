package com.example.domi

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun loginWithCorrectCredentials_navigatesToMain() {
        // Pokrećemo LoginActivity
        ActivityScenario.launch(LoginActivity::class.java)

        // Unosimo podatke koristeći testTagove koje smo dodali
        composeTestRule.onNodeWithTag("email_input").performTextInput("korisnik@email.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("lozinka123")

        // Klik na gumb za prijavu
        composeTestRule.onNodeWithTag("login_button").performClick()

        // Čekamo da se pojavi glavni ekran (iz MainActivity)
        //waitUntil s timeoutom od 10 sekundi
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("glavni_ekran").fetchSemanticsNodes().isNotEmpty()
        }

        // Provjeravamo da smo zaista na glavnom ekranu
        composeTestRule.onNodeWithTag("glavni_ekran").assertExists()
    }

    @Test
    fun loginWithWrongCredentials_showsErrorMessage() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Unosimo pogrešne podatke
        composeTestRule.onNodeWithTag("email_input").performTextInput("pogresan@email.com")
        composeTestRule.onNodeWithTag("password_input").performTextInput("kriva_lozinka")

        // Klik na gumb
        composeTestRule.onNodeWithTag("login_button").performClick()

        // Provjeravamo pojavljuje li se poruka o pogrešci
        composeTestRule.onNodeWithTag("error_message").assertExists()
        
        // Dodatno provjeravamo i sam tekst poruke
        composeTestRule.onNodeWithText("Neispravan email ili lozinka").assertExists()
    }
}
