import express from "express";
import morgan from "morgan";
import cookieParser from "cookie-parser";
import cors from "cors";

import authRoutes from "./routes/auth.routes.js";
import dataRoutes from "./routes/userData.routes.js";

const app = express();

app.use(morgan("dev"));
app.use(express.json());
app.use(cookieParser());

// Permite peticiones desde el frontend en el puerto 5173
app.use(
  cors({
    origin: "http://localhost:5173",
    credentials: true,
  })
);

app.use("/api", authRoutes);
app.use("/api", dataRoutes);

export default app;
