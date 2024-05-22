package com.example.app_phone_book.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_phone_book.R
import com.example.app_phone_book.database.DBHelper
import com.example.app_phone_book.databinding.ActivityNewContactBinding

class NewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val i = intent
        val db = DBHelper(this)

        binding.newContactImage.setOnClickListener{
            //"Implement image picker"
        }

        binding.buttonCreateContact.setOnClickListener{
            val name = binding.newContactName.text.toString().trim()
            val email = binding.newContactEmail.text.toString().trim()
            val phone = binding.newContactPhone.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()){

                val res = db.createContact(name,email,phone.toInt())
                if(res != null && res > 0) {
                    Toast.makeText(applicationContext, "Contact successfully created", Toast.LENGTH_SHORT).show()
                    setResult(1,i)
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    Toast.makeText(applicationContext, "Invalid email/phone number or contact exists", Toast.LENGTH_SHORT).show()
                    binding.newContactName.setText("")
                    binding.newContactEmail.setText("")
                    binding.newContactPhone.setText("")
                }
            }
        }

        binding.cancelCreateContact.setOnClickListener{
            setResult(0,i)
            finish()
        }
    }
}