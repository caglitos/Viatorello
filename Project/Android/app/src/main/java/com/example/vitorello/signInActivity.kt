package com.example.vitorello

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.view.View

import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class signInActivity : AppCompatActivity() {
    val TAG = "SignInActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        setContentView(R.layout.activity_sign_in)

        initComponent()
    }

    private fun initComponent() {
//        isAuth()
        formatHtml()
        initRegisterText()

        togglePwdVisibility()

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
        val logInButton: Button = findViewById(R.id.registerButton)
        val geoJson = getCurrentGeoJsonPoint(this@signInActivity)

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

            postRequest(
                "https://viatorello-production.up.railway.app/api/auth/login",
                json
            ) { res, error ->
//            postRequest("http://10.0.2.2:3000/api/auth/login", json) { res, error ->
                runOnUiThread {
                    if (error != null) {
                        Log.d(TAG, "logIn: Error $error")
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
        val logInButton: Button = findViewById(R.id.registerButton)
        val buttonMsg: TextView = findViewById(R.id.buttonMsg)

        buttonMsg.text = getString(R.string.LogInSucces)
        ViewCompat.setBackgroundTintList(
            logInButton,
            ContextCompat.getColorStateList(this, R.color.green)
        )
        logInButton.setTextColor(ContextCompat.getColor(this, R.color.black))

        startActivity(Intent(this, MainActivity::class.java))

        saveAuth(this, res)
    }

    private fun registerError() {
        val logInButton: Button = findViewById(R.id.registerButton)
        val buttonMsg: TextView = findViewById(R.id.buttonMsg)

        buttonMsg.text = getString(R.string.LogInError)

        ViewCompat.setBackgroundTintList(
            logInButton,
            ContextCompat.getColorStateList(this, R.color.red)
        )
        logInButton.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun togglePwdVisibility() {
        val pwdVisibilityTogle: ImageButton = findViewById(R.id.pwdVisibility)

        pwdVisibilityTogle.setOnClickListener {
            val password: EditText = findViewById(R.id.passwordInput)
            if (password.inputType == 129) { // 129 is the input type for password
                password.inputType = 144 // 144 is the input type for text visible password
                pwdVisibilityTogle.setImageResource(R.drawable.pwdtoggle1)
            } else {
                password.inputType = 129
                pwdVisibilityTogle.setImageResource(R.drawable.pwdtoggle0)
            }
            password.setSelection(password.text.length) // Move cursor to the end
        }
    }
    private fun newUser(): Array<Editable> {
        val username: EditText = findViewById(R.id.userNameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)

        return arrayOf(username.text, email.text, password.text)
    }
}
