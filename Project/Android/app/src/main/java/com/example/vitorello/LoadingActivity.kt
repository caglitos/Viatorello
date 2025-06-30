package com.example.vitorello

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vitorello.ui.login.LoginActivity
import android.util.Log

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initApp()
        initLoged()
    }

    private fun initLoged() {
        if (!isLoged()) {
            // Navegar a la pantalla principal o dashboard
            startActivity(Intent(this, MainActivity::class.java))
            Log.d("MyApp", "Main")
            finish()
        } else {
            // Navegar a la pantalla de inicio de sesión
            startActivity(Intent(this, LoginActivity::class.java))
            Log.d("MyApp", "Login")
            finish()
        }
    }

    private fun isLoged(): Boolean {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val isUserLoggedIn = sharedPreferences.getBoolean("isUserLoggedIn", false)

        return isUserLoggedIn
    }

    private fun initApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}