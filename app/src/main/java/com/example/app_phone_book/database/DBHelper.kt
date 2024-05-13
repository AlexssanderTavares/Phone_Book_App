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
        "CREATE TABLE contacts(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, phone TEXT NOT NULL, address TEXT",
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

        if(this.verifyEmail(userEmail)) {
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
            } else {
                val res = null
                db.close()
                return res
            }
        }else{
            return null
        }

    }

    fun getUser(id: Int, userName: String, userEmail: String, userPass: String): Boolean {
        val db = readableDatabase
        if(this.verifyEmail(userEmail)) {
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE id = ? AND userName = ? AND userEmail = ? AND password = ?",
                arrayOf(id.toString(), userName, userEmail, userPass)
            )

            return if (cursor.count == 1) {
                db.close()
                true
            } else {
                db.close()
                false
            }
        }else{
            return false
        }
    }

    fun updateUserName(id: Int, newUserName: String): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("newUserName", newUserName)
        val cursor = db.update("users", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }

    fun updateUserEmail(id: Int, newUserEmail: String): Int? {
        val db = writableDatabase
        val contentValues = ContentValues()
        if(this.verifyEmail(newUserEmail)) {
            contentValues.put("newUserEmail", newUserEmail)
            val cursor = db.update("users", contentValues, "id = ?", arrayOf(id.toString()))
            db.close()
            return cursor
        }else{
            return null
        }
    }

    fun deleteUser(id: Int): Int {
        val db = writableDatabase
        val cursor = db.delete("users", "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }

    // Contacts CRUD

    fun createContact(name: String, email: String, phone: Int, address: String = ""): Long? {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        if(this.verifyEmail(email)) {
            contentValues.put("name", name)
            contentValues.put("email", email)
            contentValues.put("phone", this.toBRPhoneFormat(phone))
            contentValues.put("address", address)

            val cursor = db.rawQuery(
                "SELECT * FROM contacts WHERE id = ? AND email = ? AND phone = ?",
                arrayOf(email, this.toBRPhoneFormat(phone))
            )

            if (cursor.count == 0) {
                val res = db.insert("contacts", null, contentValues)
                db.close()
                return res
            } else {
                val res = null
                db.close()
                return res
            }
        }else {
            return null
        }
    }

    fun getContact(id: Int, name: String, email: String, phone: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM contacts WHERE id = ? AND name = ? AND email = ? AND phone = ?",
            arrayOf(id.toString(), name, email, this.toBRPhoneFormat(phone))
        )

        return if (cursor.count == 1) {
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    fun updateContactNumber(id: Int, newPhone: Int) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("newPhone", toBRPhoneFormat(newPhone))
        val cursor = db.update("contacts", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }

    fun updateContactName(id: Int, newName: String) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("newName", newName)
        val cursor = db.update("contacts", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }

    fun updateContactEmail(id: Int, newEmail: String) : Int? {
        val db = this.writableDatabase
        if(this.verifyEmail(newEmail)){
            val contentValues = ContentValues()
            contentValues.put("newEmail", newEmail)
            val cursor = db.update("contacts", contentValues, "id = ?", arrayOf(id.toString()))
            db.close()
            return cursor
        }else {
            return null
        }

    }

    fun deleteContact(id:Int) : Int {
        val db = this.writableDatabase
        val cursor = db.delete("contacts", "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }

    // END of CRUDs

    fun verifyEmail(email:String) : Boolean {
        return if(email.contains("@")){
            true
        }else{
            false
        }
    }

    fun toBRPhoneFormat(phone: Int) : String {
        val phone = phone.toString()
        if (phone.length == 11) {
            val res = "(${phone[0]}${phone[1]} - ${phone[2]}${phone[3]}${phone[4]}${phone[5]}${phone[6]} - " +
                    "${phone[7]}${phone[8]}${phone[9]}${phone[10]}"
            return res
        }else if(phone.length == 10){
            val res = "(${phone[0]}${phone[1]} - ${phone[2]}${phone[3]}${phone[4]}${phone[5]} - ${phone[6]}" +
                    "${phone[7]}${phone[8]}${phone[9]}"
            return res
        }else{
            return "Invalid Phone Number"
        }
    }
}


