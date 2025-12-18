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
import kotlin.reflect.typeOf

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var mapView: MapView
    private val context: Context = this@MainActivity
    private var driversIDs = mutableListOf<String>()
    private var drivers = JSONObject()
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
            initTaxiIDs()
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            lifecycleScope.launch {
                initTaxis()
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

        val url = "http://10.0.2.2:3000/api/driver/nearby/${
            coordinates.getDouble(0)
        }/${coordinates.getDouble(1)}"

//        val url = "https://viatorello-production.up.railway.app/api/driver/nearby/${
//            coordinates.getDouble(0)
//        }/${coordinates.getDouble(1)}"

        getRequest( url, "") { res, error ->
            runOnUiThread {
                try {
                    val coords = mutableListOf<List<Double>>()
                    // Parsear la respuesta del backend
                    val jsonResponse = JSONObject(res)
                    val driversArray = jsonResponse.getJSONArray("drivers")
                    Log.d(TAG, "initTaxis: driversArray $driversArray")
                    for (i in 0 until driversArray.length()) {
                        val driverObj = driversArray.getJSONObject(i)
                        val driverCoords = driverObj.getJSONArray("driverCoordinates")
                        coords.add(
                            listOf(
                                driverCoords.getString(0).toDouble(),
                                driverCoords.getString(1).toDouble()
                            )
                        )
                    }

                    removeAllMarkers(mapView)

                    addTaxis(
                        mapView,
                        resources.getDrawable(R.drawable.location_red, null),
                        coords
                    )
                } catch (e: Exception) {
                    Log.e("initTaxis", "Error: $e")
                }
            }
        }
    }

    private suspend fun initTaxiIDs() {
        val geo = getCurrentGeoJsonPoint(context)

        val geoJSON = JSONObject(geo)
        val coordinates = geoJSON.getJSONArray("coordinates")

        val url = "http://10.0.2.2:3000/api/driver/nearby/" +
                "${coordinates.getDouble(0)}/" +
                "${coordinates.getDouble(1)}"
//        val url = "https://viatorello-production.up.railway.app/api/driver/nearby/" +
//                "${coordinates.getDouble(0)}/" +
//                "${coordinates.getDouble(1)}"

        getRequest(url, "") { res, error ->
            if (error != null) {
                Log.d(TAG, "Error: ${error.message}")

            } else if (res.isNullOrEmpty()) {
                Log.e(TAG, "Respuesta vacía del backend")
            } else {
                Log.d(TAG, "Tipo de res: ${(res!!)::class.java.name}")
                val jsonResponse = JSONObject(res)
                val jsonDrivers = jsonResponse.getJSONArray("drivers")
                val driverIds = mutableListOf<String>()
                for (i in 0 until jsonDrivers.length()) {
                    val driver = jsonDrivers.getJSONObject(i)
                    val driverId = driver.getString("driversId")
                    println("Driver ID: $driverId")
                    driverIds.add(driverId)
                }
                getTaxiInfoById(driverIds)
            }
        }

    }

    fun getTaxiInfoById(driverIds: List<String>) {

        for (driverId in driverIds) {
            getRequest(
                "http://localhost:3000/api/driver/profile/${driverId}", ""
            ) { response, exception ->
                if (exception != null) {
                    println("Error: ${exception.message}")
                } else {
                    println("Driver Name: ${response.toString()}")
                }
            }
        }
    }

    private fun initAjustes() {
        val ajustes = findViewById<AppCompatImageView>(R.id.ivMenu)

        ajustes.setOnClickListener {
            startActivity(Intent(this, TerminosActivity::class.java))
        }
    }

    private fun initDestino() {
        val cargarDestino: ConstraintLayout = findViewById(R.id.clChofer)

        cargarDestino.setOnClickListener {
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

