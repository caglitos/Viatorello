import mongoose from "mongoose";

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
userSchema.index({currentLocation: "2dsphere"});

export default mongoose.model("User", userSchema);
