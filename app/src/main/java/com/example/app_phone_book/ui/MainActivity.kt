package com.example.app_phone_book.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_phone_book.Model.Contact
import com.example.app_phone_book.R
import com.example.app_phone_book.adapter.ContactsRecyclerViewAdapter
import com.example.app_phone_book.database.DBHelper
import com.example.app_phone_book.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ArrayAdapter<Contact>
    private lateinit var result: ActivityResultLauncher<Intent>
    private lateinit var db: DBHelper
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

        sharedPreferences = application.getSharedPreferences("login", MODE_PRIVATE)

        loadContacts()

        binding.buttonLogout.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("username", "")
            editor.apply()
            finish()
        }

        binding.buttonAdd.setOnClickListener{
            result.launch(Intent(this, NewContactActivity::class.java))
        }

        result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.data != null && it.resultCode == 1){
                adapter.notifyDataSetChanged()
            }else if(it.data != null && it.resultCode == 0){
                Toast.makeText(applicationContext, "Operation canceled!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "Operation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun loadContacts(){
        db = DBHelper(this)
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ContactsRecyclerViewAdapter(db.getAllContact(), ContactsRecyclerViewAdapter.OnClickListener{
            result.launch(Intent(this, ContactActivity::class.java))
        })
        binding.contactRecyclerView.adapter = adapter
    }
}