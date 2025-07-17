package com.example.vitorello

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )

        setContentView(R.layout.activity_register)

        initComponent()

    }

    fun initComponent() {
//        isAuth()
        initLoginText()
        formatHtml()

        lifecycleScope.launch {
            initRegisterFetch()
        }
    }

    private fun initLoginText() {
        val logInText: TextView = findViewById(R.id.LogIN)

        logInText.setOnClickListener {
            Log.d(TAG, "Navigating to SignInActivity")
            startActivity(Intent(this, signInActivity::class.java))
        }
    }

    private fun formatHtml() {
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.text =
            Html.fromHtml(getString(R.string.forgotPassword), Html.FROM_HTML_MODE_LEGACY)

        val logIn = findViewById<TextView>(R.id.LogIN)
        logIn.text = Html.fromHtml(getString(R.string.LogIn), Html.FROM_HTML_MODE_LEGACY)
    }

    private suspend fun initRegisterFetch() {
        val pwdMsg = findViewById<TextView>(R.id.passwordMsg)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val geoJson = getCurrentLocationAsGeoJsonPoint(this@RegisterActivity)

        registerBtn.setOnClickListener {
            val confirmpwd = confirmPasword()

            if (confirmpwd is Int)
                pwdMsg.text = getString(confirmpwd)


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

    private fun registerError(res: String?) {
        Log.d(TAG, "registerError: Error during registration")
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