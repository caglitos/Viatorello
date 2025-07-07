import { z } from "zod";

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
