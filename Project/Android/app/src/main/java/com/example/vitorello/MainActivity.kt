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
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {
    
    private lateinit var mapView: MapView
    private val taxiMarkers = mutableListOf<Marker>()
    
    // Ubicación del usuario (Buenos Aires por defecto)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponets()
    }

    private fun initComponets() {
        initMap()
        initBotMenu()
        initDestino()
        val ajustes = findViewById<AppCompatImageView>(R.id.ivMenu)

        ajustes.setOnClickListener{
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

    private fun initMap() {
        Log.d("MainActivity", "initMap: Inicializando OSMDroid...")
        
        // Configurar OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))
        
        // Obtener referencia al MapView
        mapView = findViewById(R.id.mapa)
        
        // Configurar el mapa
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        
        // Centrar en Buenos Aires
        val buenosAires = GeoPoint(-34.6037, -58.3816)
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(buenosAires)
        
        // Iniciar fetch de taxis en tiempo real


        Log.d("MainActivity", "initMap: OSMDroid configurado exitosamente")
    }
}