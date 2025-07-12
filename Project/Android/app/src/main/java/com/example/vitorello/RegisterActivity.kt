package com.example.vitorello

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_register)

        initComponent()
    }

    fun initComponent() {
//        isAuth()
        val logInText: TextView = findViewById(R.id.LogIN)

        logInText.setOnClickListener {
            Log.d("SignIn", "Navigating to SignInActivity")
            startActivity(Intent(this, signInActivity::class.java))
        }
    }

    private fun isAuth() {
        val token = getAuthToken(this)

        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}