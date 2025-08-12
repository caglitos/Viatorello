// Configuración de TensorFlow.js para el proyecto Viatorello
// Este archivo configura TensorFlow.js con el backend CPU estable

import * as tf from "@tensorflow/tfjs";
import "@tensorflow/tfjs-backend-cpu";

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
 * Inicializa TensorFlow.js con configuración optimizada
 */
export async function initializeTensorFlow() {
  try {
    // Configurar backend CPU
    await tf.setBackend("cpu");

    // Configurar memoria para evitar leaks
    tf.env().set("WEBGL_CPU_FORWARD", true);

    console.log("✅ TensorFlow.js inicializado correctamente");
    console.log("📊 Backend:", tf.getBackend());
    console.log("📦 Versión:", tf.version.tfjs);

    return true;
  } catch (error) {
    console.error("❌ Error inicializando TensorFlow.js:", error);
    return false;
  }
}

/**
 * Función para limpiar memoria de tensores
 */
export function cleanupTensors(...tensors) {
  tensors.forEach((tensor) => {
    if (tensor && typeof tensor.dispose === "function") {
      tensor.dispose();
    }
  });
}

/**
 * Verificar que TensorFlow.js esté funcionando
 */
export async function verifyTensorFlow() {
  try {
    // Crear tensor de prueba
    const testTensor = tf.tensor([1, 2, 3, 4]);
    const result = tf.add(testTensor, 1);

    const data = await result.data();
    console.log("🧪 Test TensorFlow.js: OK", data);

    // Limpiar memoria
    cleanupTensors(testTensor, result);

    return true;
  } catch (error) {
    console.error("❌ Error en verificación TensorFlow.js:", error);
    return false;
  }
}

// Auto-inicializar si se importa directamente
if (import.meta.url === `file://${process.argv[1]}`) {
  (async () => {
    await initializeTensorFlow();
    await verifyTensorFlow();
  })();
}
