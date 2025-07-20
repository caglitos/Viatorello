import app from "./app.js";
import { connectDB } from "./db.js";
import { port } from "./config.js";

connectDB();

app.listen(port);

console.log(`Server is running on http://localhost:${port}`);
