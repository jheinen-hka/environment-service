package de.hka.environmentservice.service

import de.hka.environmentservice.model.Holiday
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class HolidayService(builder: WebClient.Builder) {
    private val client = builder.baseUrl("https://date.nager.at").build()

    fun fetchHolidays(countryCode: String, year: Int): List<Holiday> = try {
        client.get()
            .uri("/api/v3/PublicHolidays/{year}/{cc}", year, countryCode)
            .retrieve()
            .bodyToFlux(Map::class.java)
            .collectList()
            .map { list ->
                list.mapNotNull { h ->
                    val date = h["date"] as? String ?: return@mapNotNull null
                    val localName = h["localName"] as? String ?: return@mapNotNull null
                    Holiday(date = date, localName = localName, countryCode = countryCode)
                }
            }
            .onErrorReturn(emptyList())
            .block() ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
}