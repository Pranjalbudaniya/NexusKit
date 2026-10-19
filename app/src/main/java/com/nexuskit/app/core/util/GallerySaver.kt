package com.nexuskit.app.core.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.IOException

object GallerySaver {

    /**
     * Saves an existing image file directly into the device Gallery (Pictures/NexusKit).
     * Works across Android 10 (API 29) to Android 15 (API 35+) using Scoped Storage.
     */
    fun saveFileToGallery(
        context: Context,
        file: File,
        displayName: String = "NexusKit_${System.currentTimeMillis()}",
        mimeType: String = "image/png",
        titlePrefix: String? = null
    ): Result<Uri> {
        return runCatching {
            val extension = if (mimeType.contains("png", ignoreCase = true)) "png" else "jpg"
            val targetName = if (titlePrefix != null) "${titlePrefix}_${System.currentTimeMillis()}" else displayName
            val fullName = if (targetName.endsWith(".$extension", ignoreCase = true)) targetName else "$targetName.$extension"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fullName)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NexusKit")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IOException("Failed to create MediaStore entry for $fullName")

            resolver.openOutputStream(uri)?.use { out ->
                file.inputStream().use { input ->
                    input.copyTo(out)
                }
                out.flush()
            } ?: throw IOException("Could not open output stream for gallery image")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            uri
        }
    }

    /**
     * Compresses and saves a Bitmap directly into the device Gallery (Pictures/NexusKit).
     */
    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        displayName: String = "NexusKit_${System.currentTimeMillis()}",
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
        titlePrefix: String? = null
    ): Result<Uri> {
        return runCatching {
            val isPng = format == Bitmap.CompressFormat.PNG
            val extension = if (isPng) "png" else "jpg"
            val mimeType = if (isPng) "image/png" else "image/jpeg"
            val targetName = if (titlePrefix != null) "${titlePrefix}_${System.currentTimeMillis()}" else displayName
            val fullName = if (targetName.endsWith(".$extension", ignoreCase = true)) targetName else "$targetName.$extension"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fullName)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NexusKit")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IOException("Failed to create MediaStore entry for $fullName")

            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(format, quality, out)
                out.flush()
            } ?: throw IOException("Could not open output stream for gallery bitmap")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            uri
        }
    }
}
