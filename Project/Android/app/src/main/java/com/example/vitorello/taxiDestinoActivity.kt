package com.example.vitorello

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class taxiDestinoActivity : AppCompatActivity() {
    
    private lateinit var mapView: MapView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_taxi_destino)

        initComponents()
    }

    private fun initEtDireccion() {
        val botonMenu = findViewById<FrameLayout>(R.id.flDireccion)

        botonMenu.setOnClickListener {
            // Infla el diseño personalizado
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.menu_text_inputs, null)

            // Obtén las referencias de los EditText
//            val calle = dialogView.findViewById<EditText>(R.id.etCalle)
//            val numero = dialogView.findViewById<EditText>(R.id.etNumero)
//            val colonia = dialogView.findViewById<EditText>(R.id.etColonia)
//            val cP = dialogView.findViewById<EditText>(R.id.etCP)
//            val ciudad = dialogView.findViewById<EditText>(R.id.etCiudad)
//            val estado = dialogView.findViewById<EditText>(R.id.etEstado)
//            val pais = dialogView.findViewById<EditText>(R.id.etPais)

            // Crea y muestra el diálogo
            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Dirección")
                .setView(dialogView)
                .setPositiveButton("Aceptar") { _, _ ->


                    Intent(this, PagoActivity::class.java)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


    }

    private fun initComponents() {
        initMap()
        initEtDireccion()

        val pagar = findViewById<Button>(R.id.bPay)
        pagar.setOnClickListener{
            startActivity(Intent(this, PagoActivity::class.java))
        }
    }

    private fun initMap() {
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
    }
    
    override fun onResume() {
        super.onResume()
        if (::mapView.isInitialized) {
            mapView.onResume()
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (::mapView.isInitialized) {
            mapView.onPause()
        }
    }
}