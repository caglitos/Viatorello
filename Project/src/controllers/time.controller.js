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

export const getDistanceTime = async (req, res) => {
	try {
		const {  lon1, lat1,  lon2, lat2 } = req.params;

		// Create an AbortController to handle timeout
		const controller = new AbortController();
		const timeout = setTimeout(() => controller.abort(), 10000); // 10 second timeout

		try {
			const response = await fetch(
				`http://router.project-osrm.org/route/v1/driving/${lon1},${lat1};${lon2},${lat2}?overview=false`,
				{
					method: "GET",
					signal: controller.signal
				}
			);

			clearTimeout(timeout);

			if (!response.ok) {
				return res.status(500).json({
					message: "Error fetching route from OSRM service",
					error: `HTTP ${response.status}`
				});
			}

			const data = await response.json();

			// Validate response structure
			if (!data.routes || !data.routes[0] || typeof data.routes[0].duration !== 'number') {
				return res.status(400).json({
					message: "Invalid route data received",
					error: "No valid route found"
				});
			}

			const duration = Math.floor(data.routes[0].duration);

			const hours = Math.floor(duration / 3600);
			const minutes = Math.floor((duration % 3600) / 60);
			const seconds = Math.floor(duration % 60);

			const hh = String(hours).padStart(2, '0');
			const mm = String(minutes).padStart(2, '0');
			const ss = String(seconds).padStart(2, '0');

			let time = `${ss}`;

			if (minutes || hours) time = `${mm}:${ss}`;

			if (hours) time = `${hh}:${mm}:${ss}`;

			return res.status(200).json({
				message: "Distance and time calculated successfully",
				time: time
			});

		} catch (fetchError) {
			clearTimeout(timeout);

			if (fetchError.name === 'AbortError') {
				console.error('Request timeout:', fetchError);
				return res.status(504).json({
					message: "Request timeout: OSRM service is not responding",
					error: "Gateway Timeout"
				});
			}

			console.error('Fetch error:', fetchError);
			return res.status(500).json({
				message: "Failed to connect to OSRM service",
				error: fetchError.message
			});
		}

	} catch (error) {
		console.error('Unexpected error:', error);
		return res.status(500).json({
			message: "Internal server error",
			error: error.message
		});
	}
}
