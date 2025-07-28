import mongoose from "mongoose";

const driverSchema = new mongoose.Schema(
  {
    // Información básica del conductor
    fullName: {
      type: String,
      required: true,
      trim: true,
    },
    //phone
    email: {
      type: String,
      required: true,
      unique: true,
      trim: true,
    },
    password: {
      type: String,
      required: true,
    },

    // Ubicación actual en tiempo real
    currentTrip: {
      type: {
        type: String,
        enum: ["Point"],
        default: "Point",
      },
      coordinates: {
        type: [Number], // [longitude, latitude]
        required: true,
      },
    },
    currentLocation: {
      type: {
        type: String,
        enum: ["Point"],
        default: "Point",
      },
      coordinates: {
        type: [Number], // [longitude, latitude]
        required: false,
      },
    },

    // Estado del conductor
    isOnline: {
      type: Boolean,
      default: false,
    },

    // Información del vehículo
    vehicle: {
      brand: {
        type: String,
        default: "Chevrolet",
        required: true,
      },
      model: {
        type: String,
        model: "Corvette",
        required: true,
      },
      year: {
        type: Number,
        required: true,
      },
      licensePlate: {
        type: String,
        required: true,
      },
      capacity: {
        type: Number,
        default: 4,
        required: true, // número de pasajeros
      },
    },

    // Documentación y verificación
    documents: {
      driverLicense: {
        number: String,
        expiryDate: Date,
        isVerified: {
          type: Boolean,
          default: false,
        },
      },
      vehicleRegistration: {
        number: String,
        expiryDate: Date,
        isVerified: {
          type: Boolean,
          default: false,
        },
      },
      insurance: {
        number: String,
        expiryDate: Date,
        isVerified: {
          type: Boolean,
          default: false,
        },
      },
    },

    // Rating y estadísticas
    rating: {
      average: {
        type: Number,
        min: 0,
        max: 5,
        default: 5,
      },
      totalRatings: {
        type: Number,
        default: 0,
      },
    },
    totalTrips: {
      type: Number,
      default: 0,
    },
    totalEarnings: {
      type: Number,
      default: 0,
    },

    // Estado de cuenta
    isVerified: {
      type: Boolean,
      default: false, // true cuando todos los documentos estén verificados
    },

    // Foto del conductor
    photo: {
      data: Buffer,
      contentType: String,
    },
  },
  {
    timestamps: true,
  }
);

// Índice geoespacial para búsquedas por proximidad
driverSchema.index({ currentLocation: "2dsphere" });

// Índices para consultas frecuentes
driverSchema.index({ isOnline: 1, isAvailable: 1 });
driverSchema.index({ "vehicle.licensePlate": 1 });

export default mongoose.model("Driver", driverSchema);
