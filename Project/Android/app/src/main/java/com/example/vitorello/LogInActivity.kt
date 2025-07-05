package com.example.vitorello

import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        forgotPassword.text = Html.fromHtml(getString(R.string.forgotPassword), Html.FROM_HTML_MODE_LEGACY)
    }
}