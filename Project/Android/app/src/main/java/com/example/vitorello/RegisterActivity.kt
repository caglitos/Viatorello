package com.example.vitorello

import android.content.Intent
import android.os.Bundle
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
        togglePwdVisibility()

        lifecycleScope.launch {
            initRegisterFetch()
        }
    }

    // Initialize the text for navigating to the login screen
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
        val geoJson = getCurrentGeoJsonPoint(this@RegisterActivity)

        registerBtn.setOnClickListener {
            val confirmpwd = confirmPasword()

            if (confirmpwd is Int)
                pwdMsg.text = getString(confirmpwd)

            if (confirmpwd is Boolean && confirmpwd) {
                pwdMsg.text = getString(R.string.pwdValid)

                val json = """{${newUser()},
                        "currentLocation": $geoJson,
                        "isOnline": true,
                        "lastLocationUpdate": "${isoDate()}",
                        "currentTrip": false} """.trimIndent()

                postRequest(
                    "https://viatorello-production.up.railway.app/api/auth/register",
                    json
                ) { res, error ->
//            postRequest("http://10.0.2.2:3000/api/Register", json) { res, error ->
                    runOnUiThread {
                        if (error != null) {
                            Log.d(TAG, "logIn: Error $error")
                            registerError()
                        } else {
                            registeredSuccessful(res)
                        }
                    }
                }
            } else if (confirmpwd is Boolean) {
                pwdMsg.text = getString(R.string.pwdInvalid)
            }

        }
    }

    // Show a success message when registration is successful2w
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

    // Show an error message when registration fails
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

    // Toggle password visibility
    private fun togglePwdVisibility() {
        val pwdVisibilityToggle: ImageButton = findViewById(R.id.pwdVisibility)
        val password: EditText = findViewById(R.id.passwordInput)
        val confirmPassword: EditText = findViewById(R.id.passwordConfirmInput)

        pwdVisibilityToggle.setOnClickListener {
            if (password.inputType == 129) {
                password.inputType = 144
                confirmPassword.inputType = 144
                pwdVisibilityToggle.setImageResource(R.drawable.pwdtoggle1)
            } else {
                password.inputType = 129
                confirmPassword.inputType = 129
                pwdVisibilityToggle.setImageResource(R.drawable.pwdtoggle0)
            }
            password.setSelection(password.text.length)
            confirmPassword.setSelection(confirmPassword.text.length)
        }
    }

    // Validate the password and confirm password fields
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

    // Check if the user is already authenticated
    private fun isAuth() {
        val token = getAuthToken(this)

        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun newUser(): String {
        val username: EditText = findViewById(R.id.userNameInput)
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)

        return """
            "username": "${username.text}",
            "email": "${email.text}",
            "password": "${password.text}"
        """.trimIndent()
    }
}