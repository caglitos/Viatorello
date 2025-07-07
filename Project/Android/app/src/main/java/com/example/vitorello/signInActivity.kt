package com.example.vitorello

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class signInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        initComponent()
    }

    private fun initComponent() {
        FormatHtml()
        LogIn()

    }

    private fun LogIn() {
        val logInButton: Button = findViewById(R.id.logInButton)

        logInButton.setOnClickListener {
            val json = """
                {
                    "username": "${NewUser()[0]}",
                    "email": "${NewUser()[1]}",
                    "password": "${NewUser()[2]}",
                    "currentLocation": {
                        "type": "Point",
                        "coordinates": [0, 0]
                    },
                    "isOnline": true,
                    "lastLocationUpdate": "${isoDate()}",
                    "currentTrip": false
                } 
            """.trimIndent()


            postRequest("http://10.0.2.2:3000/api/register", json) { response, error ->
                runOnUiThread {
                    if (error != null) {
                        RegisterError()
                    } else {
                        RegistredSuccesfull()
                    }
                }
            }
        }
    }

    private fun FormatHtml() {
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.text =
            Html.fromHtml(getString(R.string.forgotPassword), Html.FROM_HTML_MODE_LEGACY)

        val register = findViewById<TextView>(R.id.register)
        register.text = Html.fromHtml(getString(R.string.register), Html.FROM_HTML_MODE_LEGACY)
    }

    private fun RegistredSuccesfull() {
        val logInButton: Button = findViewById(R.id.logInButton)
        val buttonMsg: TextView = findViewById(R.id.buttonMsg)
        buttonMsg.text = "Registro exitoso"
        logInButton.setBackgroundTintList(ColorStateList.valueOf(green))
        logInButton.setTextColor(black)
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun RegisterError() {
        val logInButton: Button = findViewById(R.id.logInButton)
        val buttonMsg: TextView = findViewById(R.id.buttonMsg)
        buttonMsg.text = "Error al iniciar sesión"
        logInButton.setBackgroundTintList(ColorStateList.valueOf(red))
        logInButton.setTextColor(white)
    }

    private fun NewUser(): Array<Editable> {
        val username: EditText = findViewById(R.id.userNameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)

        return arrayOf(username.text, email.text, password.text)
    }
}

