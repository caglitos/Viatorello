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

		let time

		fetch(`http://router.project-osrm.org/route/v1/driving/${lon1},${lat1};${lon2},${lat2}?overview=false`, {
			method: "GET"
		})
			.then(response => response.json())
			.then(data => {
				const duration = Math.floor(data.routes[0].duration);

				const hours = Math.floor(duration / 3600);
				const minutes = Math.floor((duration % 3600) / 60);
				const seconds = Math.floor(duration % 60);

				const hh = String(hours).padStart(2, '0');
				const mm = String(minutes).padStart(2, '0');
				const ss = String(seconds).padStart(2, '0');

				let time = `${ss}`;

				if (minutes || hours) time = `${mm}:${ss}`;

				if (hours) time = `${hh}:${mm}:${ss}`;json({ message: "Internal Server Error",  error});

				res.status(200).json({
					message: "Distance and time calculated successfully",
					time: time
				});
			})
			.catch(error => res.status(500).json({
				message: "Internal Server Error",  error
			}));

	} catch (error) {
		console.log(error)
		return res.status(500).json({ message: "Internal Server Error",  error});
	}
}