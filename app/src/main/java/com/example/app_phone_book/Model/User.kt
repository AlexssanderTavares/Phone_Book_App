package com.example.app_phone_book.Model

data class User(
    var id: String = "",
    var userName: String = "",
    var userEmail: String = "",
    var userPass: String = ""
) {
    constructor(id: String, userName: String, userEmail: String) : this(id, userName, userEmail, userPass = "")
}
