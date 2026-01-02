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
        pets: {
            type: Boolean,
            default: false,
        },

        // Number of the driver
        number: {
            type: String,

            unique: true,
            trim: true,
        },

        // Vehicle information
        vehicle: {
            brand: {
                type: String,
                default: "Chevrolet",

            },
            model: {
                type: String,
                model: "Corvette",

            },
            year: {
                type: Number,

            },
            licensePlate: {
                type: String,
                unique: true,
                sparse: true,
                default: null,
                trim: true,
            },
            capacity: {
                type: Number,
                default: 4,
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


    },
    {
        timestamps: true,
    }
);
export default mongoose.model("DriverData", driverModel);
