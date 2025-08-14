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

import {z} from "zod";

const geoPointSchema = z.object({
    type: z.literal('Point'),
    coordinates: z.array(z.number()).length(2).optional(), // [longitude, latitude]
});

const vehicleSchema = z.object({
    brand: z.string(),
    model: z.string(),
    year: z.number(),
    licensePlate: z.string(),
    capacity: z.number().default(4),
}).partial();

const documentVerificationSchema = z.object({
    number: z.string().optional(),
    expiryDate: z.date().optional(),
    isVerified: z.boolean().default(false),
});

const documentsSchema = z.object({
    driverLicense: documentVerificationSchema,
    vehicleRegistration: documentVerificationSchema,
    insurance: documentVerificationSchema,
});

export const driverSchema = z.object({
    fullName: z.string().trim(),
    email: z.string({
        required_error: "Email is required",
    })
        .email({
            message: "Invalid email",
        }).trim(),
    password: z
        .string({
            required_error: "Password is required",
        })
        .min(4, {
            message: "Password must be at least 8 characters long",
        }),
    currentTrip: geoPointSchema.optional().default({
        type: "Point",
        coordinates: [0, 0],
    }),
    currentLocation: geoPointSchema.optional().default({
        type: "Point",
        coordinates: [0, 0],
    }),

    isOnline: z.boolean().default(false),

    vehicle: vehicleSchema.optional(),

    documents: documentsSchema.optional()
});

export const loginSchema = z.object({
    email: z
        .string({
            required_error: "Email is required",
        })
        .email({
            message: "Invalid email",
        }).trim(),
    password: z
        .string({
            required_error: "Password is required",
        })
        .min(4, {
            message: "Password must be at least 8 characters long",
        }),
    currentLocation: geoPointSchema.optional().default({
        type: "Point",
        coordinates: [0, 0],
    }),

    isOnline: z
        .boolean()
        .default(false),

    lastLocationUpdate: geoPointSchema.optional().default({
        type: "Point",
        coordinates: [0, 0],
    }),
});

export const updateLocationSchema = z.object({
    email: z
        .string({
            required_error: "Email is required",
        })
        .email({
            message: "Invalid email",
        }).trim(),
    currentLocation: geoPointSchema.optional().default({
        type: "Point",
        coordinates: [0, 0],
    }),
    isOnline: z.boolean().default(false),
});