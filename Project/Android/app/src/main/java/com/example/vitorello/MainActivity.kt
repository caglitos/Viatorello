package com.example.vitorello

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponets()
    }

    private fun initComponets() {
        initMap()
        initBotMenu()
        initDestino()
        val ajustes = findViewById<Button>(R.id.ivMenu)

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

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(-34.6037, -58.3816),
                10f
            )
        ) // Ejemplo: Buenos Aires
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


}

