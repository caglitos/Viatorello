/*
 * Copyright 2025 Carlos Rodrigo Briseño Ruiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.vitorello

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import android.os.Handler
import android.content.Context
import android.widget.TextView
import kotlin.math.log
import kotlin.text.isNullOrEmpty

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var loading: Boolean = true
    private lateinit var mapView: MapView
    private lateinit var nameTV: TextView
    private lateinit var timeTV: TextView
    private lateinit var carTV: TextView
    private lateinit var rateTV: TextView
    private lateinit var petsTV: TextView
    private val data = mutableListOf<List<String>>()
    private val context: Context = this@MainActivity
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponets()
    }

    private fun initComponets() {
//        initBotMenu()
        initDestino()
        initAjustes()

        lifecycleScope.launch {
            initMap()
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            lifecycleScope.launch {
                initTaxis()
                loadInfo(data)
            }
            // execute every second
            handler.postDelayed(this, 5000)
        }
    }

    // Para iniciar el bucle
    override fun onStart() {
        super.onStart()
        handler.post(updateRunnable)
    }

    // Para detener el bucle (importante para evitar leaks)
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateRunnable)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private suspend fun initTaxis() {
        mapView = findViewById(R.id.mapa)

        val geo = getCurrentGeoJsonPoint(context)

        // Parse geo to get latitude and longitude (as backend expects)
        val geoJson = JSONObject(geo)
        val coordinates = geoJson.getJSONArray("coordinates")

        // Construye el JSON que espera el backend
        val json = JSONObject()
        json.put("latitude", coordinates.getDouble(0))
        json.put("longitude", coordinates.getDouble(1))

        val url = "$BASE_URL/driver/nearby/${
            coordinates.getDouble(0)
        }/${coordinates.getDouble(1)}"

        getRequest(url, "") { res, error ->
            if (error != null) {
                Log.d(TAG, "Error: ${error.message}")

                return@getRequest
            }
            if (res.isNullOrEmpty()) {
                Log.e(TAG, "Respuesta vacía del backend")

                return@getRequest
            }

            runOnUiThread {

                try {
                    val cords = mutableListOf<List<Double>>()
                    // Transformar la respuesta del backend
                    val jsonResponse = JSONObject(res)

                    val driversArray = jsonResponse.getJSONArray("drivers")

                    Log.d(TAG, "initTaxis: driversArray $driversArray")

                    for (i in 0 until driversArray.length()) {
                        val driverObj = driversArray.getJSONObject(i)

                        val driverCoords = driverObj.getJSONArray("driverCoordinates")

                        cords.add(
                            listOf(
                                driverCoords.getString(0).toDouble(),
                                driverCoords.getString(1).toDouble()
                            )
                        )

                    }

                    removeAllMarkers(mapView)

                    addTaxis(
                        mapView, resources.getDrawable(R.drawable.location_red, null), cords
                    )

                } catch (e: Exception) {
                    Log.e("initTaxis", "Error: $e")

                    return@runOnUiThread
                }

                val jsonResponse = JSONObject(res)
                val jsonDrivers = jsonResponse.getJSONArray("drivers")
                val driverIds = mutableListOf<String>()

                for (i in 0 until jsonDrivers.length()) {
                    val driver = jsonDrivers.getJSONObject(i)
                    val driverId = driver.getString("driversId")
                    driverIds.add(driverId)
                }

                lifecycleScope.launch {
                    getTaxiInfoById(driverIds)
                }
            }
        }
    }

    suspend fun getTaxiInfoById(driverIds: List<String>) {
        val url = "$BASE_URL/driver/public-profile/"
        var abort = false
        var time: String

        val geo = getCurrentGeoJsonPoint(context)

        // Parse geo to get latitude and longitude (as backend expects)
        val geoJson = JSONObject(geo)
        val coordinates = geoJson.getJSONArray("coordinates")

        val lon1 = coordinates.getDouble(0)
        val lat1 = coordinates.getDouble(1)

        for (driverId in driverIds) {
            if (abort) break
            val indexDriver = mutableListOf<String>()

            getRequest(url + driverId, "") { res, error ->
                if (error != null) {
                    Log.d(TAG, "Error: ${error.message}")
                    abort = true
                    return@getRequest
                }
                if (res.isNullOrEmpty()) {
                    Log.e(TAG, "Respuesta vacía del backend")
                    abort = true
                    return@getRequest
                }

                val response = JSONObject(res)
                val jsonRes = response.getJSONObject("driverFound")

                indexDriver.add(0, jsonRes.getString("name"))

                if (jsonRes.getBoolean("pets")) {
                    indexDriver.add(1, "✓")
                } else {
                    indexDriver.add(1, "✗")
                }
                indexDriver.add(2, jsonRes.getJSONObject("vehicle").getString("model"))
                indexDriver.add(3, jsonRes.getJSONObject("rating").getDouble("average").toString())

                val lon2 = response.getJSONObject("currentLocation").getJSONArray("coordinates")
                    .getString(0)
                val lat2 = response.getJSONObject("currentLocation").getJSONArray("coordinates")
                    .getString(1)

//                val timeUrl = "$BASE_URL/time/getDistanceTime/$lon1/$lat1/$lon2/$lat2"
                val timeUrl =
                    "$BASE_URL/time/getDistanceTime/90.060901/18.510901/97.060099/18.510099" //test

                Log.d(TAG, timeUrl)

                getRequest(timeUrl) { resTime, errorTime ->
                    if (errorTime != null) {
                        Log.e(TAG, "Error: ${errorTime}")
                        return@getRequest
                    }
                    if (resTime.isNullOrEmpty()) {
                        Log.e(TAG, "Response null or empty")
                        return@getRequest
                    }

                    val jsonResTime = JSONObject(resTime)

                    time = jsonResTime.getString("time")

                    indexDriver.add(4, time)

                    data.add(indexDriver)

                    if (data.size == driverIds.size)
                        loading = false

                }


            }


        }

    }

    /*
     * Ejemplo de la estructura de la lista data.
     *
     * [
     *      [
     *          "carlos"   → Nombre,
     *          "✓"        → Mascotas,
     *          "Corvette" → Modelo del auto,
     *          "*****"    → Calificación,
     *          "5:10"     → Tiempo,
     *      ],
     *      [
     *          "Jan"      → Nombre,
     *          "✗"        → Mascotas,
     *          "Camaro"   → Modelo del auto,
     *          "**"       → Calificación,
     *          "5:10"     → Tiempo,
     *      ]
     * ]
     *
     */


    private fun loadInfo(data: List<List<String>>) {
        if (loading) return

        nameTV = findViewById(R.id.tvChofer)
        timeTV = findViewById(R.id.tvTiempo)
        carTV  = findViewById(R.id.tvCar)
        rateTV = findViewById(R.id.tvRate)
        petsTV = findViewById(R.id.tvPets)
0

        nameTV.text = data[0][0]
//        petsTV.text = "${R.string.pets} ${data[0][1]}"
        petsTV.text = "mascotas: ${data[0][1]}"
        carTV.text = data[0][2]
        rateTV.text = data[0][3]
        timeTV.text = data[0][4]

        loading = true
    }

    private fun initAjustes() {
        val ajustes = findViewById<AppCompatImageView>(R.id.ivMenu)

        ajustes.setOnClickListener {
            startActivity(Intent(this, TerminosActivity::class.java))
        }
    }

    private fun initDestino() {
        val chofer: ConstraintLayout = findViewById(R.id.clChofer)

        chofer.setOnClickListener {
            startActivity(Intent(this, taxiDestinoActivity::class.java))
        }
    }

//    private fun initBotMenu() {
//        busButton()
//        desButton()
//    }

//    private fun desButton() {
//        val despensa: Button = findViewById(R.id.bDespensa)
//        despensa.setOnClickListener {
//            startActivity(Intent(this, despensaActivity::class.java))
//        }
//    }
//
//    private fun busButton() {
//        val bus: Button = findViewById(R.id.bBus)
//        bus.setOnClickListener {
//            startActivity(Intent(this, busActivity::class.java))
//        }
//    }

    private suspend fun initMap() {
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))

        val geoJson = getCurrentGeoJsonPoint(context)

        mapView = findViewById(R.id.mapa)
        Log.d(TAG, "initComponets: Iniciando componentes de la actividad principal")

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Centrar en la Ubicacion Actual
        center(mapView, geoJson)

        // Punto del usuario
        createPoint(mapView, resources.getDrawable(R.drawable.location, null), geoJson)

        Log.d("MainActivity", "initMap: OSMDroid configurado exitosamente")
    }

}

