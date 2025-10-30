package de.hka.environmentservice.util

import com.fasterxml.jackson.databind.ObjectMapper
import java.security.MessageDigest

object Hashing {
    /**
     * Bildet aus einem Objekt einen stabilen SHA-256-Hash (auf 16 Hex-Zeichen gekürzt).
     * Nutzt Jackson, damit die Serialisierung konsistent bleibt.
     */
    fun stableHash16(mapper: ObjectMapper, any: Any): String {
        val jsonBytes = mapper.writeValueAsBytes(any)
        val digest = MessageDigest.getInstance("SHA-256").digest(jsonBytes)
        return digest.joinToString("") { b -> "%02x".format(b) }.substring(0, 16)
    }
}