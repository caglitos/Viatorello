import { Router } from "express";
import multer from "multer";
import { registerDriver } from "../controller/driver.controller.js";

const storage = multer.memoryStorage();
const upload = multer({ storage });

const router = Router();

router.post("/register", upload.single("photo"), registerDriver);

export default router;