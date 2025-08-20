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

import express from "express";
import morgan from "morgan";
import cookieParser from "cookie-parser";
import cors from "cors";
import authRoutes from "./routes/auth.routes.js";
import driverRoutes from "./routes/driver.routes.js";

const app = express();

app.use(morgan("dev"));
app.use(cookieParser());

// Permite peticiones desde el frontend en el puerto 5173
app.use(
  cors({
    origin: [
      "http://localhost:5173", // React frontend
      "http://10.0.2.2:5173", // Android emulator accessing local dev server
      "http://localhost:8081", // Example: React Native Metro bundler
      "http://10.0.2.2:8081", // Android emulator for Metro
      "http://localhost:46216",
      "http://10.0.2.2:46216",
      "*",
    ],
    credentials: true,
  })
);

// Middleware para parsing JSON
app.use(express.json());

// Rutas
app.use("/api/auth", authRoutes);
app.use("/api/driver", driverRoutes);

export default app;
