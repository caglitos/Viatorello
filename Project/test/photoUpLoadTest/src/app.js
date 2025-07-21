import express, { Router } from "express";
import multer from "multer";
import { registerDriver } from "./controller/driver.controller.js";

const storage = multer.memoryStorage();
const upload = multer({ storage });

const router = Router();

// Solo esta ruta usa Multer:
router.post("/register", upload.single("photo"), registerDriver);

const app = express();

app.use(router)

export default app;