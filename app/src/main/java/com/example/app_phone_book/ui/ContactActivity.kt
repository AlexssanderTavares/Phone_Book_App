package com.example.app_phone_book.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_phone_book.Model.Contact
import com.example.app_phone_book.R
import com.example.app_phone_book.database.DBHelper
import com.example.app_phone_book.databinding.ActivityContactBinding

class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding
    private lateinit var db: DBHelper
    private lateinit var contact: Contact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = DBHelper(this)
        val i = intent
        val id = i.extras?.getInt("id")

        if(id != null && id > -1) {
            contact = db.selectContact(id)

            binding.contactName.setText(contact.name)
            binding.contactEmail.setText(contact.email)
            binding.contactPhone.setText(contact.phone)
            //binding.contactImage.setImageResource()
        }

        val oldName = contact.name
        val oldEmail = contact.email
        val oldPhone = contact.phone

        binding.buttonEditContact.setOnClickListener{
            if(id != null) {

                val newName = binding.contactName.text.toString().trim()
                val newEmail = binding.contactEmail.text.toString().trim()
                val newPhone = binding.contactPhone.text.toString().trim()

                if(oldName != newName && newName.isNotEmpty()){
                    val res = db.updateContactName(id, newName)
                    if(res > 0) {
                        setResult(1, i)
                    }
                }

                if(oldEmail != newEmail && newEmail.isNotEmpty()){
                    val res = db.updateContactEmail(id, newEmail)
                    if(res != null && res > 0){
                        setResult(2,i)
                    }
                }

                if(oldPhone != newPhone && newPhone.isNotEmpty()){
                    val res = db.updateContactNumber(id, newPhone)
                    if(res > 0) {
                        setResult(3, i)
                    }
                }

                Toast.makeText(applicationContext,"Update Ok!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.deleteContact.setOnClickListener{
            if(id != null){
                val res = db.deleteContact(id)
                if(res > 0){
                    setResult(1,i)
                    Toast.makeText(applicationContext, "Contact Deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    setResult(0,i)
                    finish()
                }
            }
        }

        binding.cancelEditContact.setOnClickListener{
            setResult(0,i)
            finish()
        }
    }
}