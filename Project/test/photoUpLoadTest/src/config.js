import dotenv from "dotenv";

dotenv.config();

export const port = process.env.PORT || 3000;
export const db = process.env.DB_URI;
export const TOKEN_SECRET = process.env.TOKEN_SECRET;
