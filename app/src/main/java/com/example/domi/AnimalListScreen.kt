package com.example.domi

data class Animal(
    val id: Int,
    val name: String,
    val breed: String,
    val age: String,
    val description: String,
    val imageRes: Int,
)

object AnimalRepository {
    val animals = listOf(
        Animal(1, "Darcy", "Njemački prepeličar", "8 godina", "Darcy je veseo i zaigran pas...", R.drawable.darcy),
        Animal(2, "Luna", "Bosanski barak", "1 godina", "Luna je mirna i privržena ženka...", R.drawable.bari),
        Animal(3, "Maks", "Maine coon", "4 godine", "Maks je samostalan mačak...", R.drawable.img),
        Animal(4, "Bella", "Beagle", "3 godine", "Bella je prava mala dama...", R.drawable.bella),
        Animal(5, "Rex", "Mješanac", "5 godina", "Rex je snažan pas velikog srca...", R.drawable.apas),
    )
}
