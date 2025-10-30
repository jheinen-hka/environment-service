package de.hka.environmentservice.service

import de.hka.environmentservice.model.WeatherContext
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class WeatherService(builder: WebClient.Builder) {
    private val client = builder.baseUrl("https://api.open-meteo.com/v1").build()

    fun fetchCurrent(lat: Double, lon: Double): WeatherContext? = try {
        val uri = "/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,precipitation,wind_speed_10m"

        client.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(Map::class.java)
            .map { json ->
                val current = json["current"] as? Map<*, *> ?: emptyMap<String, Any>()
                WeatherContext(
                    temperatureC = (current["temperature_2m"] as? Number)?.toDouble(),
                    windKph = (current["wind_speed_10m"] as? Number)?.toDouble(),
                    precipitationMm = (current["precipitation"] as? Number)?.toDouble(),
                    summary = null
                )
            }
            .block()
    } catch (_: Exception) {
        null
    }
}