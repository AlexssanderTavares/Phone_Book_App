package com.example.app_phone_book.UI

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app_phone_book.R
import com.example.app_phone_book.database.DBHelper
import com.example.app_phone_book.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DBHelper(this)

        sharedPreferences = application.getSharedPreferences("login", Context.MODE_PRIVATE)

        binding.buttonLogin.setOnClickListener {
            val loginUserName = binding.editUserName.text.toString().trim()
            val loginEmail = binding.editUserEmail.text.toString().trim()
            val loginPass = binding.editPassword.text.toString().trim()
            val keeper = binding.checkboxLogged.isChecked

            if(loginUserName.isNotEmpty() && loginPass.isNotEmpty()){
                if(db.loginUser(loginUserName, loginEmail, loginPass)){
                    if(keeper){
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("username", loginUserName)
                        editor.apply()
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    Toast.makeText(applicationContext,
                        getString(R.string.user_not_already_registered), Toast.LENGTH_SHORT).show()
                    binding.editUserName.setText("")
                    binding.editUserEmail.setText("")
                    binding.editPassword.setText("")
                }
            }else{
                Toast.makeText(applicationContext,
                    getString(R.string.no_blank_fields_allowed), Toast.LENGTH_SHORT).show()
            }

        }

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.recoverText.setOnClickListener {

        }


    }
}