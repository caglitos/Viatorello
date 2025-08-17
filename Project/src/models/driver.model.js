/*
 * Copyright 2025 Carlos Rodrigo Briseño Ruiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import mongoose from "mongoose";

const driverModel = new mongoose.Schema(
  {
    // Basic driver information
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

    // Real-time current location
    currentTrip: {
      type: {
        type: String,
        enum: ["Point"],
        default: "Point",
      },
      coordinates: {
        type: [mongoose.Schema.Types.Decimal128], // [longitude, latitude]
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
        type: [mongoose.Schema.Types.Decimal128], // [longitude, latitude]
        required: false,
      },
    },

    // Driver status
    isOnline: {
      type: Boolean,
      default: false,
    },

    // Number of the driver
    number: {
      type: String,
      required: true,
      trim: true,
    },

    // Vehicle information
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

    // Documentation and verification
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

    // Rating and statistics
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

    // Account status
    isVerified: {
      type: Boolean,
      default: false, // true cuando todos los documentos estén verificados
    },
  },
  {
    timestamps: true,
  }
);

// Geospatial index for proximity searches
driverModel.index({ currentLocation: "2dsphere" });

// Índices para consultas frecuentes
driverModel.index({ isOnline: 1, isAvailable: 1 });
driverModel.index({ "vehicle.licensePlate": 1 });

export default mongoose.model("Driver", driverModel);
