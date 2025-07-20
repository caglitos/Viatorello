import mongoose from "mongoose";

const DriverSchema = new mongoose.Schema({
    name: String,
    email: String,
    photo: {
        data: Buffer,
        contentType: String,
    },
    // ...otros campos de driver si los hay
});

export const Driver = mongoose.model("TestDriver", DriverSchema);