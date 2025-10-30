package de.hka.environmentservice.service


import com.fasterxml.jackson.databind.ObjectMapper
import de.hka.environmentservice.model.ContextEnvelope
import de.hka.environmentservice.model.DateTimeContext
import de.hka.environmentservice.model.EnvironmentContext
import de.hka.environmentservice.model.LocaleContext
import de.hka.environmentservice.model.LocationHint
import de.hka.environmentservice.model.LocationResolved
import de.hka.environmentservice.model.PartOfDay
import de.hka.environmentservice.util.Hashing
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*

@Service
class ContextAssembler(
    private val holidayService: HolidayService,
    private val weatherService: WeatherService,
    private val mapper: ObjectMapper,
    @Value("\${app.timezone:Europe/Berlin}") private val defaultTz: String,
) {

    private fun partOfDay(hour: Int): PartOfDay = when {
        hour < 5  -> PartOfDay.night
        hour < 12 -> PartOfDay.morning
        hour < 18 -> PartOfDay.afternoon
        else      -> PartOfDay.evening
    }

    private fun parseLocale(acceptLanguage: String?): LocaleContext {
        val primary = (acceptLanguage ?: "en-US").split(",").first()
        val language = primary.split("-").first()
        return LocaleContext(language = language, locale = primary)
    }

    private fun resolveLocation(hint: LocationHint?): LocationResolved {
        // Reihenfolge: expliziter Hint > Default (Berlin). (IP-Geo kann man später ergänzen)
        val lat = hint?.lat ?: 52.52
        val lon = hint?.lon ?: 13.405
        return LocationResolved(
            lat = lat,
            lon = lon,
            city = hint?.city ?: "Berlin",
            countryCode = hint?.countryCode ?: "DE",
            region = hint?.region
        )
    }

    fun snapshot(acceptLanguage: String?, hint: LocationHint?): ContextEnvelope<EnvironmentContext> {
        val locale = parseLocale(acceptLanguage)
        val loc = resolveLocation(hint)

        val tz = ZoneId.of(defaultTz) // TODO: optional aus Geo ableiten
        val now = ZonedDateTime.now(tz)

        val dt = DateTimeContext(
            iso = now.toString(),
            timezone = tz.id,
            weekday = now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
            partOfDay = partOfDay(now.hour)
        )

        val holidays = loc.countryCode?.let { holidayService.fetchHolidays(it, now.year) } ?: emptyList()
        val weather = weatherService.fetchCurrent(loc.lat, loc.lon)

        val data = EnvironmentContext(
            location = loc,
            dateTime = dt,
            holidays = holidays,
            weather = weather,
            locale = locale
        )

        val producedAt = ZonedDateTime.now(tz).toString()
        val hash = Hashing.stableHash16(mapper, data)

        return ContextEnvelope(
            type = "context-snapshot",
            producedAt = producedAt,
            hash = hash,
            data = data
        )
    }
}