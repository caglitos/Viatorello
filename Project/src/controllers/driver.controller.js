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
import DriverPhoto from "../models/driver.photo.model.js";
import {createAccesToken} from "../libs/jwt.js";
import mongoose from "mongoose";

// This function registers a new driver and saves it to the database.
export const register = async (req, res) => {
    try {
        const {
            fullName,
            email,
            password,
            number,
            currentTrip,
            currentLocation,
            isOnline,
            vehicle,
            documents,
        } = req.body;

        const passwordHash = await bcrypt.hash(password, 10);

        const newDriver = new Driver({
            fullName: fullName.trim(),
            email: email.toLowerCase().trim(),
            password: passwordHash,
            number: number,
            currentTrip: currentTrip || {
                type: "Point",
                coordinates: [0, 0],
            },
            currentLocation: currentLocation || {
                type: "Point",
                coordinates: [0, 0],
            },
            isOnline: isOnline !== undefined ? isOnline : false,
            vehicle: vehicle || {},
            documents: documents || {},
        });

        const driverSaved = await newDriver.save();
        const token = await createAccesToken({id: driverSaved._id});

        res.cookie("token", token);

        console.log("Registration successful!");
        return res.json({
            message: "Registration successful! Now send your foto.",
            id: driverSaved._id,
            fullName: driverSaved.fullName,
            email: driverSaved.email,
            currentTrip: driverSaved.currentTrip,
            currentLocation: driverSaved.currentLocation,
            isOnline: driverSaved.isOnline,
            vehicle: driverSaved.vehicle,
            documents: driverSaved.documents,
            token: token,
        });
    } catch (error) {
        console.log("Error details:", error);
        res.status(500).json({
            message: "Error registering driver",
            error: error.message,
        });
    }
};

export const registerPhoto = async (req, res) => {
    try {
        let {driverID, photo, contentType} = req.body;
        const mid = new mongoose.Types.ObjectId(driverID);

        // Validar que la imagen venga en formato correcto base64
        const matches = photo.match(/^data:(.+);base64,(.+)$/);

        contentType = matches[1]; // ej: image/png
        const data = Buffer.from(matches[2], "base64");

        const driverFound = await Driver.find(
            { id: mid },
        );

        if (!driverFound) {
            return res.status(404).json({ message: "Driver not found" });
        }

        const newPhoto = new DriverPhoto({
            driverID: driverID,
            contentType: contentType,
            data: data,
        });

        const photoSaved = await newPhoto.save();

        return res.json({
            message: "Photo uploaded successfully",
            driver: {
                id: driverFound._id,
                fullName: driverFound.fullName,
                email: driverFound.email,
                createdAt: driverFound.createdAt,
                updatedAt: driverFound.updatedAt,
            },
            photo: {
                id: photoSaved._id,
                contentType: contentType,
                size: data.length,
            },
        });
    } catch (error) {
        console.log(error);
        return res.status(500).json({
            message: "Error uploading photo",
            error: error.message,
        });
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

        if (!isMatch) {
            return res.status(401).json({message: "Invalid credentials"});
        }

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
        const {latitude, longitude} = req.params;
        // Example inside your loop:
        const overLap = 100; // Overlap distance between rings
        var minDistance = 0;
        var maxDistance = 500;
        var drivers = [];

        while (drivers.length === 0 && maxDistance <= 5000000) {
            // Limit search to a maximum of 5 km
            drivers = await Driver.find({
                currentLocation: {
                    $near: {
                        $geometry: {
                            type: "Point",
                            coordinates: [longitude, latitude],
                        },
                        $minDistance: minDistance, // Minimum distance
                        $maxDistance: maxDistance, // Maximum distance
                    },
                },
                isOnline: true,
            });

            if (drivers.length === 0) {
                minDistance = maxDistance - overLap; // The next ring starts where the previous one ended, providing a buffer in case a user changed position
                maxDistance += 500; // Increment maximum distance
            }
        }

        if (drivers.length === 0) {
            return res.status(404).json({message: "No drivers found nearby"});
        }

        return res.status(200).json({
            msg: `Succes, found ${drivers.length} drivers, in a radius of ${maxDistance} meters`,
            drivers: drivers.map((d) => ({
                driversId: d._id,
                driverCoordinates: [
                    d.currentLocation.coordinates[1],
                    d.currentLocation.coordinates[0],
                ], // [lat, lng]
            })),
        });
    } catch (error) {
        console.log(error);
        return res
            .status(500)
            .json({message: "Error retrieving nearby drivers", error: error});
    }
};

export const profile = async (req, res) => {
    const { id } = req.params;

    try {

        const driverFound = await Driver.findById(id);

        if (!driverFound) {
            return res.status(404).json({ message: "Driver not found" });
        }
        return res.json({
            driverFound
        });
    } catch (error) {
        console.log(error);
        return res.status(500).json({
            message: "Error retrieving driver profile",
            error: error.message,
        });
    }
}
