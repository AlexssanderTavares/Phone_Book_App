package com.example.app_phone_book.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_phone_book.Model.Contact
import com.example.app_phone_book.R
import com.example.app_phone_book.database.DBHelper
import com.example.app_phone_book.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ArrayAdapter<Contact>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DBHelper(this)
        val contacts = db.getAllContact()
        adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, contacts)

        sharedPreferences = application.getSharedPreferences("login", MODE_PRIVATE)
        binding.listViewContacts.adapter = adapter

        binding.buttonLogout.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("username", "")
            editor.apply()
            finish()
        }

        binding.buttonAdd.setOnClickListener{
            startActivity(Intent(this, NewContactActivity::class.java))
        }
    }
}