package com.example.vitorello

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class signInActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hacer barra de notificaciones y navegación transparentes
        window.insetsController?.let { controller ->
            controller.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.setDecorFitsSystemWindows(false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContentView(R.layout.activity_sign_in)

        initComponent()
    }

    private fun initComponent() {
//        isAuth()
        formatHtml()
        initRegisterText()

        lifecycleScope.launch {
            logIn()
        }

    }

    private fun initRegisterText() {
        val register: TextView = findViewById(R.id.register)

        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isAuth() {
        val token = getAuthToken(this)

        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private suspend fun logIn() {
        val logInButton: Button = findViewById(R.id.logInButton)
        val geoJson = getCurrentLocationAsGeoJsonPoint(this@signInActivity)

        logInButton.setOnClickListener {
            val json = """
                {
                    "username": "${newUser()[0]}",
                    "email": "${newUser()[1]}",
                    "password": "${newUser()[2]}",
                    "currentLocation": $geoJson,
                    "isOnline": true,
                    "lastLocationUpdate": "${isoDate()}",
                    "currentTrip": false
                }
            """.trimIndent()

            Log.d("LogIn", "LogIn: $json")

            postRequest("https://viatorello-production.up.railway.app/api/login", json) { res, error ->
                runOnUiThread {
                    if (error != null) {
                        registerError()
                    } else {
                        registeredSuccessful(res)
                    }
                }
            }
        }
    }

    private fun formatHtml() {
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.text =
            Html.fromHtml(getString(R.string.forgotPassword), Html.FROM_HTML_MODE_LEGACY)

        val register = findViewById<TextView>(R.id.register)
        register.text = Html.fromHtml(getString(R.string.register), Html.FROM_HTML_MODE_LEGACY)
    }

    private fun registeredSuccessful(res: String?) {
        val logInButton: Button = findViewById(R.id.logInButton)
        val buttonMsg: TextView = findViewById(R.id.buttonMsg)

        buttonMsg.text = getString(R.string.LogInSucces)
        ViewCompat.setBackgroundTintList(
            logInButton,
            ContextCompat.getColorStateList(this, R.color.green)
        )
        logInButton.setTextColor(ContextCompat.getColor(this, R.color.black))

        startActivity(Intent(this, MainActivity::class.java))

        saveAuthToken(this, res)
    }

    private fun registerError() {
        val logInButton: Button = findViewById(R.id.logInButton)
        val buttonMsg: TextView = findViewById(R.id.buttonMsg)
        buttonMsg.text = getString(R.string.LogInError)
        ViewCompat.setBackgroundTintList(
            logInButton,
            ContextCompat.getColorStateList(this, R.color.red)
        )
        logInButton.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun newUser(): Array<Editable> {
        val username: EditText = findViewById(R.id.userNameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)

        return arrayOf(username.text, email.text, password.text)
    }
}
