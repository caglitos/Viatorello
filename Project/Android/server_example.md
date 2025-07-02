# 🚖 Ejemplo de API del Servidor para Taxis

## 📋 Endpoint para obtener taxis cercanos

### **POST /api/taxis/nearby**

**Request Body:**
```json
{
  "latitude": -34.6037,
  "longitude": -58.3816,
  "radius_meters": 500
}
```

**Response (Success):**
```json
{
  "success": true,
  "taxis": [
    {
      "id": "taxi_001",
      "driver_name": "Carlos González",
      "latitude": -34.6047,
      "longitude": -58.3826,
      "vehicle_type": "Sedán",
      "license_plate": "ABC-123",
      "is_available": true,
      "distance_meters": 150.0,
      "rating": 4.8,
      "estimated_arrival_minutes": 3
    },
    {
      "id": "taxi_002",
      "driver_name": "María Rodriguez",
      "latitude": -34.6027,
      "longitude": -58.3806,
      "vehicle_type": "SUV",
      "license_plate": "XYZ-789",
      "is_available": true,
      "distance_meters": 280.0,
      "rating": 4.9,
      "estimated_arrival_minutes": 5
    }
  ],
  "total_count": 2,
  "message": null
}
```

## 🛠️ Ejemplo de implementación con Node.js + Express

```javascript
// server.js
const express = require('express');
const app = express();
app.use(express.json());

// Base de datos simulada de taxis
const taxis = [
  {
    id: "taxi_001",
    driver_name: "Carlos González",
    latitude: -34.6047,
    longitude: -58.3826,
    vehicle_type: "Sedán",
    license_plate: "ABC-123",
    is_available: true,
    rating: 4.8
  },
  {
    id: "taxi_002", 
    driver_name: "María Rodriguez",
    latitude: -34.6027,
    longitude: -58.3806,
    vehicle_type: "SUV",
    license_plate: "XYZ-789",
    is_available: true,
    rating: 4.9
  },
  // ... más taxis
];

// Función para calcular distancia entre dos puntos
function calculateDistance(lat1, lon1, lat2, lon2) {
  const R = 6371000; // Radio de la Tierra en metros
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  return R * c;
}

// Endpoint para obtener taxis cercanos
app.post('/api/taxis/nearby', (req, res) => {
  const { latitude, longitude, radius_meters = 500 } = req.body;
  
  const nearbyTaxis = taxis
    .map(taxi => {
      const distance = calculateDistance(
        latitude, longitude,
        taxi.latitude, taxi.longitude
      );
      
      return {
        ...taxi,
        distance_meters: distance,
        estimated_arrival_minutes: Math.ceil(distance / 100) // Estimación simple
      };
    })
    .filter(taxi => taxi.distance_meters <= radius_meters && taxi.is_available)
    .sort((a, b) => a.distance_meters - b.distance_meters);
  
  res.json({
    success: true,
    taxis: nearbyTaxis,
    total_count: nearbyTaxis.length,
    message: null
  });
});

app.listen(3000, () => {
  console.log('Servidor corriendo en puerto 3000');
});
```

## 🔧 Para configurar tu servidor:

1. **Reemplaza la URL** en `TaxiApiFactory.kt`:
   ```kotlin
   private const val BASE_URL = "https://tu-servidor-real.com/"
   ```

2. **Estructura de base de datos sugerida** (SQL):
   ```sql
   CREATE TABLE taxis (
     id VARCHAR(50) PRIMARY KEY,
     driver_name VARCHAR(100),
     latitude DECIMAL(10, 8),
     longitude DECIMAL(11, 8),
     vehicle_type VARCHAR(50),
     license_plate VARCHAR(20),
     is_available BOOLEAN,
     rating DECIMAL(3, 2),
     last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

3. **Query para obtener taxis en radio** (ejemplo MySQL):
   ```sql
   SELECT *, 
     (6371000 * acos(cos(radians(?)) * cos(radians(latitude)) * 
     cos(radians(longitude) - radians(?)) + sin(radians(?)) * 
     sin(radians(latitude)))) AS distance_meters
   FROM taxis 
   WHERE is_available = true
   HAVING distance_meters <= ?
   ORDER BY distance_meters ASC;
   ```

## 📱 Comportamiento actual de la app:

- ✅ **Funciona sin servidor**: Muestra taxis de ejemplo automáticamente
- ✅ **Fetch cada 10 segundos**: Actualización en tiempo real
- ✅ **Manejo de errores**: Si falla la conexión, usa datos de ejemplo
- ✅ **Radio de 500 metros**: Busca solo taxis cercanos
- ✅ **Información completa**: Nombre, vehículo, distancia, rating, tiempo estimado
- ✅ **Marcadores interactivos**: Puedes tocar los taxis en el mapa

## 🎯 Próximos pasos recomendados:

1. Implementar tu servidor con el endpoint mostrado arriba
2. Actualizar la URL en `TaxiApiFactory.kt`
3. Agregar autenticación si es necesaria
4. Implementar WebSockets para actualizaciones más rápidas
5. Agregar funcionalidad para solicitar un taxi específico
