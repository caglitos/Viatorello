import app from "./app.js";
import { port } from "./config.js";
import { connectDB } from "./db.js";

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

console.log(">>> Iniciando aplicación...");

// Conectar a la base de datos
console.log(">>> Conectando a la base de datos...");
connectDB();

console.log(">>> Iniciando servidor...");
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});

console.log(">>> Configuración completada");
