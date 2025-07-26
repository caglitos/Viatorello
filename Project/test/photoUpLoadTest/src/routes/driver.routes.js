import express, {Router} from "express";
import multer from "multer";
import {getDriverPhoto, loginDriver, registerDriver} from "../controller/driver.controller.js";

const storage = multer.memoryStorage();
const upload = multer({storage});
const download = multer({storage: multer.memoryStorage()}); // Para descargar fotos

const router = Router();

router.post("/register", upload.single("photo"), registerDriver);
router.post("/login", express.json(), loginDriver);
router.get("/photo", download.single("photo"), getDriverPhoto); // Para obtener la foto del conductor

export default router;