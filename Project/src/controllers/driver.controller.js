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

import bcrypt from "bcryptjs";
import Driver from "../models/driver.model.js";
import {createAccesToken} from "../libs/jwt.js";

// This function registers a new driver and saves it to the database.
export const register = async (req, res) => {
    console.log("Request body:", req.body);
    console.log("Request file:", req.file);

    if (!req.body) {
        return res.status(400).json({message: "Body vacío o mal formado"});
    }

    try {
        if (!req.file) {
            return res.status(400).json({message: "Foto requerida"});
        }

        // Importación dinámica para evitar problemas al inicio
        const {detectMainFace} = await import('../libs/faceDetection.js');
        const faceResult = await detectMainFace(req.file.buffer);

        if (!faceResult.hasFace) {
            return res.status(400).json({message: "No se detectó rostro en la imagen"});
        } else {
            console.log("Rostro detectado:", faceResult);
        }

        if (!faceResult.isCloseUp) {
            return res.status(400).json({message: "El rostro no está en primer plano"});
        } else {
            console.log("Rostro detectado:", faceResult);
        }

        const {
            fullName,
            email,
            password,
            currentTrip,
            currentLocation,
            isOnline,
            vehicle,
            documents,
        } = req.body;

        // Parse JSON strings if they come from form-data
        const parsedCurrentTrip =
            typeof currentTrip === "string" ? JSON.parse(currentTrip) : currentTrip;
        const parsedCurrentLocation =
            typeof currentLocation === "string"
                ? JSON.parse(currentLocation)
                : currentLocation;
        const parsedVehicle =
            typeof vehicle === "string" ? JSON.parse(vehicle) : vehicle;
        const parsedDocuments =
            typeof documents === "string" ? JSON.parse(documents) : documents;
        let photo = undefined;
        if (req.file) {
            photo = {
                data: req.file.buffer,
                contentType: req.file.mimetype,
            };
        }

        const passwordHash = await bcrypt.hash(password, 10);

        // Only assign fields defined in the Mongoose model
        const newDriver = new Driver({
            fullName: fullName.trim(),
            email: email.toLowerCase().trim(),
            password: passwordHash,
            currentTrip: parsedCurrentTrip || {
                type: "Point",
                coordinates: [0, 0],
            },
            currentLocation: parsedCurrentLocation || {
                type: "Point",
                coordinates: [0, 0],
            },
            isOnline: isOnline !== undefined ? isOnline : false,
            vehicle: parsedVehicle || {},
            documents: parsedDocuments || {},
            photo: photo,
        });
        const driverSaved = await newDriver.save();
        const token = await createAccesToken({id: driverSaved._id});

        res.cookie("token", token);
        const response = {
            id: driverSaved._id,
            fullName: driverSaved.fullName,
            email: driverSaved.email,
            currentTrip: driverSaved.currentTrip,
            currentLocation: driverSaved.currentLocation,
            isOnline: driverSaved.isOnline,
            vehicle: driverSaved.vehicle,
            documents: driverSaved.documents,
            hasPhoto: !!driverSaved.photo, // Indica si tiene foto sin enviar los datos binarios
            token: token,
        };
        console.log("Registration successful:", response);
        res.json(response);
    } catch (error) {
        res.status(500).json({
            message: "Error registering driver",
            error: error,
        });
        console.log("Request body:", req.body);
        console.log("Request file:", req.file);
        console.log("Error details:", error);
    }
};

// This function logs in a driver and returns their information along with a token.
export const login = async (req, res) => {
    const {
        email,
        password,
        currentTrip,
        currentLocation,
        isOnline,
        vehicle,
        documents,
    } = req.body;

    try {
        const driverFound = await Driver.findOne({
            email: email.toLowerCase().trim(),
        });
        if (!driverFound) {
            return res.status(404).json({message: "Driver not found"});
        }

        const isMatch = await bcrypt.compare(password, driverFound.password);

        if (!isMatch)
            return res.status(401).json({message: "Invalid credentials"});

        // Update fields only if they are provided in the request body
        if (currentTrip !== undefined) driverFound.currentTrip = currentTrip;
        if (currentLocation !== undefined)
            driverFound.currentLocation = currentLocation;
        if (isOnline !== undefined) driverFound.isOnline = isOnline;
        if (vehicle !== undefined) driverFound.vehicle = vehicle;
        if (documents !== undefined) driverFound.documents = documents;

        // Save the updated driver information
        await driverFound.save();

        // Create a new access token for the driver
        const token = await createAccesToken({id: driverFound._id});

        // Set the token in the response cookie
        res.cookie("token", token);
        // Return the driver information along with the token
        res.json({
            id: driverFound._id,
            fullName: driverFound.fullName,
            currentTrip: driverFound.currentTrip,
            currentLocation: driverFound.currentLocation,
            isOnline: driverFound.isOnline,
            vehicle: driverFound.vehicle,
            documents: driverFound.documents,
            email: driverFound.email,
            createdAt: driverFound.createdAt,
            updatedAt: driverFound.updatedAt,
            token: token,
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error logging in",
            error: error,
        });
    }
};

