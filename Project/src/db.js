import mongoose from "mongoose";
import { db } from "./config.js";

export const connectDB = async () => {
  console.log(">>> Conecting to DB...");
  try {
    await mongoose.connect(db);
    console.log(">>> DB is connected");
  } catch (error) {
    console.log(error);
  }
};
