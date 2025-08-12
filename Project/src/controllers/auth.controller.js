import bcrypt from "bcryptjs";
import User from "../models/user.model.js";
import { createAccesToken } from "../libs/jwt.js";

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

/**
 * @file auth.controller.js
 * @description Controller for handling user authentication operations such as registration, login, logout, and profile retrieval.
 * @requires bcryptjs
 * @requires ../models/user.model.js
 * @requires ../libs/jwt.js
 * @exports register, login, logout, profile
 * @module controllers/auth.controller
 *
 * This module provides functions to register a new user, log in an existing user, log out a user, and retrieve the user's profile.
 * Each function interacts with the User model and handles JWT token creation for session management.
 */

//This function generates a new user and saves it to the database.
export const register = async (req, res) => {
  console.log(req.body);
  const {
    username,
    email,
    password,
    currentLocation,
    isOnline,
    lastLocationUpdate,
    currentTrip,
  } = req.body;

  try {
    const passwordHash = await bcrypt.hash(password, 10);

    // Solo asigna los campos definidos en el modelo de Mongoose
    const newUser = new User({
      username: username.trim(),
      email: email.toLowerCase().trim(),
      password: passwordHash,
      currentLocation: currentLocation || {
        type: "Point",
        coordinates: [0, 0],
      },
      lastLocationUpdate: lastLocationUpdate || null,
    });

    if (isOnline !== undefined) newUser.isOnline = isOnline;
    else newUser.isOnline = false;

    if (currentTrip !== undefined) newUser.currentTrip = currentTrip;
    else newUser.currentTrip = null;

    const userSaved = await newUser.save();
    const token = await createAccesToken({ id: userSaved._id });

    res.cookie("token", token);
    res.json({
      id: userSaved._id,
      username: userSaved.username,
      email: userSaved.email,
      createdAt: userSaved.createdAt,
      updatedAt: userSaved.updatedAt,
      currentLocation: userSaved.currentLocation,
      isOnline: userSaved.isOnline,
      lastLocationUpdate: userSaved.lastLocationUpdate,
      currentTrip: userSaved.currentTrip,
      token: token,
    });
  } catch (error) {
    console.log(error);
    res.status(500).json({
      message: "Internal Server Error",
      error: error,
    });
  }
};

// This function logs in a user by verifying their credentials and updating their session information.
export const login = async (req, res) => {
  const {
    email,
    password,
    currentLocation,
    isOnline,
    lastLocationUpdate,
    currentTrip,
  } = req.body;

  try {
    const userFound = await User.findOne({ email: email.toLowerCase().trim() });

    if (!userFound) return res.status(404).json({ message: "User not found" });

    const isMatch = await bcrypt.compare(password, userFound.password);

    if (!isMatch)
      return res.status(401).json({ message: "Invalid credentials" });

    userFound.isOnline = isOnline || userFound.isOnline;
    userFound.currentLocation = currentLocation || userFound.currentLocation;
    userFound.lastLocationUpdate =
      lastLocationUpdate || userFound.lastLocationUpdate;

    // Manejar currentTrip - puede ser false, null, o un ObjectId válido
    if (currentTrip !== undefined) {
      userFound.currentTrip = currentTrip;
    }

    console.log("userFound before save:", {
      currentTrip: userFound.currentTrip,
      currentLocation: userFound.currentLocation,
      isOnline: userFound.isOnline,
    });
    console.log("Request currentTrip:", currentTrip);

    await userFound.save();

    const token = await createAccesToken({ id: userFound._id });

    res.cookie("token", token);
    res.json({
      id: userFound._id,
      username: userFound.username,
      email: userFound.email,
      createdAt: userFound.createdAt,
      updatedAt: userFound.updatedAt,
      currentLocation: userFound.currentLocation,
      isOnline: userFound.isOnline,
      lastLocationUpdate: userFound.lastLocationUpdate,
      currentTrip: userFound.currentTrip,
      token: token,
    });
  } catch (error) {
    console.log(error);
    res.status(500).json({
      message: "Internal Server Error",
      error: error,
    });
  }
};

// This function logs out a user by clearing their session token.
export const logout = (req, res) => {
  res.cookie("token", "", { expires: new Date(0) });
  return res.sendStatus(200);
};

// This function retrieves the profile of the currently authenticated user.
export const profile = async (req, res) => {
  const userFound = await User.findById(req.user.id);

  if (!userFound) {
    return res.status(400).json({ message: "User not found" });
  }

  return res.json({
    id: userFound._id,
    username: userFound.username,
    email: userFound.email,
    isOnline: userFound.isOnline,
    currentLocation: userFound.currentLocation,
    lastLocationUpdate: userFound.lastLocationUpdate,
    currentTrip: userFound.currentTrip,
    createdAt: userFound.createdAt,
    updatedAt: userFound.updatedAt,
  });
};
