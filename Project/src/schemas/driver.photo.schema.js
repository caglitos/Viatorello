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
import { z } from "zod";

export const photoSchema = z.object({
    driverID: z.string().length(24), // MongoDB ObjectId length
    photo: z.string().refine(str =>
        /^data:image\/(png|jpeg|jpg|gif|webp);base64,/.test(str), {
        message: "Debe ser una cadena base64 de imagen",
    }),
    contentType:
        z.string().refine(str =>
        /^(image\/(png|jpeg|jpg|gif|webp))$/.test(str), {
        message: "Debe ser un tipo de contenido de imagen válido",
    }),
});