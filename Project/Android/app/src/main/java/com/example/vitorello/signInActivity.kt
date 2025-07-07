package com.example.vitorello

import android.os.Bundle
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
        val username: EditText = findViewById(R.id.userNameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)
        val buttonMsg: TextView = findViewById(R.id.buttonMsg)


        logInButton.setOnClickListener {


            val json = """
                {
                    "username": "${username.text}",
                    "email": "${email.text}",
                    "password": "${password.text}",
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
                        Log.i("LogIn", "Error: ${error.message}")
                        buttonMsg.text = "Error al iniciar sesión"
                    } else {

                        buttonMsg.text = "Registro exitoso"
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



}