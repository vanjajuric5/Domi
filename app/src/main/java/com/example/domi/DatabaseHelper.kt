package com.example.domi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "domi_app.db"
        private const val DATABASE_VERSION = 3 // Povećavamo verziju

        const val TABLE_ANIMALS = "animals"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_BREED = "breed"
        const val COLUMN_AGE = "age"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_RES = "image_res"

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USER_NAME = "name"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_PHONE = "phone"
        const val COLUMN_USER_CITY = "city"
        const val COLUMN_USER_EXPERIENCE = "experience"
        const val COLUMN_USER_IS_ADMIN = "is_admin" // 0 = korisnik, 1 = admin
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createAnimalsTable = ("CREATE TABLE " + TABLE_ANIMALS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_BREED + " TEXT,"
                + COLUMN_AGE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IMAGE_RES + " INTEGER" + ")")
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
        
        insertInitialData(db)
        insertAdmin(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANIMALS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
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

    fun addAnimal(name: String, breed: String, age: String, description: String, imageRes: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_BREED, breed)
            put(COLUMN_AGE, age)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_IMAGE_RES, imageRes)
        }
        return db.insert(TABLE_ANIMALS, null, values)
    }

    fun deleteAnimal(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_ANIMALS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        val animals = listOf(
            Animal(1, "Darcy", "Njemački prepeličar", "8 godina", "Darcy je veseo i zaigran pas...", R.drawable.darcy),
            Animal(2, "Luna", "Bosanski barak", "1 godina", "Luna je mirna i privržena ženka...", R.drawable.bari),
            Animal(3, "Maks", "Maine coon", "4 godine", "Maks je samostalan mačak...", R.drawable.img),
            Animal(4, "Bella", "Beagle", "3 godine", "Bella je prava mala dama...", R.drawable.bella),
            Animal(5, "Rex", "Mješanac", "5 godina", "Rex je snažan pas velikog srca...", R.drawable.apas)
        )

        for (animal in animals) {
            val values = ContentValues().apply {
                put(COLUMN_NAME, animal.name)
                put(COLUMN_BREED, animal.breed)
                put(COLUMN_AGE, animal.age)
                put(COLUMN_DESCRIPTION, animal.description)
                put(COLUMN_IMAGE_RES, animal.imageRes)
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
                    imageRes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES))
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
                imageRes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES))
            )
        }
        cursor.close()
        return animal
    }
}
