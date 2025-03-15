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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class taxiDestinoActivity : AppCompatActivity(), OnMapReadyCallback {
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
            val calle = dialogView.findViewById<EditText>(R.id.etCalle)
            val numero = dialogView.findViewById<EditText>(R.id.etNumero)
            val colonia = dialogView.findViewById<EditText>(R.id.etColonia)
            val cP = dialogView.findViewById<EditText>(R.id.etCP)
            val ciudad = dialogView.findViewById<EditText>(R.id.etCiudad)
            val estado = dialogView.findViewById<EditText>(R.id.etEstado)
            val pais = dialogView.findViewById<EditText>(R.id.etPais)

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

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-34.6037, -58.3816), 10f)) // Ejemplo: Buenos Aires
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}