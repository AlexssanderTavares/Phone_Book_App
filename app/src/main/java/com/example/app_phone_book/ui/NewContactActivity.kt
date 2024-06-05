package com.example.app_phone_book.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_phone_book.R
import com.example.app_phone_book.database.DBHelper
import com.example.app_phone_book.databinding.ActivityNewContactBinding

class NewContactActivity : AppCompatActivity() {
    companion object{
        const val REQUEST_IMAGE_PICK = 1
    }
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


        /*if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED){
            requestGalleryPermission()
        }else {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        }

        binding.newContactImage.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                imageResult.launch(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
            }
        }

        imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode != null && it.resultCode == RESULT_OK){
                val uri = it.data?.data
                binding.newContactImage.setImageURI(uri)
            }else{
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }*/

        //For some unknown reason, OpenGLRenderer is Unable to match the desired swap behavior and
        //Uri$HierarchicalUri is not matching the desired file mapping path
        //Lots of ways of getting Image Result is deprecated and not recommended to be used anymore, so It will be implemented later with some actual image processing method, like a composable

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