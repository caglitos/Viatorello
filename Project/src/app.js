import express from "express";
import morgan from "morgan";
import cookieParser from "cookie-parser";
import cors from "cors";

import authRoutes from "./routes/auth.routes.js";

const app = express();

app.use(morgan("dev"));
app.use(express.json());
app.use(cookieParser());

// Permite peticiones desde el frontend en el puerto 5173
app.use(
  cors({
    origin: [
      "http://localhost:5173", // React frontend
      "http://10.0.2.2:5173", // Android emulator accessing local dev server
      "http://localhost:8081", // Example: React Native Metro bundler
      "http://10.0.2.2:8081", // Android emulator for Metro
    ],
    credentials: true,
  })
);

app.use("/api", authRoutes);

export default app;
