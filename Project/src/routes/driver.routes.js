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
    registerPhoto,
} from "../controllers/driver.controller.js";
import { validateSchema } from "../middlewares/validator.middleware.js";
import {
  loginSchema,
  driverSchema,
} from "../schemas/driver.schema.js";
import { photoSchema } from "../schemas/driver.photo.schema.js";

const router = Router();

router.post(
  "/register",
  validateSchema(driverSchema),
  register
);

router.post("/register-photo", validateSchema(photoSchema), registerPhoto);

router.post("/login", validateSchema(loginSchema), login);

router.post("/logout", logout);

router.get("/nearby/:latitude/:longitude", nearby);

router.get("/profile:id", profile);

export default router;
