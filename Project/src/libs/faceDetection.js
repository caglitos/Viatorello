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
import * as faceapi from "@vladmandic/face-api";
import canvas from "canvas";
import path from "path";
import { fileURLToPath } from "url";

const LOG = "[faceDetection]";

// Configuración para usar canvas en Node
const { Canvas, Image, ImageData } = canvas;

faceapi.env.monkeyPatch({ Canvas, Image, ImageData });

// Ubicación de los modelos
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const MODEL_PATH = path.join(__dirname, "../ai-models");
let modelsLoaded = false;

export async function loadModels() {
    if (!modelsLoaded) {
        await faceapi.nets.ssdMobilenetv1.loadFromDisk(MODEL_PATH);
        modelsLoaded = true;
    }
}

export async function detectMainFace(imageBuffer) {
    await loadModels();
    const img = await canvas.loadImage(imageBuffer);
    const detections = await faceapi.detectAllFaces(img);

    if (detections.length === 0) {
        return {hasFace: false, isCloseUp: false};
    }

    const biggestFace = detections.reduce((max, det) =>
        det.box.width * det.box.height > max.box.width * max.box.height ? det : max
    );

    const imgArea = img.width * img.height;
    const faceArea = biggestFace.box.width * biggestFace.box.height;
    const isCloseUp = faceArea / imgArea > 0.4;

    return {hasFace: true, isCloseUp};
}
