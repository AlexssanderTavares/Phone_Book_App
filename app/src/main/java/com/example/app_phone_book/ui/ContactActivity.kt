package com.example.app_phone_book.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
    private val REQUEST_PHONE_CALL = 1

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

        if (id != null && id > -1) {
            contact = db.selectContact(id)

            binding.contactName.setText(contact.name)
            binding.contactEmail.setText(contact.email)
            binding.contactPhone.setText(contact.phone)
            //binding.contactImage.setImageResource()
        }

        val oldName = contact.name
        val oldEmail = contact.email
        val oldPhone = contact.phone

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendEmailButton.setOnClickListener{
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/plain"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(contact.email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello! Sent By App_Phone_Book")

            try{
                startActivity(Intent.createChooser(emailIntent, "Choose Email Client"))
            }catch(e: Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }

        }

        binding.callButton.setOnClickListener{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            }else{
                val dialIntent = Intent(Intent.ACTION_CALL, Uri.parse("Tel:" + this.contact.phone))
                startActivity(dialIntent)
            }
        }

        binding.buttonEditContact.setOnClickListener {
            changeState(true)
        }

        binding.confirmButton.setOnClickListener {
            if (id != null) {

                val newName = binding.contactName.text.toString().trim()
                val newEmail = binding.contactEmail.text.toString().trim()
                val newPhone = binding.contactPhone.text.toString().trim()

                if (oldName != newName && newName.isNotEmpty()) {
                    val res = db.updateContactName(id, newName)
                    if (res > 0) {
                        setResult(1, i)
                    }
                }

                if (oldEmail != newEmail && newEmail.isNotEmpty()) {
                    val res = db.updateContactEmail(id, newEmail)
                    if (res != null && res > 0) {
                        setResult(2, i)
                    }
                }

                if (oldPhone != newPhone && newPhone.isNotEmpty()) {
                    val res = db.updateContactNumber(id, newPhone)
                    if (res > 0) {
                        setResult(3, i)
                    }
                }

                Toast.makeText(applicationContext, "Update Ok!", Toast.LENGTH_SHORT).show()
                changeState(false)
            }
        }

        binding.deleteContact.setOnClickListener {
            if (id != null) {
                val res = db.deleteContact(id)
                if (res > 0) {
                    setResult(1, i)
                    Toast.makeText(applicationContext, "Contact Deleted", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    setResult(0, i)
                    binding.buttonEditContact.visibility = View.VISIBLE
                    binding.confirmButton.visibility = View.GONE
                }
            }
        }

        binding.cancelEditContact.setOnClickListener{
            binding.contactName.setText(oldName)
            binding.contactEmail.setText(oldEmail)
            binding.contactPhone.setText(oldPhone)
            changeState(false)
        }

        binding.cancelButton.setOnClickListener {
            setResult(0, i)
            finish()
        }
    }

    private fun changeState(switch:Boolean){
        if(switch){
            binding.contactImage.isEnabled = switch
            binding.contactName.isEnabled = switch
            binding.contactEmail.isEnabled = switch
            binding.contactPhone.isEnabled = switch
            binding.cancelEditContact.visibility = View.VISIBLE
            binding.confirmButton.visibility = View.VISIBLE
            binding.cancelButton.visibility = View.GONE
            binding.buttonEditContact.visibility = View.GONE
        }else {
            binding.contactImage.isEnabled = switch
            binding.contactName.isEnabled = switch
            binding.contactEmail.isEnabled = switch
            binding.contactPhone.isEnabled = switch
            binding.cancelEditContact.visibility = View.GONE
            binding.confirmButton.visibility = View.GONE
            binding.cancelButton.visibility = View.VISIBLE
            binding.buttonEditContact.visibility = View.VISIBLE
        }
    }
}