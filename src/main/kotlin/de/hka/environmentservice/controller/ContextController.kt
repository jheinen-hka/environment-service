package de.hka.environmentservice.controller

import de.hka.environmentservice.model.ContextEnvelope
import de.hka.environmentservice.model.ContextInput
import de.hka.environmentservice.model.EnvironmentContext
import de.hka.environmentservice.model.LocationHint
import de.hka.environmentservice.service.ContextAssembler
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class ContextController(private val assembler: ContextAssembler) {

    @GetMapping("/health")
    fun health() = mapOf("ok" to true)

    @GetMapping("/context", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getContext(
        @RequestHeader(name = "Accept-Language", required = false) acceptLanguage: String?,
        @RequestParam(required = false) lat: Double?,
        @RequestParam(required = false) lon: Double?,
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) countryCode: String?,
        @RequestParam(required = false) region: String?,
    ): ContextEnvelope<EnvironmentContext> {
        val hint = if (lat != null && lon != null) LocationHint(city, countryCode, region, lat, lon) else null
        return assembler.snapshot(acceptLanguage, hint)
    }

    @PostMapping("/context", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun postContext(
        @RequestHeader(name = "Accept-Language", required = false) acceptLanguage: String?,
        @RequestBody(required = false) body: ContextInput?
    ): ContextEnvelope<EnvironmentContext> =
        assembler.snapshot(acceptLanguage, body?.locationHint)

    @GetMapping("/context/delta", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDelta(
        @RequestHeader(name = "Accept-Language", required = false) acceptLanguage: String?,
        @RequestParam(required = false) sinceHash: String?,
        @RequestParam(required = false) lat: Double?,
        @RequestParam(required = false) lon: Double?,
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) countryCode: String?,
        @RequestParam(required = false) region: String?,
    ): ContextEnvelope<Any> {
        val hint = if (lat != null && lon != null) LocationHint(city, countryCode, region, lat, lon) else null
        val snap = assembler.snapshot(acceptLanguage, hint)
        return if (sinceHash.isNullOrBlank() || sinceHash == snap.hash) {
            ContextEnvelope(
                type = "context-delta",
                producedAt = snap.producedAt,
                hash = snap.hash,
                data = emptyMap<String, Any>()
            )
        } else {
            ContextEnvelope(
                type = "context-delta",
                producedAt = snap.producedAt,
                hash = snap.hash,
                data = snap.data
            )
        }
    }
}