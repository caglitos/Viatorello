import { Driver } from "../models/driver.model.js";

export const registerDriver = async (req, res) => {
    try {
        const { name, email } = req.body;

        let photo = undefined;
        if (req.file) {
            photo = {
                data: req.file.buffer,
                contentType: req.file.mimetype,
            };
        }

        const newDriver = new Driver({
            name,
            email,
            photo,
        });

        await newDriver.save();
        res.status(201).json({ message: "Conductor registrado con foto", driver: newDriver });
        console.log("BODY:", req.body);
        console.log("FILE:", req.file);
    } catch (error) {
        res.status(500).json({ message: "Error registrando conductor", error: error.message });
        console.log("BODY:", req.body);
        console.log("FILE:", req.file);
    }
};