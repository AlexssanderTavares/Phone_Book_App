package com.example.app_phone_book.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.app_phone_book.Model.Contact
import com.example.app_phone_book.Model.User
import kotlin.random.Random

class DBHelper(context: Context) : SQLiteOpenHelper(context, "App_Phone_Book.db", null, 1) {

    val sqlCommands = arrayOf(
        "CREATE TABLE users(id TEXT PRIMARY KEY NOT NULL, userName TEXT UNIQUE NOT NULL, userEmail TEXT NOT NULL, password TEXT NOT NULL)",
        "CREATE TABLE contacts(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, phone TEXT NOT NULL, imageId INTEGER)",
        "INSERT INTO users(id, userName, userEmail, password) VALUES ('############','admin','admin@admin.com','admin123')",
        "INSERT INTO contacts(name, email, phone, imageId) VALUES ('teste','testeCont@teste.com','(00)00000-0000',1)",
        "INSERT INTO contacts(name, email, phone, imageId) VALUES ('teste2','testeCont2@teste.com','(11)11111-1111',2)",
    )

    override fun onCreate(db: SQLiteDatabase?) {
        sqlCommands.forEach { command -> db?.execSQL(command) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE users")
        db?.execSQL("DROP TABLE contacts")
        onCreate(db!!)
    }

    // ---------------------------------------------------------------------------------
    //                                  Users CRUD
    // ---------------------------------------------------------------------------------

    fun createUser(userName: String, userEmail: String, pass: String): Long? {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        if(this.verifyEmail(userEmail)) {
            val id = this.idGenerator()
            contentValues.put("id", id)
            contentValues.put("userName", userName)
            contentValues.put("userEmail", userEmail)
            contentValues.put("password", pass)

            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE id = ? AND userName = ? AND userEmail = ? AND password = ?",
                arrayOf(id, userName, userEmail, pass)
            )

            if (cursor.count == 0) {
                val res = db.insert("users", null, contentValues)
                db.close()
                return res
            } else {
                db.close()
                return null
            }
        }else{
            return null
        }

    }

