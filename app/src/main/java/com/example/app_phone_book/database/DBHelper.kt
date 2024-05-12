package com.example.app_phone_book.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.random.Random
import kotlin.random.nextInt

class DBHelper(context: Context) : SQLiteOpenHelper(context, "AppPhoneBook.db", null, 1) {

    val sqlCommands = arrayOf(
        "CREATE TABLE users(id INTEGER PRIMARY KEY NOT NULL, userName TEXT NOT NULL, userEmail TEXT NOT NULL, password TEXT NOT NULL)",
        "CREATE TABLE contacts(id INTEGER AUTOINCREMENT NOT NULL, name TEXT NOT NULL, email TEXT, phone TEXT NOT NULL, address TEXT NOT NULL",
        "INSERT INTO users(id, userName, userMail, password) VALUES (0, teste, teste@teste.com, teste123"
    )

    override fun onCreate(db: SQLiteDatabase?) {
        sqlCommands.forEach { command -> db?.execSQL(command) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE users")
        db?.execSQL("DROP TABLE contacts")
        onCreate(db!!)
    }


    // User CRUD

    fun createUser(userName: String, userEmail: String, pass: String): Long? {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        val id = Random.nextInt(0..10000)

        contentValues.put("userId", id)
        contentValues.put("userName", userName)
        contentValues.put("userEmail", userEmail)
        contentValues.put("password", pass)

        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE id = ? AND userName = ? AND userEmail = ? AND password = ?",
            arrayOf(id.toString(), userName, userEmail, pass)
        )

        if (cursor.count == 0) {
            val res = db.insert("users", null, contentValues)
            db.close()
            return res
        }

        if (cursor.count == 1) {
            db.close()
            throw Exception("User already exist")
        }
        return null
    }

    fun getUser(id: Int, userName: String, userEmail: String, userPass: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE id = ? AND userName = ? AND userEmail = ? AND password = ?",
            arrayOf(id.toString(), userName, userEmail, userPass))

        return if (cursor.count == 1) {
            db.close()
            true
        }else {
            db.close()
            false
        }
    }

    fun updateUserName(id: Int, newUserName: String) : Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("newUserName",newUserName)
        val cursor = db.update("users", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }

    fun updateUserEmail(id: Int, newUserEmail: String) : Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("newUserEmail",newUserEmail)
        val cursor = db.update("users", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }

    fun deleteUser(id:Int) : Int {
        val db = writableDatabase
        val cursor = db.delete("users", "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }



}


