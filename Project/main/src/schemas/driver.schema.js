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

    vehicle: vehicleSchema
        .optional(),

    documents: documentsSchema
        .optional(),

    lastLocationUpdate: geoPointSchema.optional().default({
        type: "Point",
        coordinates: [0, 0],
    }),

    currentTrip: geoPointSchema.optional().default({
        type: "Point",
        coordinates: [0, 0],
    }),
});
