import { z } from "zod";

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
});

export const loginSchema = z.object({
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
});
