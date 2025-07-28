import express, { Router } from "express";
import {
  register,
  login,
  logout,
  profile,
  getDriverPhoto,
  updateLocation,
} from "../controllers/driver.controller.js";
import { authRequiered } from "../middlewares/validateToken.js";
import { validateSchema } from "../middlewares/validator.middleware.js";
import {
  loginSchema,
  driverSchema,
  updateLocationSchema,
} from "../schemas/driver.schema.js";
import multer from "multer";

const storage = multer.memoryStorage();
const upload = multer({ storage });
const download = multer({ storage: storage });

const router = Router();

console.log("[ROUTE] /api/driver/register route loaded");

router.post(
  "/register",
  upload.single("photo"),
  // validateSchema(driverSchema),
  register
);

router.post("/login", express.json(), validateSchema(loginSchema), login);

router.post("/logout", logout);

router.get("/profile", authRequiered, profile);

router.get("/photo", authRequiered, getDriverPhoto);

router.put(
  "/update-location",
  express.json(),
  authRequiered,
  validateSchema(updateLocationSchema),
  updateLocation
);

export default router;
