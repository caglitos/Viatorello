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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponets()
    }

    private fun initComponets() {
        initBotMenu()
        initDestino()
        initAjustes()

        lifecycleScope.launch{initMap()}

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

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        
        // Centrar en la Ubicacion Actual
        center(mapView, geoJson)

        // Punto del usuario
        createPoint(mapView, resources.getDrawable(R.drawable.location, null), geoJson)

        Log.d("MainActivity", "initMap: OSMDroid configurado exitosamente")
    }



}