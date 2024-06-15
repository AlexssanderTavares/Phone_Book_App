package com.example.app_phone_book.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
    private lateinit var adapter: ContactsRecyclerViewAdapter
    private lateinit var resultAdd: ActivityResultLauncher<Intent>
    private lateinit var resultLoad: ActivityResultLauncher<Intent>
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

        db = DBHelper(this)
        this.recyclerViewLoad()


        binding.buttonLogout.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("username", "")
            editor.apply()
            finish()
        }

        binding.buttonAdd.setOnClickListener {
            resultAdd.launch(Intent(this, NewContactActivity::class.java))
        }

        resultAdd = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null && it.resultCode == 1) {
                this.recyclerViewLoad()
            } else if (it.data != null && it.resultCode == 0) {
                Toast.makeText(applicationContext, "Operation canceled!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Operation failed", Toast.LENGTH_SHORT).show()
            }
        }

        resultLoad = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null) {
                when (it.resultCode) {
                    1 -> this.recyclerViewLoad()
                    2 -> this.recyclerViewLoad()
                    3 -> this.recyclerViewLoad()
                    0 -> Toast.makeText(
                        applicationContext,
                        "Operation canceled! Nothing has been changed",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> {
                        Toast.makeText(
                            applicationContext,
                            "Operation failed! Nothing has been changed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun recyclerViewLoad() {
        val contactList: List<Contact> = db.getAllContact().sortedWith( compareBy{ it.name})
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactsRecyclerViewAdapter(contactList, ContactsRecyclerViewAdapter.OnClickListener {
                val contactActivity = Intent(this, ContactActivity::class.java)
                contactActivity.putExtra("id", it.id)
                resultLoad.launch(contactActivity)
            })
        binding.contactRecyclerView.adapter = this.adapter
        adapter.notifyDataSetChanged()
    }


}