package de.hka.environmentservice.model

import com.fasterxml.jackson.annotation.JsonInclude

// --- Grundtypen ---
typealias CountryCode = String // ISO-3166-1 alpha-2 (z. B. "DE")
typealias RegionCode  = String // ISO-3166-2 (z. B. "DE-BW")

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LocationHint(
    val city: String? = null,
    val countryCode: CountryCode? = null,
    val region: RegionCode? = null,
    val lat: Double? = null,
    val lon: Double? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DateTimeContext(
    val iso: String,          // ISO-8601
    val timezone: String,     // IANA tz (z. B. "Europe/Berlin")
    val weekday: String,      // Monday..Sunday (en) oder lokalisiert
    val partOfDay: PartOfDay, // morning/afternoon/evening/night
)

enum class PartOfDay { morning, afternoon, evening, night }

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Holiday(
    val date: String,            // YYYY-MM-DD
    val localName: String,       // z. B. "Tag der Deutschen Einheit"
    val countryCode: CountryCode,
    val regions: List<RegionCode>? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WeatherContext(
    val provider: String = "open-meteo",
    val temperatureC: Double? = null,
    val windKph: Double? = null,
    val precipitationMm: Double? = null,
    val summary: String? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LocaleContext(
    val language: String, // "de"
    val locale: String,   // "de-DE"
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LocationResolved(
    val lat: Double,
    val lon: Double,
    val city: String? = null,
    val countryCode: CountryCode? = null,
    val region: RegionCode? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class EnvironmentContext(
    val location: LocationResolved,
    val dateTime: DateTimeContext,
    val holidays: List<Holiday> = emptyList(),
    val weather: WeatherContext? = null,
    val locale: LocaleContext,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContextEnvelope<T>(
    val type: String,       // "context-snapshot" | "context-delta"
    val version: String = "1.0",
    val producedAt: String, // ISO-8601
    val hash: String,       // sha256/16
    val data: T,
)

// Request-Body für POST /context
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContextInput(
    val locationHint: LocationHint? = null
)