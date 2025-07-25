import bcrypt from "bcryptjs";
import driver from "../models/driver.model.js";
import {createAccesToken} from "../libs/jwt.js";

// This function registers a new driver and saves it to the database.
export const register = async (req, res) => {
    const {
        fullName,
        email,
        password,
        currentTrip,
        currentLocation,
        isOnline,
        vehicle,
        documents
    } = req.body;

    try {
        const passwordHash = await bcrypt.hash(password, 10);

        // Only assign fields defined in the Mongoose model
        const newDriver = new driver({
            fullName: fullName.trim(),
            email: email.toLowerCase().trim(),
            password: passwordHash,
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
            documents: documents || {}
        });

        const driverSaved = await newDriver.save();
        const token = await createAccesToken({id: driverSaved._id});

        res.cookie("token", token);
        res.json({
            id: driverSaved._id,
            fullName: driverSaved.fullName,
            email: driverSaved.email,
            createdAt: driverSaved.createdAt,
            updatedAt: driverSaved.updatedAt,
            currentTrip: driverSaved.currentTrip,
            currentLocation: driverSaved.currentLocation,
            isOnline: driverSaved.isOnline,
            vehicle: driverSaved.vehicle,
            documents: driverSaved.documents,
            token: token
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error registering driver",
            error: error
        });
    }
}

// This function logs in a driver and returns their information along with a token.
export const login = async (req, res) => {
    const {
        email,
        password,
        currentTrip,
        currentLocation,
        isOnline,
        vehicle,
        documents
    } = req.body;

    try {
        const driverFound = await driver.findOne({email: email.toLowerCase().trim()});
        if (!driverFound) {
            return res.status(404).json({message: "Driver not found"});
        }

        const isMatch = await bcrypt.compare(password, driverFound.password);

        if (!isMatch)
            return res.status(401).json({message: "Invalid credentials"});

        // Update fields only if they are provided in the request body
        if (currentTrip !== undefined) driverFound.currentTrip = currentTrip;
        if (currentLocation !== undefined) driverFound.currentLocation = currentLocation;
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
            token: token
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error logging in",
            error: error
        });
    }
};

// This function logs out a driver by clearing their session information.
export const logout = async (req, res) => {
    res.cookie("token", "", {expires: new Date(0)});
    return res.sendStatus(200);
};

// This function retrieves the profile information of a driver.
export const profile = async (req, res) => {
    try {
        const driverId = req.user.id; // Assuming user ID is stored in req.user
        const driverFound = await driver.findById(driverId);

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
            documents: driverFound.documents
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error retrieving profile",
            error: error
        });
    }
};

// This function updates the driver's current location and status.
export const updateLocation = async (req, res) => {
    const {currentLocation, isOnline} = req.body;

    try {
        const driverId = req.user.id; // Assuming user ID is stored in req.user
        const driverFound = await driver.findById(driverId);

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
            updatedAt: driverFound.updatedAt
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            message: "Error updating location",
            error: error
        });
    }
};