// This function logs out a driver by clearing their session information.
export const logout = async (req, res) => {
    res.cookie("token", "", {expires: new Date(0)});
    return res.sendStatus(200);
};

// This function gets nearby drivers
export const nearby = async (req, res) => {
    try {
        const {
            latitude,
            longitude,
        } = req.body;
        // Ejemplo dentro de tu bucle:
        const overLap = 100; // Distancia de superposición entre anillos
        var minDistance = 0;
        var maxDistance = 500;
        var drivers = [];

        while (drivers.length === 0 && maxDistance <= 5000) { // Limita la búsqueda a un máximo de 5 km
            drivers = await Driver.find({
                currentLocation: {
                    $near: {
                        $geometry: {
                            type: "Point",
                            coordinates: [longitude, latitude],
                        },
                        $minDistance: minDistance,   // Distancia mínima
                        $maxDistance: maxDistance    // Distancia máxima
                    }
                },
                isOnline: true
            });

            if (drivers.length === 0) {
                minDistance = maxDistance - overLap;    // El próximo anillo empieza donde terminó el anterior y da un rango por si un user cambio de posicion
                maxDistance += 500;           // Incrementa la distancia máxima
            }
        }

        if (drivers.length === 0) {
            return res.status(404).json({message: "No drivers found nearby"});
        }

        return res.status(200).json({
            msg: `Succes, found ${drivers.length} drivers, in a radius of ${maxDistance} meters`,
            drivers: drivers.map(d => ({
                driversId: d._id,
                driverCoordinates: [d.currentLocation.coordinates[1], d.currentLocation.coordinates[0]], // [lat, lng]
            }))
        });

    } catch (error) {
        console.log(error)
        return res.status(500).json({message: "Error retrieving nearby drivers", error: error});
    }
}

// This function retrieves the profile information of a driver.
export const profile = async (req, res) => {
    try {
        const driverId = req.user.id; // Assuming user ID is stored in req.user
        const driverFound = await Driver.findById(driverId);

        if (!driverFound) {
            return res.status(404).json({message: "Driver not found"});
        }

        res.json({
            id: driverFound._id,
            fullName: driverFound.fullName,
            email: driverFound.email,
            createdAt: driverFound.createdAt,
            updatedAt: driverFound.updatedAt,
            currentTrip: driverFound.currentTrip,
            currentLocation: driverFound.currentLocation,
            isOnline: driverFound.isOnline,
            vehicle: driverFound.vehicle,
            documents: driverFound.documents,
            hasPhoto: !!driverFound.photo, // Indica si tiene foto
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error retrieving profile",
            error: error,
        });
    }
};

// This function retrieves the photo of the authenticated driver.
export const getDriverPhoto = async (req, res) => {
    try {
        const driverId = req.user.id; // Assuming user ID is stored in req.user
        const driverFound = await Driver.findById(driverId);

        if (!driverFound) {
            return res.status(404).json({message: "Driver not found"});
        }

        if (!driverFound.photo) {
            return res.status(404).json({message: "Photo not found"});
        }

        // Set the correct content type and send the image data
        res.set("Content-Type", driverFound.photo.contentType);
        res.send(driverFound.photo.data);
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error retrieving photo",
            error: error,
        });
    }
};

// This function updates the driver's current location and status.
export const updateLocation = async (req, res) => {
    const {currentLocation, isOnline} = req.body;

    try {
        const driverId = req.user.id; // Assuming user ID is stored in req.user
        const driverFound = await Driver.findById(driverId);

        if (!driverFound) {
            return res.status(404).json({message: "Driver not found"});
        }

        // Update the driver's current location and online status
        if (currentLocation) {
            driverFound.currentLocation = currentLocation;
        }
        if (isOnline !== undefined) {
            driverFound.isOnline = isOnline;
        }

        // Save the updated driver information
        await driverFound.save();

        res.json({
            id: driverFound._id,
            fullName: driverFound.fullName,
            email: driverFound.email,
            currentLocation: driverFound.currentLocation,
            isOnline: driverFound.isOnline,
            createdAt: driverFound.createdAt,
            updatedAt: driverFound.updatedAt,
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error updating location",
            error: error,
        });
    }
};
