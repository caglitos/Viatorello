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
import com.example.vitorello.models.TaxiData
import com.example.vitorello.models.UserLocation
import com.example.vitorello.network.TaxiApiFactory

class MainActivity : AppCompatActivity() {
    
    private lateinit var mapView: MapView
    private val taxiApiService = TaxiApiFactory.createTaxiApiService()
    private val taxiMarkers = mutableListOf<Marker>()
    
    // Ubicación del usuario (Buenos Aires por defecto)
    private var userLocation = UserLocation(
        latitude = -34.6037,
        longitude = -58.3816,
        radiusMeters = 500
    )
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
        startTaxiFetching()
        
        Log.d("MainActivity", "initMap: OSMDroid configurado exitosamente")
    }
    
    private fun addTaxiMarker(location: GeoPoint) {
        val marker = Marker(mapView)
        marker.position = location
        marker.title = "Taxi Disponible"
        marker.snippet = "Driver: Juan Pérez"
        
        // Aquí puedes personalizar el ícono del marcador
        // marker.icon = ContextCompat.getDrawable(this, R.drawable.taxi_icon)
        
        mapView.overlays.add(marker)
        mapView.invalidate()
        
        Log.d("MainActivity", "addTaxiMarker: Marcador de taxi agregado")
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
    
    // ============= FUNCIONES PARA FETCH DE TAXIS EN TIEMPO REAL =============
    
    /**
     * Inicia el fetch automático de taxis cada 10 segundos
     */
    private fun startTaxiFetching() {
        Log.d("MainActivity", "startTaxiFetching: Iniciando fetch automático de taxis")
        
        lifecycleScope.launch {
            while (true) {
                fetchNearbyTaxis()
                delay(10000) // Actualizar cada 10 segundos
            }
        }
    }
    
    /**
     * Obtiene los taxis cercanos desde la API
     */
    private suspend fun fetchNearbyTaxis() {
        try {
            Log.d("MainActivity", "fetchNearbyTaxis: Buscando taxis en radio de 500m...")
            
            val response = taxiApiService.getNearbyTaxis(userLocation)
            
            if (response.isSuccessful) {
                val taxisResponse = response.body()
                if (taxisResponse?.success == true) {
                    Log.d("MainActivity", "fetchNearbyTaxis: ${taxisResponse.taxis.size} taxis encontrados")
                    updateTaxisOnMap(taxisResponse.taxis)
                } else {
                    Log.w("MainActivity", "fetchNearbyTaxis: Error del servidor: ${taxisResponse?.message}")
                    // Mostrar datos de ejemplo si falla el servidor
                    showExampleTaxis()
                }
            } else {
                Log.e("MainActivity", "fetchNearbyTaxis: Error HTTP ${response.code()}")
                // Mostrar datos de ejemplo si falla la conexión
                showExampleTaxis()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "fetchNearbyTaxis: Error de conexión: ${e.message}")
            // Mostrar datos de ejemplo si no hay conexión
            showExampleTaxis()
        }
    }
    
    /**
     * Actualiza los marcadores de taxis en el mapa
     */
    private fun updateTaxisOnMap(taxis: List<TaxiData>) {
        // Limpiar marcadores anteriores
        clearTaxiMarkers()
        
        // Agregar nuevos marcadores
        taxis.forEach { taxi ->
            if (taxi.isAvailable) {
                addRealTaxiMarker(taxi)
            }
        }
        
        mapView.invalidate()
        Log.d("MainActivity", "updateTaxisOnMap: ${taxis.size} marcadores actualizados")
    }
    
    /**
     * Agrega un marcador real de taxi al mapa
     */
    private fun addRealTaxiMarker(taxi: TaxiData) {
        val marker = Marker(mapView)
        marker.position = GeoPoint(taxi.latitude, taxi.longitude)
        marker.title = "${taxi.driverName} - ${taxi.vehicleType}"
        marker.snippet = "${taxi.licensePlate} • ${taxi.distanceMeters.toInt()}m • ⭐${taxi.rating} • ${taxi.estimatedArrivalMinutes}min"
        
        // Personalizar ícono según disponibilidad
        // marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_taxi_available)
        
        // Evento al tocar el marcador
        marker.setOnMarkerClickListener { clickedMarker, _ ->
            Log.d("MainActivity", "Taxi seleccionado: ${taxi.driverName}")
            // Aquí puedes agregar lógica para solicitar el taxi
            false
        }
        
        taxiMarkers.add(marker)
        mapView.overlays.add(marker)
    }
    
    /**
     * Limpia todos los marcadores de taxis del mapa
     */
    private fun clearTaxiMarkers() {
        taxiMarkers.forEach { marker ->
            mapView.overlays.remove(marker)
        }
        taxiMarkers.clear()
    }
    
    /**
     * Muestra taxis de ejemplo cuando no hay conexión a la API
     */
    private fun showExampleTaxis() {
        Log.d("MainActivity", "showExampleTaxis: Mostrando datos de ejemplo")
        
        val exampleTaxis = listOf(
            TaxiData(
                id = "taxi_001",
                driverName = "Carlos González",
                latitude = -34.6047,
                longitude = -58.3826,
                vehicleType = "Sedán",
                licensePlate = "ABC-123",
                isAvailable = true,
                distanceMeters = 150.0,
                rating = 4.8f,
                estimatedArrivalMinutes = 3
            ),
            TaxiData(
                id = "taxi_002",
                driverName = "María Rodriguez",
                latitude = -34.6027,
                longitude = -58.3806,
                vehicleType = "SUV",
                licensePlate = "XYZ-789",
                isAvailable = true,
                distanceMeters = 280.0,
                rating = 4.9f,
                estimatedArrivalMinutes = 5
            ),
            TaxiData(
                id = "taxi_003",
                driverName = "Juan Pérez",
                latitude = -34.6057,
                longitude = -58.3836,
                vehicleType = "Compacto",
                licensePlate = "DEF-456",
                isAvailable = true,
                distanceMeters = 420.0,
                rating = 4.6f,
                estimatedArrivalMinutes = 7
            )
        )
        
        updateTaxisOnMap(exampleTaxis)
    }
}

