package com.example.vitorello

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.core.content.edit

// Realizar una solicitudes HTTP
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

fun getRequest(url: String, jsonBody: String, callback: (String?, Exception?) -> Unit) {
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val requestBody = jsonBody.toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url)
        .method("GET", requestBody)
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

// Obtener la fecha y hora actual en formato ISO 8601
fun isoDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(Date())
}

// Obtener la localizacion actual como un punto GeoJSON
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
fun saveAuth(context: Context, res: String?) {
    try {
        val json = JSONObject(res ?: "")
        val token = json.optString("token", null)
        val userId = json.optString("id", null)
        if (token != null) {
            val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

            sharedPref.edit{
                putString("auth_token", token)
                putString("userId", userId)
                putBoolean("is_logged_in", true)
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

// Obtener userId
fun getUserId(context: Context): String? {
    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return sharedPref.getString("userId", null)
}

// Logout (limpiar datos)
fun logout(context: Context) {
    val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    sharedPref.edit{
        remove("auth_token")
        putBoolean("is_logged_in", false)
        apply()
    }
}

// Centrar el mapa en una ubicación específica
fun center(map: MapView, coordinates: String) {
    val json = JSONObject(coordinates)
    val coordinates = json.getJSONArray("coordinates")
    val currentLocation = GeoPoint(coordinates.getDouble(1), coordinates.getDouble(0))
    map.controller.setZoom(18.0)
    map.controller.setCenter(currentLocation)
}

// Crear un punto en el mapa con un icono y coordenadas específicas
fun createPoint(map: MapView, icon: Drawable, coordinates: String) {
    val json = JSONObject(coordinates)
    val coordinates = json.getJSONArray("coordinates")
    val currentLocation = GeoPoint(coordinates.getDouble(1), coordinates.getDouble(0))

    val userMarker = Marker(map)
    userMarker.position = currentLocation
    userMarker.icon = icon
    userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

    map.overlays.add(userMarker)
}