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

// src/middlewares/validator.middleware.js
export const validateSchema = (schema) => (req, res, next) => {
  try {
    const result = schema.parse(req.body);
    req.body = result;
    next();
  } catch (err) {
    // Si no es un error de Zod, responde genéricamente
    console.log(err);

    if (!err.errors) {
      return res.status(500).json({ message: "Unexpected validation error" });
    }

    return res.status(400).json({
      message: "Validation failed",
      errors: err.errors.map((e) => ({
        path: e.path.join("."),
        message: e.message,
      })),
    });
  }
};
