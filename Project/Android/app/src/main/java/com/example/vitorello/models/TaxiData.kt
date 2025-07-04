package com.example.vitorello.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para un taxi
 */
data class TaxiData(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("driver_name")
    val driverName: String,
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("vehicle_type")
    val vehicleType: String,
    
    @SerializedName("license_plate")
    val licensePlate: String,
    
    @SerializedName("is_available")
    val isAvailable: Boolean,
    
    @SerializedName("distance_meters")
    val distanceMeters: Double,
    
    @SerializedName("rating")
    val rating: Float = 0.0f,
    
    @SerializedName("estimated_arrival_minutes")
    val estimatedArrivalMinutes: Int = 0
)

/**
 * Ubicación del usuario para enviar al servidor
 */
data class UserLocation(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("radius_meters")
    val radiusMeters: Int = 500
)

/**
 * Respuesta del servidor con los taxis cercanos
 */
data class TaxisResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("taxis")
    val taxis: List<TaxiData>,
    
    @SerializedName("total_count")
    val totalCount: Int,
    
    @SerializedName("message")
    val message: String?
)
