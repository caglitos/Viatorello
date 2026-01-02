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
    getDistanceTime
} from "../controllers/time.controller.js";
import { validateParamsSchema } from "../middlewares/validator.middleware.js";
// import {
//
// } from "../schemas/driverRegisterSchema.js";

const router = Router();

router.get(
    "/getDistanceTime/:lon1/:lat1/:lon2/:lat2",
    // validateParamsSchema(),
    getDistanceTime
);


export default router;
