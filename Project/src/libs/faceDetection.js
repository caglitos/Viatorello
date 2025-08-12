// src/libs/faceDetection.js
import * as faceapi from "@vladmandic/face-api";
import canvas from "canvas";
import path from "path";
import { fileURLToPath } from "url";

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

const LOG = "[faceDetection]";

// Configuración para usar canvas en Node
const { Canvas, Image, ImageData } = canvas;

let isInitialized = false;
let initializationPromise = null;

async function initializeFaceAPI() {
  if (initializationPromise) {
    return initializationPromise;
  }
  
  if (!isInitialized) {
    initializationPromise = (async () => {
      try {
        console.log(LOG, "Inicializando Face API...");
        
        // Configurar el entorno SOLO cuando se necesite
        faceapi.env.monkeyPatch({ Canvas, Image, ImageData });
        
        // Configurar backend de TensorFlow con timeout más corto
        await Promise.race([
          (async () => {
            await faceapi.tf.setBackend('cpu');
            await faceapi.tf.ready();
          })(),
          new Promise((_, reject) => 
            setTimeout(() => reject(new Error('Timeout inicializando TensorFlow')), 5000)
          )
        ]);
        
        console.log(LOG, "Backend configurado:", faceapi.tf.getBackend());
        isInitialized = true;
        return true;
      } catch (error) {
        console.error(LOG, "Error inicializando Face API:", error.message);
        console.warn(LOG, "La aplicación funcionará sin detección facial");
        isInitialized = false;
        initializationPromise = null;
        return false;
      }
    })();
    
    return initializationPromise;
  }
  
  return true;
}

// Ubicación de los modelos
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const MODEL_PATH = path.join(__dirname, "../ai-models");
let modelsLoaded = false;

export async function loadModels() {
  await initializeFaceAPI(); // Asegurar que Face API esté inicializado
  
  if (!modelsLoaded) {
    try {
      console.log(LOG, "Cargando modelos desde:", MODEL_PATH);
      await faceapi.nets.ssdMobilenetv1.loadFromDisk(MODEL_PATH);
      modelsLoaded = true;
      console.log(LOG, "Modelos cargados exitosamente");
    } catch (error) {
      console.error(LOG, "Error cargando modelos:", error);
      // No lanzar error aquí para evitar que la app falle al inicio
      console.warn(LOG, "La aplicación funcionará sin detección facial");
    }
  }
}

export async function detectMainFace(imageBuffer) {
  try {
    await loadModels();
    
    if (!modelsLoaded) {
      console.warn(LOG, "Modelos no cargados, retornando sin detección");
      return { hasFace: false, isCloseUp: false, error: "Modelos no disponibles" };
    }
    
    const img = await canvas.loadImage(imageBuffer);
    const detections = await faceapi.detectAllFaces(img);

    if (detections.length === 0) {
      return { hasFace: false, isCloseUp: false };
    }

    const biggestFace = detections.reduce((max, det) =>
      det.box.width * det.box.height > max.box.width * max.box.height ? det : max
    );

    const imgArea = img.width * img.height;
    const faceArea = biggestFace.box.width * biggestFace.box.height;
    const isCloseUp = faceArea / imgArea > 0.4;

    return { hasFace: true, isCloseUp };
  } catch (error) {
    console.error(LOG, "Error en detectMainFace:", error);
    return { hasFace: false, isCloseUp: false, error: error.message };
  }
}
