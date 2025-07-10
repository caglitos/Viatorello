package com.example.vitorello

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log

import com.google.android.gms.location.LocationServices

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun postRequest(url: String, jsonBody: String, callback: (String?, Exception?) -> Unit) {
    val client = OkHttpClient()

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(null, e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    callback(null, IOException("Unexpected code $response"))
                } else {
                    callback(response.body?.string(), null)
                }
            }
        }
    })
}

fun getRequest(url: String, callback: (String?, Exception?) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(null, e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    callback(null, IOException("Unexpected code $response"))
                } else {
                    callback(response.body?.string(), null)
                }
            }
        }
    })
}

fun putRequest(url: String, jsonBody: String, callback: (String?, Exception?) -> Unit) {
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val requestBody = jsonBody.toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url)
        .put(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(null, e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    callback(null, IOException("Unexpected code $response"))
                } else {
                    callback(response.body?.string(), null)
                }
            }
        }
    })
}

fun deleteRequest(url: String, callback: (String?, Exception?) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .delete()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(null, e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    callback(null, IOException("Unexpected code $response"))
                } else {
                    callback(response.body?.string(), null)
                }
            }
        }
    })
}

fun isoDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(Date())
}

@SuppressLint("MissingPermission")
suspend fun getCurrentLocationAsGeoJsonPoint(context: Context): String = suspendCoroutine { cont ->
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            val point = """
                {
                    "type": "Point",
                    "coordinates": [
                        ${location.longitude}, 
                        ${location.latitude}
                    ]
                }
            """.trimIndent()
            cont.resume(point)
        } else {
            cont.resume(
                """
                {
                    "type": "Point",
                    "coordinates": [
                        0, 0
                    ]
                }
            """.trimMargin()
            )
        }
    }.addOnFailureListener {
        cont.resume(
            """
            {
                "type": "Point",
                "coordinates": [
                    0, 0
                ]
            }
        """.trimIndent()
        )
    }
}

// Guardar token
fun saveAuthToken(context: Context, res: String?) {
    try {
        val json = JSONObject(res ?: "")
        val token = json.optString("token", null)
        if (token != null) {
            val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

            with(sharedPref.edit()) {
                putString("auth_token", token)
                putBoolean("is_logged_in", true)
                apply()
            }

            Log.d("LogIn", "LogInSuccessful: $token")
        } else {
            Log.e("LogIn", "Token not found in response")
        }
    } catch (e: Exception) {
        Log.e("LogIn", "Error parsing token from response: ${e.message}")
    }
}

// Obtener token
fun getAuthToken(context: Context): String? {
    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("auth_token", null)
}

// Verificar si está logueado
fun isLoggedIn(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return sharedPref.getBoolean("is_logged_in", false)
}

// Logout (limpiar datos)
fun logout(context: Context) {
    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        remove("auth_token")
        putBoolean("is_logged_in", false)
        apply()
    }
}