package com.example.domi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "domi_app.db"
        private const val DATABASE_VERSION = 7 // Verzija 7: Dodan email u zahtjeve

        const val TABLE_ANIMALS = "animals"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_BREED = "breed"
        const val COLUMN_AGE = "age"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_RES = "image_res"
        const val COLUMN_SHELTER_NAME = "shelter_name"
        const val COLUMN_IMAGE_URI = "image_uri"

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
        val createAnimalsTable = ("CREATE TABLE " + TABLE_ANIMALS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_BREED + " TEXT,"
                + COLUMN_AGE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IMAGE_RES + " INTEGER,"
                + COLUMN_SHELTER_NAME + " TEXT,"
                + COLUMN_IMAGE_URI + " TEXT" + ")")
        db.execSQL(createAnimalsTable)

        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT,"
                + COLUMN_USER_PHONE + " TEXT,"
                + COLUMN_USER_CITY + " TEXT,"
                + COLUMN_USER_EXPERIENCE + " TEXT,"
                + COLUMN_USER_IS_ADMIN + " INTEGER DEFAULT 0" + ")")
        db.execSQL(createUsersTable)

        val createRequestsTable = ("CREATE TABLE " + TABLE_REQUESTS + "("
                + COLUMN_REQ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_REQ_ANIMAL_NAME + " TEXT,"
                + COLUMN_REQ_USER_NAME + " TEXT,"
                + COLUMN_REQ_USER_EMAIL + " TEXT,"
                + COLUMN_REQ_MESSAGE + " TEXT" + ")")
        db.execSQL(createRequestsTable)
        
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
        return db.insert(TABLE_USERS, null, values)
    }

    fun getUserRole(email: String, pass: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USER_IS_ADMIN FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?",
            arrayOf(email, pass)
        )
        var role = -1
        if (cursor.moveToFirst()) {
            role = cursor.getInt(0)
        }
        cursor.close()
        return role
    }

    data class UserSession(val name: String, val email: String, val isAdmin: Boolean)
    data class UserDetails(val id: Int, val name: String, val email: String, val phone: String, val city: String, val experience: String)

    fun getLoggedInUser(email: String, pass: String): UserSession? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USER_NAME, $COLUMN_USER_EMAIL, $COLUMN_USER_IS_ADMIN FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?",
            arrayOf(email, pass)
        )
        var user: UserSession? = null
        if (cursor.moveToFirst()) {
            user = UserSession(
                name = cursor.getString(0),
                email = cursor.getString(1),
                isAdmin = cursor.getInt(2) == 1
            )
        }
        cursor.close()
        return user
    }

    fun getUserDetails(email: String): UserDetails? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USER_ID, $COLUMN_USER_NAME, $COLUMN_USER_EMAIL, $COLUMN_USER_PHONE, $COLUMN_USER_CITY, $COLUMN_USER_EXPERIENCE FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ?",
            arrayOf(email)
        )
        var user: UserDetails? = null
        if (cursor.moveToFirst()) {
            user = UserDetails(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                email = cursor.getString(2),
                phone = cursor.getString(3),
                city = cursor.getString(4),
                experience = cursor.getString(5)
            )
        }
        cursor.close()
        return user
    }

    fun updateUser(email: String, name: String, phone: String, city: String, exp: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, name)
            put(COLUMN_USER_PHONE, phone)
            put(COLUMN_USER_CITY, city)
            put(COLUMN_USER_EXPERIENCE, exp)
        }
        return db.update(TABLE_USERS, values, "$COLUMN_USER_EMAIL = ?", arrayOf(email))
    }

    fun addAnimal(name: String, breed: String, age: String, description: String, imageRes: Int, shelterName: String, imageUri: String? = null): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_BREED, breed)
            put(COLUMN_AGE, age)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_IMAGE_RES, imageRes)
            put(COLUMN_SHELTER_NAME, shelterName)
            put(COLUMN_IMAGE_URI, imageUri)
        }
        return db.insert(TABLE_ANIMALS, null, values)
    }

    fun deleteAnimal(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_ANIMALS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun addRequest(animalName: String, userName: String, userEmail: String, message: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REQ_ANIMAL_NAME, animalName)
            put(COLUMN_REQ_USER_NAME, userName)
            put(COLUMN_REQ_USER_EMAIL, userEmail)
            put(COLUMN_REQ_MESSAGE, message)
        }
        return db.insert(TABLE_REQUESTS, null, values)
    }

    data class AdoptionRequest(val id: Int, val animalName: String, val userName: String, val userEmail: String, val message: String)

    fun getAllRequests(): List<AdoptionRequest> {
        val requestList = mutableListOf<AdoptionRequest>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REQUESTS", null)
        if (cursor.moveToFirst()) {
            do {
                requestList.add(AdoptionRequest(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQ_ID)),
                    animalName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQ_ANIMAL_NAME)),
                    userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQ_USER_NAME)),
                    userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQ_USER_EMAIL)),
                    message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQ_MESSAGE))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return requestList
    }

    fun deleteRequest(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_REQUESTS, "$COLUMN_REQ_ID = ?", arrayOf(id.toString()))
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val animals = AnimalRepository.animals

        for (animal in animals) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, animal.name)
                put(COLUMN_BREED, animal.breed)
                put(COLUMN_AGE, animal.age)
                put(COLUMN_DESCRIPTION, animal.description)
                put(COLUMN_IMAGE_RES, animal.imageRes)
                put(COLUMN_SHELTER_NAME, animal.shelterName)
                put(COLUMN_IMAGE_URI, animal.imageUri)
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
                val animal = Animal(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    breed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREED)),
                    age = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    imageRes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES)),
                    shelterName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHELTER_NAME)),
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                )
                animalList.add(animal)
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
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                breed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREED)),
                age = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                imageRes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES)),
                shelterName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHELTER_NAME)),
                imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            )
        }
        cursor.close()
        return animal
    }
}
