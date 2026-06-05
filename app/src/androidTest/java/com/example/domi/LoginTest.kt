package com.example.domi

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    // Koristimo EmptyComposeRule jer je fleksibilniji za rad s više Activity-ja
    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun loginWithCorrectCredentials_navigatesToMain() {
        // Pokrećemo LoginActivity
        ActivityScenario.launch(LoginActivity::class.java)

        // Unosimo email (tražimo tekst koji je u labeli)
        composeTestRule.onNodeWithText("Email").performTextInput("korisnik@email.com")
        
        // Unosimo lozinku
        composeTestRule.onNodeWithText("Lozinka").performTextInput("lozinka123")
        
        // Kliknemo na gumb
        composeTestRule.onNodeWithText("PRIJAVI SE").performClick()
        
        // Čekamo da se pojavi naslov "Ljubimci" na novom ekranu
        // Budući da ih ima više, čekamo dok se ne pojavi barem jedan
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Ljubimci").fetchSemanticsNodes().isNotEmpty()
        }
        
        // Potvrđujemo da barem jedan postoji
        composeTestRule.onAllNodesWithText("Ljubimci").onFirst().assertExists()
    }

    @Test
    fun loginWithWrongCredentials_showsErrorMessage() {
        ActivityScenario.launch(LoginActivity::class.java)

        composeTestRule.onNodeWithText("Email").performTextInput("pogresan@email.com")
        composeTestRule.onNodeWithText("Lozinka").performTextInput("kriva_lozinka")
        
        composeTestRule.onNodeWithText("PRIJAVI SE").performClick()
        
        // Provjeravamo poruku o pogrešci
        composeTestRule.onNodeWithText("Neispravan email ili lozinka").assertExists()
    }
}
