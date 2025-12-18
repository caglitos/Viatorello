package com.example.vitorello

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.junit.Test

class DriverApiTest {

    private val client = OkHttpClient()

    @Test
    fun testGetDriverProfile() {
        val url = "http://localhost:3000/api/driver/profile/68a2693c53fa2e2faab64694"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            assert(response.isSuccessful) { "Request failed: ${response.code}" }
            val body = response.body?.string()
            println("Respuesta: $body")

            val json = JSONObject(body)
            println("Driver ID: ${json.optString("_id")}")
        }
    }
}
