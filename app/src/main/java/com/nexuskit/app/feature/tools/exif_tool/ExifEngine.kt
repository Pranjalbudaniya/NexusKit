package com.nexuskit.app.feature.tools.exif_tool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale

data class ExifTagItem(
    val label: String,
    val value: String,
    val category: String,
    val isSensitive: Boolean = false
)

data class ExifAnalysisResult(
    val uriString: String,
    val fileName: String,
    val fileSizeFormatted: String,
    val dimensions: String,
    val tags: List<ExifTagItem>,
    val hasGps: Boolean,
    val gpsCoordinates: String?,
    val mapsUrl: String?
)

object ExifEngine {

    fun extractExif(context: Context, uri: Uri): Result<ExifAnalysisResult> {
        return runCatching {
            var fileName = "image.jpg"
            var fileSize = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: "image.jpg"
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }

            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Could not open input stream for selected image")

            val exif = ExifInterface(inputStream)
            val tags = mutableListOf<ExifTagItem>()

            // 1. GPS Location
            val latLong = FloatArray(2)
            val hasGps = exif.getLatLong(latLong)
            var gpsCoords: String? = null
            var mapsUrl: String? = null

            if (hasGps) {
                val lat = latLong[0]
                val lng = latLong[1]
                gpsCoords = String.format(Locale.US, "%.6f, %.6f", lat, lng)
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=$lat,$lng"

                tags.add(ExifTagItem("GPS Coordinates", gpsCoords, "GPS & Location", isSensitive = true))
                val alt = exif.getAltitude(0.0)
                if (alt != 0.0) {
                    tags.add(ExifTagItem("GPS Altitude", String.format(Locale.US, "%.1f m", alt), "GPS & Location", isSensitive = true))
                }
                exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)?.let {
                    tags.add(ExifTagItem("GPS Date Stamp", it, "GPS & Location", isSensitive = true))
                }
                exif.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD)?.let {
                    tags.add(ExifTagItem("GPS Processing Method", it, "GPS & Location", isSensitive = true))
                }
            }

            // 2. Camera & Hardware
            exif.getAttribute(ExifInterface.TAG_MAKE)?.let {
                tags.add(ExifTagItem("Camera Make", it, "Camera & Hardware"))
            }
            exif.getAttribute(ExifInterface.TAG_MODEL)?.let {
                tags.add(ExifTagItem("Camera Model", it, "Camera & Hardware", isSensitive = true))
            }
            exif.getAttribute("LensModel")?.let {
                tags.add(ExifTagItem("Lens Model", it, "Camera & Hardware"))
            }
            exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)?.let {
                tags.add(ExifTagItem("Focal Length", "$it mm", "Camera & Hardware"))
            }
            exif.getAttribute("FocalLengthIn35mmFilm")?.let {
                tags.add(ExifTagItem("35mm Equivalent Focal Length", "$it mm", "Camera & Hardware"))
            }
            exif.getAttribute(ExifInterface.TAG_F_NUMBER)?.let {
                tags.add(ExifTagItem("Aperture", "f/$it", "Camera & Hardware"))
            }
            exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)?.let {
                val sec = it.toDoubleOrNull()
                val display = if (sec != null && sec > 0.0 && sec < 1.0) "1/${(1.0 / sec).toInt()}s" else "${it}s"
                tags.add(ExifTagItem("Shutter Speed", display, "Camera & Hardware"))
            }
            (exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS) ?: exif.getAttribute("PhotographicSensitivity"))?.let {
                tags.add(ExifTagItem("ISO Speed", "ISO $it", "Camera & Hardware"))
            }
            exif.getAttribute(ExifInterface.TAG_FLASH)?.let {
                val flashDesc = if (it == "0") "No Flash" else "Flash Fired ($it)"
                tags.add(ExifTagItem("Flash Mode", flashDesc, "Camera & Hardware"))
            }
            exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE)?.let {
                val wb = if (it == "0") "Auto" else "Manual"
                tags.add(ExifTagItem("White Balance", wb, "Camera & Hardware"))
            }

            // 3. Date & Time
            exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)?.let {
                tags.add(ExifTagItem("Date Taken", it, "Date & Time", isSensitive = true))
            }
            exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)?.let {
                tags.add(ExifTagItem("Date Digitized", it, "Date & Time"))
            }
            exif.getAttribute(ExifInterface.TAG_OFFSET_TIME)?.let {
                tags.add(ExifTagItem("Time Zone Offset", it, "Date & Time", isSensitive = true))
            }

            // 4. Dimensions & Format
            val width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
            val height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val dimensions = if (width > 0 && height > 0) "$width × $height px" else "Unknown"

            if (width > 0 && height > 0) {
                tags.add(ExifTagItem("Resolution", dimensions, "Image & Format"))
            }
            val orientLabel = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> "Rotated 90°"
                ExifInterface.ORIENTATION_ROTATE_180 -> "Rotated 180°"
                ExifInterface.ORIENTATION_ROTATE_270 -> "Rotated 270°"
                else -> "Normal (0°)"
            }
            tags.add(ExifTagItem("Orientation", orientLabel, "Image & Format"))

            // 5. Software & Creator
            exif.getAttribute(ExifInterface.TAG_SOFTWARE)?.let {
                tags.add(ExifTagItem("Software / App", it, "Software & Device", isSensitive = true))
            }
            exif.getAttribute(ExifInterface.TAG_ARTIST)?.let {
                tags.add(ExifTagItem("Artist / Author", it, "Software & Device", isSensitive = true))
            }
            exif.getAttribute(ExifInterface.TAG_COPYRIGHT)?.let {
                tags.add(ExifTagItem("Copyright", it, "Software & Device"))
            }

            val sizeFormatted = formatFileSize(fileSize)

            ExifAnalysisResult(
                uriString = uri.toString(),
                fileName = fileName,
                fileSizeFormatted = sizeFormatted,
                dimensions = dimensions,
                tags = tags,
                hasGps = hasGps,
                gpsCoordinates = gpsCoords,
                mapsUrl = mapsUrl
            )
        }
    }

    fun stripExifMetadata(context: Context, uri: Uri): Result<File> {
        return runCatching {
            // First determine orientation to ensure orientation is preserved upon stripping
            var orientation = ExifInterface.ORIENTATION_NORMAL
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val ex = ExifInterface(stream)
                orientation = ex.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }

            // Decode bitmap
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Failed to read image stream")

            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw IllegalArgumentException("Could not decode image bitmap")

            // Rotate bitmap if needed so clean image is upright
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            val orientedBitmap = if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
            } else {
                originalBitmap
            }

            // Save to app cache directory without ANY EXIF headers
            val cleanDir = File(context.cacheDir, "clean_photos")
            if (!cleanDir.exists()) cleanDir.mkdirs()

            val cleanFile = File(cleanDir, "clean_${System.currentTimeMillis()}.jpg")
            FileOutputStream(cleanFile).use { out ->
                orientedBitmap.compress(Bitmap.CompressFormat.JPEG, 96, out)
                out.flush()
            }

            cleanFile
        }
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "Unknown"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1.0) {
            String.format(Locale.US, "%.2f MB", mb)
        } else {
            String.format(Locale.US, "%.1f KB", kb)
        }
    }
}
