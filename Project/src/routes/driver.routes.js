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

import { Router } from "express";
import {
    register,
    login,
    logout,
    profile,
    nearby,
    registerPhoto, publicProfile, registerData,
} from "../controllers/driver.controller.js";
import { validateBodySchema } from "../middlewares/validator.middleware.js";
import {
  loginSchema,
  driverRegisterSchema,
} from "../schemas/driverRegisterSchema.js";
import { photoSchema } from "../schemas/driver.photo.schema.js";

const router = Router();

router.post(
  "/register",
  validateBodySchema(driverRegisterSchema),
  register
);

router.post("/register-photo", validateBodySchema(photoSchema), registerPhoto);

router.post("/login", validateBodySchema(loginSchema), login);

router.post(
    "/register-data",
    // validateBodySchema(),
    registerData
);

router.post("/logout", logout);

router.get("/nearby/:latitude/:longitude", nearby);

router.get("/profile/:id", profile);

router.get(
    "/public-profile/:id",
    publicProfile
);

export default router;
