import express from "express";
import driverRoutes from "./routes/driver.routes.js";

const app = express();

app.use(driverRoutes)

export default app;
