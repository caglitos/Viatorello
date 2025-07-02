package com.example.vitorello.network

import com.example.vitorello.models.TaxisResponse
import com.example.vitorello.models.UserLocation
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz API para obtener datos de taxis
 */
interface TaxiApiService {
    
    /**
     * Obtiene todos los taxis disponibles en un radio de 500 metros
     * @param location Ubicación del usuario con radio de búsqueda
     * @return Lista de taxis disponibles
     */
    @POST("api/taxis/nearby")
    suspend fun getNearbyTaxis(
        @Body location: UserLocation
    ): Response<TaxisResponse>
    
    /**
     * Obtiene un taxi específico por ID
     * @param taxiId ID del taxi
     * @return Datos del taxi
     */
    @GET("api/taxis/{id}")
    suspend fun getTaxiById(
        @Path("id") taxiId: String
    ): Response<TaxisResponse>
    
    /**
     * Actualiza la ubicación del usuario para recibir notificaciones
     * @param location Nueva ubicación del usuario
     */
    @POST("api/user/location")
    suspend fun updateUserLocation(
        @Body location: UserLocation
    ): Response<Map<String, Any>>
}

/**
 * Factory para crear instancias del servicio API
 */
object TaxiApiFactory {
    
    // URL base del servidor - REEMPLAZA CON TU URL REAL
    private const val BASE_URL = "https://tu-servidor.com/"
    
    fun createTaxiApiService(): TaxiApiService {
        return retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
                        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()
            .create(TaxiApiService::class.java)
    }
}
