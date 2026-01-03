import { z } from "zod";

export const registerSchema  = z.object({
    name: z.string(),
    email: z.string().email(),
    // Otros campos...
    photo: z.any().optional(), // Aquí irá el buffer de la imagen
});

export const loginSchema = z.object({
    email: z.string().email(),
    // Otros campos...
    photo: z.any().optional(), // Aquí irá el buffer de la imagen
})