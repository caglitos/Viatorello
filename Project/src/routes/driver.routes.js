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

const storage = multer.memoryStorage();
const upload = multer({ storage });
const download = multer({ storage: storage });

const router = Router();

router.post(
  "/register",
  upload.single("photo"),
  validateSchema(driverSchema),
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