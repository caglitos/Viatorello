import { Driver } from "../models/driver.model.js";

export const registerDriver = async (req, res) => {
    console.log("BODY:", req.body);
    console.log("FILE:", req.file);

    // Solo sigue si req.body existe y tiene datos
    if (!req.body) {
        return res.status(400).json({ message: "Body vacío o mal formado", error: "req.body está undefined" });
    }

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
    } catch (error) {
        res.status(500).json({ message: "Error registrando conductor", error: error.message });
        console.log("BODY:", req.body);
        console.log("FILE:", req.file);
    }
};

export const loginDriver = async (req, res) => {
    const { email } = req.body;
    try {
        const driverFound = await Driver.findOne({email: email.toLowerCase().trim()});

        if (!driverFound) return res.status(404).json({message: "User not found"});

        res.status(200).json(driverFound);
    }catch (error) {
        res.status(500).json({ message: "Error registrando conductor", error: error.message });
        console.log("BODY:", req.body);
        console.log("FILE:", req.file);
        console.log(error)
    }
}

export const getDriverPhoto = async (req, res) => {
    const { email } = req.body;
    try {
        const driverFound = await Driver.findOne({email: email.toLowerCase().trim()});

        if (!driverFound) return res.status(404).json({message: "User not found"});

        res.status(200).json(driverFound);
    }catch (error) {
        res.status(500).json({ message: "Error registrando conductor", error: error.message });
        console.log("BODY:", req.body);
        console.log("FILE:", req.file);
    }
}