    fun loginUser(userName: String, userEmail: String, userPass: String): Boolean {
        val db = this.readableDatabase
        return if(this.verifyEmail(userEmail)) {
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE userName = ? AND userEmail = ? AND password = ?",
                arrayOf(userName, userEmail, userPass)
            )

            if (cursor.count == 1) {
                db.close()
                true
            } else {
                db.close()
                false
            }
        }else{
            false
        }
    }

    fun selectUser(userName: String, userEmail: String) : User {
        val db = this.readableDatabase
        if(this.verifyEmail(userEmail)){
            val cursor = db.rawQuery("SELECT * FROM users WHERE userName = ? AND userEmail = ?", arrayOf(userName,userEmail))
            lateinit var user: User

            if(cursor.count == 1){

                val idIndex = cursor.getColumnIndex("id")
                val userNameIndex = cursor.getColumnIndex("userName")
                val userEmailIndex = cursor.getColumnIndex("userEmail")

                user = User(cursor.getString(idIndex), cursor.getString(userNameIndex), cursor.getString(userEmailIndex))

            }
            db.close()
            return user
        }else{
            val user = User("","","")
            return user
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

    fun updateUserEmail(id: Int, newUserEmail: String): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        if(this.verifyEmail(newUserEmail)) {
            contentValues.put("newUserEmail", newUserEmail)
            val cursor = db.update("users", contentValues, "id = ?", arrayOf(id.toString()))
            db.close()
            return cursor
        }else{
            return 0
        }
    }

    fun deleteUser(id: Int): Int {
        val db = writableDatabase
        val cursor = db.delete("users", "id = ?", arrayOf(id.toString()))
        db.close()
        return cursor
    }
    // ---------------------------------------------------------------------------------
    //                                  Contacts CRUD
    // ---------------------------------------------------------------------------------

    fun createContact(name: String, email: String, phone: String): Long? {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        if(this.verifyEmail(email)) {
            contentValues.put("name", name)
            contentValues.put("email", email)
            contentValues.put("phone", phone)


            val cursor = db.rawQuery(
                "SELECT * FROM contacts WHERE email = ? AND phone = ?",
                arrayOf(email, phone)
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

    fun getContact(id: Int, name: String, email: String, phone: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM contacts WHERE id = ? AND name = ? AND email = ? AND phone = ?",
            arrayOf(id.toString(), name, email, phone)
        )

        return if (cursor.count == 1) {
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    fun selectContact(id: Int) : Contact {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM contacts WHERE id = ?", arrayOf(id.toString()))
        lateinit var contact: Contact
        /*
        This solves the problem of cursor.getPosition returning -1
        We must move the cursor manually to the first row founded
        val counter = cursor.getCount() // returns the number of rows founded
        val position = cursor.getPosition() // -1 // return the current cursor position in the list
        if(position == -1){
           val moved = cursor.moveToFirst() // true, moved to the first row founded
        }
        val isClose = cursor.isClosed() // check if the cursor is closed

         */

        if(cursor.count == 1 && cursor.moveToFirst()){ // if moveToFirst find a row to move on as first position, it will return true
            val idIndex = cursor.getColumnIndex("id")
            val nameIndex = cursor.getColumnIndex("name")
            val emailIndex = cursor.getColumnIndex("email")
            val phoneIndex = cursor.getColumnIndex("phone")
            val imageId = cursor.getColumnIndex("imageId")

            contact = Contact(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(emailIndex), cursor.getString(phoneIndex), cursor.getInt(imageId))
            db.close()
            return contact
        }else{
            contact = Contact(0,"","","")
            db.close()
            return contact
        }
    }

    fun getAllContact(): ArrayList<Contact> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM contacts", null)
        val listContact = ArrayList<Contact>()

        if(cursor.count > 0){
            cursor.moveToFirst()
            val idIndex = cursor.getColumnIndex("id")
            val nameIndex = cursor.getColumnIndex("name")
            val emailIndex = cursor.getColumnIndex("email")
            val phoneIndex = cursor.getColumnIndex("phone")
            val imageId = cursor.getColumnIndex("imageId")

            do{
                val contact = Contact(
                    cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(emailIndex),
                    cursor.getString(phoneIndex),
                    cursor.getInt(imageId)
                )

                listContact.add(contact)
            }while(cursor.moveToNext())
        }
        db.close()
        return listContact
    }

    fun updateContactNumber(id: Int, newPhone: String) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("newPhone", newPhone)
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

    private fun verifyEmail(email:String) : Boolean {
        return if(email.contains("@")){
            true
        }else{
            false
        }
    }



    private tailrec fun idGenerator() : String {
        val db = this.readableDatabase
        val lowChar = arrayOf("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","!","?","#","@","$","%","¨","&","*","+","-","/","°") //39
        val upChar = lowChar.map{ it.uppercase() }
        val nums = arrayOf(0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9)

        val matrix = Triple(lowChar,upChar,nums)
        val res = "${matrix.first[Random.nextInt(0,39)]}${matrix.third[Random.nextInt(0,39)]}${matrix.second[Random.nextInt(0,39)]}${matrix.third[Random.nextInt(0,39)]}" +
                "${matrix.second[Random.nextInt(0,39)]}${matrix.first[Random.nextInt(0,39)]}${matrix.third[Random.nextInt(0,39)]}${matrix.third[Random.nextInt(0,39)]}" +
                "${matrix.third[Random.nextInt(0,39)]}${matrix.third[Random.nextInt(0,39)]}${matrix.first[Random.nextInt(0,39)]}${matrix.second[Random.nextInt(0,39)]}"

        val hashList = db.rawQuery("SELECT * FROM users WHERE id = ?", arrayOf(res))

        if(hashList.count == 1){
            return idGenerator()
        }else{
            return res
        }
    }
}


