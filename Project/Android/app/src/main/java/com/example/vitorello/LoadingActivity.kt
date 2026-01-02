package com.example.vitorello

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {
    private var TAG = "Loading Activity"
    private val profileURL = "$BASE_URL/auth/profile"
    private val updateCoordsURL = "$BASE_URL/auth/update-coords"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()

    }

    private fun initComponents() {
        isAuth()
        lifecycleScope.launch {
            updateCords()
        }

    }

    private fun isAuth() {
        val token = getAuthToken(this) ?: return startActivity(Intent(this, RegisterActivity::class.java))
        getRequest(profileURL, "", token) { res, error ->
            if (error != null || res.isNullOrEmpty()) {
                startActivity(Intent(this, RegisterActivity::class.java))

                return@getRequest
            }

            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private suspend fun updateCords() {
        val token = getAuthToken(this) ?: return
        val cords = getCurrentGeoJsonPoint(this)
        val body = """ { "currentLocation": $cords } """.trimIndent()

        putRequest(updateCoordsURL, body, token) { res, error ->
            if (error != null || res.isNullOrEmpty()) {
                Log.e(TAG, "Error updating coordinates: $error")
                return@putRequest
            }
        }

    }

}