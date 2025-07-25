import { Router } from "express";
import {
    register,
    login,
    logout,
    profile,
    updateLocation
} from "../controllers/driver.controller.js";
import { authRequiered } from "../middlewares/validateToken.js";
import { validateSchema } from "../middlewares/validator.middleware.js";
import { loginSchema, driverSchema } from "../schemas/driver.schema.js";


const router = Router();

router.post("/register", validateSchema(driverSchema),register);

router.post("/login", validateSchema(loginSchema), login);

router.post("/logout", logout);

router.get("/profile", authRequiered, profile);

router.put("/update-location", authRequiered, updateLocation);

export default router;