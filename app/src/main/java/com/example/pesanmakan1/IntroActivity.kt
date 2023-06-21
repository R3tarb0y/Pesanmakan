package com.example.pesanmakan1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.pesanmakan1.model.UserModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class IntroActivity : AppCompatActivity() {

    private lateinit var etMeja : EditText
    private lateinit var etName : EditText
    private lateinit var buttonStart: Button
    private lateinit var buttonSave: Button

    private lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        buttonStart = findViewById(R.id.buttonStart)
        buttonSave = findViewById(R.id.buttonSave)
        etMeja = findViewById(R.id.etMeja)
        etName = findViewById(R.id.etName)

        dbRef = FirebaseDatabase.getInstance().getReference("User")

        buttonSave.setOnClickListener{
            saveUserData()
        }
        buttonStart.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun saveUserData() {
        val usName = etName.text.toString()
        val usMeja = etMeja.text.toString()

        if(usName.isEmpty()){
            etName.error = "Please Enter Name"
        }
        if(usMeja.isEmpty()){
            etMeja.error = "Please Enter Table Number"
        }
        val usId = dbRef.push().key!!

        val user = UserModel(usId, usName, usMeja)

        dbRef.child(usId).setValue(user)
            .addOnCompleteListener {
                Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show()

                etName.text.clear()
                etMeja.text.clear()



            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }

    }


}


