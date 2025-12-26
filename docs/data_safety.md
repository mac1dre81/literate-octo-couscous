# Play Console Data Safety Mapping

This app processes image metadata locally on-device using the Storage Access Framework (SAF). It does **not** request broad storage permissions or upload metadata to a server.

## Data collection & sharing summary

**Collected data:** None. Metadata is processed on-device and only leaves the device when the user explicitly uses the Share action.

**Shared data:** None by default. User-initiated sharing via the system share sheet is optional and controlled by the user.

## Potential metadata fields (review before sharing)

Metadata may include sensitive information depending on the image source. The app surfaces all fields and allows redaction before sharing.

| Metadata source | Example fields | Data Safety category | Notes |
| --- | --- | --- | --- |
| EXIF GPS | GPSLatitude, GPSLongitude, GPSAltitude | Location (Precise) | Only present if the photo contains GPS tags. |
| EXIF Camera | Make, Model, Software | Device or other IDs | Device model/manufacturer (not a unique device ID). |
| EXIF Timestamps | DateTimeOriginal | Personal info | Timestamp of capture; can be considered sensitive in some contexts. |
| Embedded tags | Various camera/app tags | Personal info / Device or other IDs | May include app identifiers or device-related strings. |

## Play Console form guidance

- **Data collected:** No.
- **Data shared:** No (unless the user explicitly shares via the share sheet).
- **Data processing:** On-device only; no backend.
- **Data deletion:** Not applicable (no backend storage).

## In-app disclosures

The app shows a privacy notice explaining:
- SAF-only access (no broad storage permissions).
- On-device extraction.
- User review + redaction before sharing.
