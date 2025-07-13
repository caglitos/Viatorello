import mongoose from "mongoose";

const userSchema = new mongoose.Schema(
  {
    username: {
      type: String,
      required: true,
      trim: true,
    },
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
    // Estado de conexión del usuario
    isOnline: {
      type: Boolean,
      default: false,
    },
    lastLocationUpdate: {
      type: Date,
      default: null,
    },
    // Información del viaje actual (si está en uno)
    currentTrip: {
      type: Boolean,
      ref: "Trip",
      default: null,
    },
  },
  {
    timestamps: true,
  }
);

// Crear índice geoespacial para consultas de ubicación eficientes
userSchema.index({ currentLocation: "2dsphere" });

export default mongoose.model("User", userSchema);
