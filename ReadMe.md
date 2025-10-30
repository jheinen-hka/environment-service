# Environment-Service – Übersicht

Der **Environment-Service** ist ein schlanker Spring-Boot-Dienst, der **Umgebungs- und Kontextinformationen** bereitstellt.  
Er ist dafür gedacht, einem digitalen Chat-Agenten zusätzliche Informationen über **Ort, Zeit, Feiertage, Wetter und Sprache** zur Verfügung zu stellen.  
Damit kann der Agent situativ auf die Umgebung eingehen (z. B. „Was machst du so spät noch hier?“ oder „Vergiss den Schirm nicht, es regnet gerade“).

---

## Funktionen

- **Health-Check**
    - `GET /health`
    - Einfache Statusabfrage (`{"ok":true}`)

- **Kontext-Snapshot**
    - `GET /context` oder `POST /context`
    - Liefert einen vollständigen Kontext-Snapshot als JSON.
    - Inhalte:
        - **Location**: Stadt, Land, Region, Geo-Koordinaten (Default: Berlin, wenn nichts angegeben).
        - **Date/Time**: Aktuelle Zeit in ISO-Format, Zeitzone, Wochentag, Tagesabschnitt (morning/afternoon/evening/night).
        - **Holidays**: Liste der Feiertage des aktuellen Landes (via Nager.Date API).
        - **Weather**: Aktuelles Wetter am Standort (via Open-Meteo API).
        - **Locale**: Sprache und Locale basierend auf dem `Accept-Language` Header.

- **Delta-Abfragen**
    - `GET /context/delta?sinceHash=<hash>`
    - Vergleicht mit dem zuletzt bekannten Hash:
        - Wenn sich nichts geändert hat → leeres `data: {}`.
        - Wenn sich etwas geändert hat (z. B. Location) → liefert aktualisierten Kontext zurück.
    - Dadurch kann der Client effizient Änderungen erkennen.

---

## Datenformate

Alle Antworten folgen einer einheitlichen **ContextEnvelope**-Struktur:

```json
{
  "type": "context-snapshot",     // oder "context-delta"
  "version": "1.0",
  "producedAt": "2025-10-30T18:05:21.301+01:00[Europe/Berlin]",
  "hash": "b3a9f1d3d3a0e1b2",     // Hash zur Erkennung von Änderungen
  "data": {
    "location": { "lat": 52.52, "lon": 13.405, "city": "Berlin", "countryCode": "DE" },
    "dateTime": { "iso": "...", "timezone": "Europe/Berlin", "weekday": "Thursday", "partOfDay": "evening" },
    "holidays": [ { "date": "2025-10-03", "localName": "Tag der Deutschen Einheit", "countryCode": "DE" } ],
    "weather": { "provider": "open-meteo", "temperatureC": 12.1, "windKph": 8.4, "precipitationMm": 0 },
    "locale": { "language": "de", "locale": "de-DE" }
  }
}