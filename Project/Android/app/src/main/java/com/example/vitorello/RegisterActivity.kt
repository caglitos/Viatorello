package com.example.vitorello

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"
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
        initLoginText()

        lifecycleScope.launch {
            initRegisterFetch()
        }
    }

    private fun initLoginText() {
        val logInText: TextView = findViewById(R.id.LogIN)

        logInText.setOnClickListener {
            Log.d("SignIn", "Navigating to SignInActivity")
            startActivity(Intent(this, signInActivity::class.java))
        }
    }

    private suspend fun initRegisterFetch() {
        val pwdMsg = findViewById<TextView>(R.id.passwordMsg)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val geoJson = getCurrentLocationAsGeoJsonPoint(this@RegisterActivity)

        registerBtn.setOnClickListener {
            val confirmpwd = confirmPasword()

            Log.d(TAG, "initRegisterFetch: $confirmpwd")

            if (confirmpwd is Int) {
                pwdMsg.text = getString(confirmpwd)
            }
            if (confirmpwd is Boolean && confirmpwd) {
                pwdMsg.text = getString(R.string.pwdValid)

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
                    "https://viatorello-production.up.railway.app/api/register",
                    json
                ) { res, error ->
//            postRequest("http://10.0.2.2:3000/api/Register", json) { res, error ->
                    runOnUiThread {
                        if (error != null) {
                            Log.d(TAG, "logIn: Error $error")
//                        registerError()
                        } else {
                            registeredSuccessful(res)
                        }
                    }
                }
            } else {
                pwdMsg.text = getString(R.string.pwdInvalid)
            }
        }
    }


    private fun registeredSuccessful(res: String?) {
        Log.d(TAG, "registeredSuccessful: $res")
        saveAuth(this, res)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun confirmPasword(): Any {
        val password: EditText = findViewById(R.id.passwordInput)
        val confirmPassword: EditText = findViewById(R.id.passwordConfirmInput)
        val pwd = password.text.toString()
        val confirmPwd = confirmPassword.text.toString()

        if (pwd.isEmpty() || confirmPwd.isEmpty())
            return R.string.pwdLenght

        if (pwd.length < 8)
            return R.string.pwdLenght

        if (pwd != confirmPwd)
            return false

        if (pwd == pwd.uppercase() || pwd == pwd.lowercase())
            return R.string.pwdUpperLower

        if (!pwd.any { it.isDigit() })
            return R.string.pwdDigit

        if (!pwd.any { !it.isLetterOrDigit() })
            return R.string.pwdSpecialChar

        if (pwd.any { it.isWhitespace() })
            return R.string.pwdNoWhitespace

        if (pwd.all { it.isDigit() })
            return R.string.pwdOnlyDigits

        if (pwd.all { it.isLetter() })
            return R.string.pwdOnlyLetters

        return true
    }

    private fun isAuth() {
        val token = getAuthToken(this)

        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun newUser(): Array<String> {
        val username: EditText = findViewById(R.id.userNameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)

        return arrayOf(username.text.toString(), email.text.toString(), password.text.toString())
    }
}