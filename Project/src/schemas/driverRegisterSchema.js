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

const geoPointSchema =
z.object({
    type: z.literal(
        "Point"
    ),
    coordinates: z.array(
        z.number()
    )
        .length(2)
        .optional(), // [longitude, latitude]
}).optional()
  .default({
      type: "Point", coordinates: [0, 0],
  });

const vehicleSchema = z
    .object({
        brand: z.string(),
        model: z.string(),
        year: z.number(),
        licensePlate: z.string(),
        capacity: z.number().default(4),
    })
    .partial();

const documentSchema = z.object({
    number: z.string().optional(),

    expiryDate: z
        .string()
        .optional()
        .refine((date) => {
            if (!date) return true;
            return !isNaN(Date.parse(date));
        }, {
            message: "Invalid date format. Use ISO string format (YYYY-MM-DD or YYYY-MM-DDTHH:mm:ss.sssZ)",
        }),

    isVerified: z.boolean().default(false),
});

const documentsSchema = z.object({
    driverLicense: documentSchema,
    vehicleRegistration: documentSchema,
    insurance: documentSchema,
});

export const driverRegisterSchema = z.object({
    name: z.string().trim(),
    lastName: z.string().trim(),

    email: z
        .email()
        .trim(),
    password: z
        .string({
            required_error: "Password is required",
        })
        .min(4, {
            message: "Password must be at least 8 characters long",
        }),

    currentTrip: geoPointSchema,

    currentLocation: geoPointSchema,

    isOnline: z.boolean().default(false),

});

export const loginSchema = z.object({
    email: z
        .email({
            message: "Invalid email",
        })
        .trim(),
    password: z
        .string({
            required_error: "Password is required",
        })
        .min(4, {
            message: "Password must be at least 8 characters long",
        }),
    currentLocation: geoPointSchema,

    isOnline: z.boolean().default(false),

    lastLocationUpdate: geoPointSchema,
});

export const updateLocationSchema = z.object({
    email: z
        .email({
            message: "Invalid email",
        })
        .trim(),
    currentLocation: geoPointSchema,

    isOnline: z.boolean().default(false),
});
