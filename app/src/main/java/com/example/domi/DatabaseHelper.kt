package com.example.domi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "domi_app.db"
        private const val DATABASE_VERSION = 11

        const val TABLE_ANIMALS = "animals"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_BREED = "breed"
        const val COLUMN_AGE = "age"
        const val COLUMN_AGE_CATEGORY = "age_category"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_HEALTH = "health_status"
        const val COLUMN_VACCINATED = "vaccinated"
        const val COLUMN_NEUTERED = "neutered"
        const val COLUMN_MICROCHIPPED = "microchipped"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_RES = "image_res"
        const val COLUMN_SHELTER_NAME = "shelter_name"
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_TYPE = "type"

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_PHONE = "phone"
        const val COLUMN_USER_CITY = "city"
        const val COLUMN_USER_EXPERIENCE = "experience"
        const val COLUMN_USER_IS_ADMIN = "is_admin"

        const val TABLE_REQUESTS = "requests"
        const val COLUMN_REQ_ID = "req_id"
        const val COLUMN_REQ_ANIMAL_NAME = "animal_name"
        const val COLUMN_REQ_USER_NAME = "user_name"
        const val COLUMN_REQ_USER_EMAIL = "user_email"
        const val COLUMN_REQ_MESSAGE = "message"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_ANIMALS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_BREED TEXT, $COLUMN_AGE TEXT, $COLUMN_AGE_CATEGORY TEXT, $COLUMN_GENDER TEXT, $COLUMN_HEALTH TEXT, $COLUMN_VACCINATED INTEGER, $COLUMN_NEUTERED INTEGER, $COLUMN_MICROCHIPPED INTEGER, $COLUMN_DESCRIPTION TEXT, $COLUMN_IMAGE_RES INTEGER, $COLUMN_SHELTER_NAME TEXT, $COLUMN_IMAGE_URI TEXT, $COLUMN_TYPE TEXT)")
        db.execSQL("CREATE TABLE $TABLE_USERS ($COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USER_NAME TEXT, $COLUMN_USER_EMAIL TEXT UNIQUE, $COLUMN_USER_PASSWORD TEXT, $COLUMN_USER_PHONE TEXT, $COLUMN_USER_CITY TEXT, $COLUMN_USER_EXPERIENCE TEXT, $COLUMN_USER_IS_ADMIN INTEGER DEFAULT 0)")
        db.execSQL("CREATE TABLE $TABLE_REQUESTS ($COLUMN_REQ_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_REQ_ANIMAL_NAME TEXT, $COLUMN_REQ_USER_NAME TEXT, $COLUMN_REQ_USER_EMAIL TEXT, $COLUMN_REQ_MESSAGE TEXT)")
        
        insertInitialData(db)
        insertAdmin(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REQUESTS")
        onCreate(db)
    }

    private fun insertAdmin(db: SQLiteDatabase) {
        val adminValues = ContentValues().apply {
            put(COLUMN_USER_NAME, "Admin")
            put(COLUMN_USER_EMAIL, "admin@domi.com")
            put(COLUMN_USER_PASSWORD, "admin123")
            put(COLUMN_USER_IS_ADMIN, 1)
        }
        db.insert(TABLE_USERS, null, adminValues)
    }

    fun registerUser(name: String, email: String, pass: String, phone: String, city: String, exp: String): Long {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_USER_NAME, name)
                put(COLUMN_USER_EMAIL, email)
                put(COLUMN_USER_PASSWORD, pass)
                put(COLUMN_USER_PHONE, phone)
                put(COLUMN_USER_CITY, city)
                put(COLUMN_USER_EXPERIENCE, exp)
                put(COLUMN_USER_IS_ADMIN, 0)
            }
            db.insert(TABLE_USERS, null, values)
        } catch (e: Exception) { -1L }
    }

    fun getLoggedInUser(email: String, pass: String): UserSession? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_USER_NAME, $COLUMN_USER_EMAIL, $COLUMN_USER_IS_ADMIN FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?", arrayOf(email, pass))
        var user: UserSession? = null
        if (cursor.moveToFirst()) {
            user = UserSession(cursor.getString(0) ?: "", cursor.getString(1) ?: "", cursor.getInt(2) == 1)
        }
        cursor.close()
        return user
    }

    fun getUserDetails(email: String): UserDetails? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_USER_ID, $COLUMN_USER_NAME, $COLUMN_USER_EMAIL, $COLUMN_USER_PHONE, $COLUMN_USER_CITY, $COLUMN_USER_EXPERIENCE FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ?", arrayOf(email))
        var user: UserDetails? = null
        if (cursor.moveToFirst()) {
            user = UserDetails(
                id = cursor.getInt(0),
                name = cursor.getString(1) ?: "",
                email = cursor.getString(2) ?: "",
                phone = cursor.getString(3) ?: "",
                city = cursor.getString(4) ?: "",
                experience = cursor.getString(5) ?: ""
            )
        }
        cursor.close()
        return user
    }

    fun updateUser(email: String, name: String, phone: String, city: String, exp: String): Int {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_USER_NAME, name)
                put(COLUMN_USER_PHONE, phone)
                put(COLUMN_USER_CITY, city)
                put(COLUMN_USER_EXPERIENCE, exp)
            }
            db.update(TABLE_USERS, values, "$COLUMN_USER_EMAIL = ?", arrayOf(email))
        } catch (e: Exception) { 0 }
    }

    fun addAnimal(name: String, breed: String, age: String, ageCategory: String, gender: String, health: String, isVaccinated: Boolean, isNeutered: Boolean, isMicrochipped: Boolean, description: String, imageRes: Int?, shelterName: String, type: String, imageUri: String? = null): Long {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_BREED, breed)
                put(COLUMN_AGE, age)
                put(COLUMN_AGE_CATEGORY, ageCategory)
                put(COLUMN_GENDER, gender)
                put(COLUMN_HEALTH, health)
                put(COLUMN_VACCINATED, if (isVaccinated) 1 else 0)
                put(COLUMN_NEUTERED, if (isNeutered) 1 else 0)
                put(COLUMN_MICROCHIPPED, if (isMicrochipped) 1 else 0)
                put(COLUMN_DESCRIPTION, description)
                put(COLUMN_IMAGE_RES, imageRes)
                put(COLUMN_SHELTER_NAME, shelterName)
                put(COLUMN_TYPE, type)
                put(COLUMN_IMAGE_URI, imageUri)
            }
            db.insert(TABLE_ANIMALS, null, values)
        } catch (e: Exception) { -1L }
    }

    fun deleteAnimal(id: Int): Int {
        return try {
            val db = this.writableDatabase
            db.delete(TABLE_ANIMALS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        } catch (e: Exception) { 0 }
    }

    fun addRequest(animalName: String, userName: String, userEmail: String, message: String): Long {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_REQ_ANIMAL_NAME, animalName)
                put(COLUMN_REQ_USER_NAME, userName)
                put(COLUMN_REQ_USER_EMAIL, userEmail)
                put(COLUMN_REQ_MESSAGE, message)
            }
            db.insert(TABLE_REQUESTS, null, values)
        } catch (e: Exception) { -1L }
    }

    fun getAllRequests(): List<AdoptionRequest> {
        val requestList = mutableListOf<AdoptionRequest>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REQUESTS", null)
        if (cursor.moveToFirst()) {
            do {
                requestList.add(AdoptionRequest(cursor.getInt(0), cursor.getString(1) ?: "", cursor.getString(2) ?: "", cursor.getString(3) ?: "", cursor.getString(4) ?: ""))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return requestList
    }

    fun deleteRequest(id: Int): Int {
        return try {
            val db = this.writableDatabase
            db.delete(TABLE_REQUESTS, "$COLUMN_REQ_ID = ?", arrayOf(id.toString()))
        } catch (e: Exception) { 0 }
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val initialAnimals = AnimalRepository.animals
        for (animal in initialAnimals) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, animal.name)
                put(COLUMN_BREED, animal.breed)
                put(COLUMN_AGE, animal.age)
                put(COLUMN_AGE_CATEGORY, animal.ageCategory)
                put(COLUMN_GENDER, animal.gender)
                put(COLUMN_HEALTH, animal.healthStatus)
                put(COLUMN_VACCINATED, if (animal.isVaccinated) 1 else 0)
                put(COLUMN_NEUTERED, if (animal.isNeutered) 1 else 0)
                put(COLUMN_MICROCHIPPED, if (animal.isMicrochipped) 1 else 0)
                put(COLUMN_DESCRIPTION, animal.description)
                put(COLUMN_IMAGE_RES, animal.imageRes)
                put(COLUMN_SHELTER_NAME, animal.shelterName)
                put(COLUMN_IMAGE_URI, animal.imageUri)
                put(COLUMN_TYPE, animal.type)
            }
            db.insert(TABLE_ANIMALS, null, values)
        }
    }
    
    fun getAllAnimals(): List<Animal> {
        val animalList = mutableListOf<Animal>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ANIMALS", null)
        if (cursor.moveToFirst()) {
            do {
                animalList.add(Animal(
                    id = cursor.getInt(0),
                    name = cursor.getString(1) ?: "",
                    breed = cursor.getString(2) ?: "",
                    age = cursor.getString(3) ?: "",
                    ageCategory = cursor.getString(4) ?: "Odrasli",
                    gender = cursor.getString(5) ?: "Nepoznato",
                    healthStatus = cursor.getString(6) ?: "Zdrav",
                    isVaccinated = cursor.getInt(7) == 1,
                    isNeutered = cursor.getInt(8) == 1,
                    isMicrochipped = cursor.getInt(9) == 1,
                    description = cursor.getString(10) ?: "",
                    imageRes = if (cursor.isNull(11)) null else cursor.getInt(11),
                    shelterName = cursor.getString(12) ?: "",
                    imageUri = cursor.getString(13),
                    type = cursor.getString(14) ?: "Pas"
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return animalList
    }

    fun getAnimalById(id: Int): Animal? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ANIMALS WHERE $COLUMN_ID = ?", arrayOf(id.toString()))
        var animal: Animal? = null
        if (cursor.moveToFirst()) {
            animal = Animal(
                id = cursor.getInt(0),
                name = cursor.getString(1) ?: "",
                breed = cursor.getString(2) ?: "",
                age = cursor.getString(3) ?: "",
                ageCategory = cursor.getString(4) ?: "Odrasli",
                gender = cursor.getString(5) ?: "Nepoznato",
                healthStatus = cursor.getString(6) ?: "Zdrav",
                isVaccinated = cursor.getInt(7) == 1,
                isNeutered = cursor.getInt(8) == 1,
                isMicrochipped = cursor.getInt(9) == 1,
                description = cursor.getString(10) ?: "",
                imageRes = if (cursor.isNull(11)) null else cursor.getInt(11),
                shelterName = cursor.getString(12) ?: "",
                imageUri = cursor.getString(13),
                type = cursor.getString(14) ?: "Pas"
            )
        }
        cursor.close()
        return animal
    }

    data class UserSession(val name: String, val email: String, val isAdmin: Boolean)
    data class UserDetails(val id: Int, val name: String, val email: String, val phone: String, val city: String, val experience: String)
    data class AdoptionRequest(val id: Int, val animalName: String, val userName: String, val userEmail: String, val message: String)
}
