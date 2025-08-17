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
import android.widget.Button
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
import com.example.vitorello.getCurrentGeoJsonPoint

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var mapView: MapView
    private var drivers = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponets()
    }

    private fun initComponets() {
        initBotMenu()
        initDestino()
        initAjustes()

        lifecycleScope.launch {
            initTaxis()
            initMap()
            initInfo()
        }
    }

    private suspend fun initInfo() {


    }

    private suspend fun initTaxis() {
        mapView = findViewById(R.id.mapa)

        val geo = getCurrentGeoJsonPoint(this@MainActivity)

        // Parse geo to get latitude and longitude (as backend expects)
        val geoJson = JSONObject(geo)
        val coordinates = geoJson.getJSONArray("coordinates")

        // Construye el JSON que espera el backend
        val json = JSONObject()
        json.put("latitude", coordinates.getDouble(0))
        json.put("longitude", coordinates.getDouble(1))

        getRequest(
            "http://10.0.2.2:3000/api/driver/nearby/${
//            "https://viatorello-production.up.railway.app/api/driver/nearby/${
                coordinates.getDouble(
                    0
                )   
            }/${
                coordinates.getDouble(
                    1
                )
            }",
            "",
        ) { res, error ->
            runOnUiThread {
                if (error != null) {
                    Log.d(TAG, "initTaxis: Error $error")
                } else if (res != null) {
                    try {
                        val coords = mutableListOf<List<Double>>()
                        // Parsear la respuesta del backend
                        val jsonResponse = JSONObject(res)
                        val driversArray = jsonResponse.getJSONArray("drivers")
                        for (i in 0 until driversArray.length()) {
                            val driverObj = driversArray.getJSONObject(i)
                            val driverCoords = driverObj.getJSONArray("driverCoordinates")
                            coords.add(listOf(driverCoords.getDouble(0), driverCoords.getDouble(1)))
                            drivers.put("driversId", driverObj.getString("driverId"))
                        }
                        addTaxis(
                            mapView,
                            resources.getDrawable(R.drawable.location_red, null),
                            coords
                        )


                    } catch (e: Exception) {
                        Log.e("initTaxis", "Error parsing response: $e")
                    }
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

    private fun initBotMenu() {
        busButton()
        desButton()
    }

    private fun desButton() {
        val despensa: Button = findViewById(R.id.bDespensa)
        despensa.setOnClickListener {
            startActivity(Intent(this, despensaActivity::class.java))
        }
    }

    private fun busButton() {
        val bus: Button = findViewById(R.id.bBus)
        bus.setOnClickListener {
            startActivity(Intent(this, busActivity::class.java))
        }
    }

    private suspend fun initMap() {
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))

        val geoJson = getCurrentGeoJsonPoint(this@MainActivity)

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