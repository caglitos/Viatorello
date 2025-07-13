import { z } from "zod";

const locationSchema = z.object({
  type: z.literal("Point"),
  coordinates: z.tuple([z.number(), z.number()]),
});

// auth.schema.js
export const registerSchema = z.object({
  username: z
    .string({
      required_error: "Username is required",
    })
    .min(3, { message: "Username must be at least 3 characters" })
    .max(30, { message: "Username too long" }),

  email: z.string().email({ message: "Invalid email" }),

  password: z
    .string()
    .min(6, { message: "Password must be at least 6 characters" })
    .regex(/[A-Z]/, {
      message: "Password must contain at least one uppercase letter",
    }),

  currentLocation: locationSchema.optional().default({
    type: "Point",
    coordinates: [0, 0],
  }),

  isOnline: z.boolean().default(false),

  lastLocationUpdate: z.string().datetime().nullable().default(null),

  currentTrip: z.boolean().default(false),
});

export const loginSchema = z.object({
  username: z
    .string({
      required_error: "Username is required",
    })
    .min(3, { message: "Username must be at least 3 characters" })
    .max(30, { message: "Username too long" }),
  email: z
    .string({
      required_error: "Email is required",
    })
    .email({
      message: "Invalid email",
    }),
  password: z
    .string({
      required_error: "Password is required",
    })
    .min(4, {
      message: "Password must be at least 8 characters long",
    }),
  currentLocation: locationSchema.optional().default({
    type: "Point",
    coordinates: [0, 0],
  }),

  isOnline: z.boolean().default(false),

  lastLocationUpdate: z.string().datetime().nullable().default(null),

  currentTrip: z.boolean().default(false),
});

/**
 * id: userFound._id,
      username: userFound.username,
      email: userFound.email,
      createdAt: userFound.createdAt,
      updatedAt: userFound.updatedAt,
      currentLocation: userFound.currentLocation,
      isOnline: userFound.isOnline,
      lastLocationUpdate: userFound.lastLocationUpdate,
      currentTrip: userFound.currentTrip,
      token: token,
 */
