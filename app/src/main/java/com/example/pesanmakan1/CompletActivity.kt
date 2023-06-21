package com.example.pesanmakan1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CompletActivity : AppCompatActivity() {

    private lateinit var  btnComplete : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complet)

        btnComplete = findViewById(R.id.buttonComplete)

        btnComplete.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}