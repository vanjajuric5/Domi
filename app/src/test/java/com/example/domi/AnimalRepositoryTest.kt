package com.example.domi

import org.junit.Test
import org.junit.Assert.*

class AnimalRepositoryTest {

    @Test
    fun getAnimalById_returnsCorrectAnimal() {
        // Tražimo životinju s ID-om 1
        val animal = AnimalRepository.getAnimalById(1)
        
        // Provjeravamo je li pronađena životinja Darcy
        assertNotNull(animal)
        assertEquals("Darcy", animal?.name)
    }

    @Test
    fun getAnimalById_returnsNullForInvalidId() {
        // Tražimo životinju s nepostojećim ID-om
        val animal = AnimalRepository.getAnimalById(99)
        
        // Provjeravamo da je rezultat null
        assertNull(animal)
    }
}
