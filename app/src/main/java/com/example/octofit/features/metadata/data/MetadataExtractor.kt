package com.example.octofit.features.metadata.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Directory
import com.drew.metadata.Metadata
import com.drew.metadata.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

class MetadataExtractor(
    private val appContext: Context,
) {
    suspend fun extract(uri: Uri): List<MetadataEntry> = withContext(Dispatchers.IO) {
        val contentResolver = appContext.contentResolver
        val entries = mutableListOf<MetadataEntry>()

        val mimeType = contentResolver.getType(uri)
        val isExifCapable = isExifCapable(mimeType, uri)
        if (isExifCapable) {
            entries += readExif(contentResolver, uri)
        }

        entries += readMetadataExtractor(contentResolver, uri)

        entries
            .distinctBy { "${it.key}:${it.value}:${it.sourceTag}" }
            .sortedWith(compareBy({ it.key }, { it.sourceTag }))
    }

    private fun isExifCapable(mimeType: String?, uri: Uri): Boolean {
        if (mimeType == null) {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString()).lowercase()
            return extension in setOf("jpg", "jpeg", "tif", "tiff", "webp", "png", "heic", "heif")
        }
        return mimeType.startsWith("image/") || mimeType == "application/octet-stream"
    }

    private fun readExif(
        contentResolver: ContentResolver,
        uri: Uri,
    ): List<MetadataEntry> {
        return contentResolver.openInputStream(uri)?.use { input ->
            val exif = ExifInterface(input)
            val entries = mutableListOf<MetadataEntry>()
            for (tag in EXIF_TAGS) {
                exif.getAttribute(tag)?.let { value ->
                    if (value.isNotBlank()) {
                        entries += MetadataEntry(
                            key = tag,
                            value = value,
                            sourceTag = "EXIF:$tag",
                        )
                    }
                }
            }
            entries
        } ?: emptyList()
    }

    private fun readMetadataExtractor(
        contentResolver: ContentResolver,
        uri: Uri,
    ): List<MetadataEntry> {
        return contentResolver.openInputStream(uri)?.use { input ->
            val safeStream = BufferedInputStream(input)
            safeStream.mark(STREAM_MARK_LIMIT)
            val metadata = try {
                ImageMetadataReader.readMetadata(safeStream)
            } catch (error: ImageProcessingException) {
                return emptyList()
            } catch (error: IOException) {
                return emptyList()
            } catch (error: RuntimeException) {
                return emptyList()
            }
            metadata.toEntries()
        } ?: emptyList()
    }

    private fun Metadata.toEntries(): List<MetadataEntry> {
        val entries = mutableListOf<MetadataEntry>()
        for (directory in directories) {
            entries += directory.toEntries()
        }
        return entries
    }

    private fun Directory.toEntries(): List<MetadataEntry> {
        val directoryName = name
        val entries = mutableListOf<MetadataEntry>()
        for (tag in tags) {
            val value = safeTagValue(tag)
            if (!value.isNullOrBlank()) {
                entries += MetadataEntry(
                    key = tag.tagName,
                    value = value,
                    sourceTag = directoryName,
                )
            }
        }
        return entries
    }

    private fun safeTagValue(tag: Tag): String? = try {
        tag.description
    } catch (error: RuntimeException) {
        null
    }

    companion object {
        private const val STREAM_MARK_LIMIT = 512 * 1024
        private val EXIF_TAGS = listOf(
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_DATETIME_ORIGINAL,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_SOFTWARE,
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_ISO_SPEED_RATINGS,
            ExifInterface.TAG_APERTURE_VALUE,
            ExifInterface.TAG_SHUTTER_SPEED_VALUE,
            ExifInterface.TAG_WHITE_BALANCE,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_ALTITUDE,
        )
    }
}
