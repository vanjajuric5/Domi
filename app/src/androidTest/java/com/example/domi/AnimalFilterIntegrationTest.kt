package com.example.domi

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class AnimalFilterIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun filterByType_updatesAnimalList_Integration() {
        // 1. Priprema testnih podataka
        val testAnimals = listOf(
            Animal(
                id = 1, name = "Darcy", breed = "Pasmina", age = "8 godina",
                ageCategory = "Senior", gender = "Ženski", healthStatus = "Zdrav",
                isVaccinated = true, isNeutered = true, isMicrochipped = true,
                description = "Opis", imageRes = null, shelterName = "Dumovec", type = "Pas"
            ),
            Animal(
                id = 2, name = "Maks", breed = "Pasmina", age = "4 godine",
                ageCategory = "Odrasli", gender = "Muški", healthStatus = "Zdrav",
                isVaccinated = true, isNeutered = false, isMicrochipped = true,
                description = "Opis", imageRes = null, shelterName = "Čakovec", type = "Mačka"
            )
        )

        // 2. Postavljamo sučelje
        composeTestRule.setContent {
            DomiTheme {
                AnimalListScreen(
                    animals = testAnimals,
                    isAdmin = false,
                    onDelete = {},
                    onAnimalClick = {}
                )
            }
        }

        // 3. Početna provjera
        composeTestRule.onNodeWithText("Darcy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Maks").assertIsDisplayed()

        // 4. INTERAKCIJA: Otvori filter
        composeTestRule.onNodeWithContentDescription("Filtriraj").performClick()

        // 5. FILTRIRANJE: Klikni točno na polje za VRSTU koristeći novi tag
        composeTestRule.onNodeWithTag("filter_vrsta").performClick()
        
        // Odaberi "Mačka"
        composeTestRule.onNodeWithText("Mačka").performClick()

        // 6. FINALNA PROVJERA: Darcy (pas) više NE smije biti na ekranu, Maks (mačka) MORA
        composeTestRule.onNodeWithText("Darcy").assertDoesNotExist()
        composeTestRule.onNodeWithText("Maks").assertIsDisplayed()
    }
}
