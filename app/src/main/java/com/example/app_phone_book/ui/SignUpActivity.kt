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
import com.example.app_phone_book.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DBHelper(this)

        binding.buttonRegister.setOnClickListener {
            val userName = binding.editUserName.text.toString().trim()
            val userEmail = binding.editUserEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()

            val confirmEmail = binding.confirmUserEmail.text.toString().trim()
            val confirmPass = binding.confirmPassword.text.toString().trim()

            if(userName.isNotEmpty() && userEmail.isNotEmpty() && password.isNotEmpty() && confirmEmail.isNotEmpty() && confirmPass.isNotEmpty()){

                if(binding.checkboxAgree.isChecked){

                    if(userEmail != confirmEmail){
                        Toast.makeText(applicationContext,
                            getString(R.string.e_mail_fields_doesn_t_match), Toast.LENGTH_SHORT).show()
                        this.setBlankFields()
                    }

                    if(password != confirmPass){
                        Toast.makeText(applicationContext,
                            getString(R.string.password_fields_doesn_t_match), Toast.LENGTH_SHORT).show()
                        this.setBlankFields()
                    }

                    val res = db.createUser(userName,userEmail,password)

                    if(res != null && res > 0){
                        Toast.makeText(this, getString(R.string.sign_up_ok), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }

                }else{
                    Toast.makeText(this,
                        getString(R.string.you_must_agree_with_our_therms_and_policy), Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(applicationContext,
                    getString(R.string.no_blank_fields_allowed), Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun setBlankFields(){
        binding.editUserEmail.setText("")
        binding.confirmUserEmail.setText("")
        binding.editPassword.setText("")
        binding.confirmPassword.setText("")
        binding.editUserName.setText("")
    }
}