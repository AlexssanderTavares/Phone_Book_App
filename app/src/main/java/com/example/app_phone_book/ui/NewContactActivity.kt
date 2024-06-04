package com.example.app_phone_book.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_phone_book.R
import com.example.app_phone_book.database.DBHelper
import com.example.app_phone_book.databinding.ActivityNewContactBinding

class NewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewContactBinding
    private lateinit var imageResult: ActivityResultLauncher<Intent>
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

        //-----------------------------------IMAGE CONTEXT----------------------------------------
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED){
            requestGalleryPermission()
        }

        binding.newContactImage.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED){
                imageResult.launch(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }
        }

        imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode != null && it.resultCode == RESULT_OK){
                val uri = it.data?.data
                val uriAddress = uri.toString()
                val bitmap = MediaStore.Images.Media.getContentUri(uriAddress)
            }else{

            }
        }

        //-----------------------------------IMAGE CONTEXT----------------------------------------

        binding.buttonCreateContact.setOnClickListener{
            val name = binding.newContactName.text.toString().trim()
            val email = binding.newContactEmail.text.toString().trim()
            val phone = binding.newContactPhone.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()){

                val res = db.createContact(name,email, this.phoneFormater(phone))
                if(res != null && res > 0) {
                    Toast.makeText(applicationContext, "Contact successfully created", Toast.LENGTH_SHORT).show()
                    setResult(1,i)
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Invalid email/phone number or contact exists", Toast.LENGTH_SHORT).show()
                    setResult(0,i)
                    finish()
                }
            }
        }

        binding.cancelCreateContact.setOnClickListener{
            setResult(0,i)
            finish()
        }
    }

    private fun phoneFormater(phone: String) : String {
        if (phone.length == 11) {
            val res = "(${phone[0]}${phone[1]})  ${phone[2]}${phone[3]}${phone[4]}${phone[5]}${phone[6]} - " +
                    "${phone[7]}${phone[8]}${phone[9]}${phone[10]}"
            return res
        }else if(phone.length == 10){
            val res = "(${phone[0]}${phone[1]})  ${phone[2]}${phone[3]}${phone[4]}${phone[5]} - ${phone[6]}" +
                    "${phone[7]}${phone[8]}${phone[9]}"
            return res
        }else{
            return "Invalid Phone Number"
        }
    }

    private fun requestGalleryPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), PackageManager.PERMISSION_GRANTED)
    }

}