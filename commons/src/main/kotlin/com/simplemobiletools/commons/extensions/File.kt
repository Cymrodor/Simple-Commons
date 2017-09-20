package com.simplemobiletools.commons.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.media.MediaMetadataRetriever
import android.webkit.MimeTypeMap
import java.io.File

fun File.isImageVideoGif() = absolutePath.isImageFast() || absolutePath.isVideoFast() || absolutePath.isGif()
fun File.isGif() = absolutePath.endsWith(".gif", true)
fun File.isVideoFast() = absolutePath.videoExtensions.any { absolutePath.endsWith(it, true) }
fun File.isImageFast() = absolutePath.photoExtensions.any { absolutePath.endsWith(it, true) }
fun File.isAudioFast() = absolutePath.audioExtensions.any { absolutePath.endsWith(it, true) }

fun File.isImageSlow() = absolutePath.isImageFast() || getMimeType().startsWith("image")
fun File.isVideoSlow() = absolutePath.isVideoFast() || getMimeType().startsWith("video")
fun File.isAudioSlow() = absolutePath.isAudioFast() || getMimeType().startsWith("audio")

fun File.getMimeType(default: String = getDefaultMimeType()): String {
    try {
        val extension = MimeTypeMap.getFileExtensionFromUrl(absolutePath)
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
    } catch (ignored: Exception) {
    }
    return default
}

fun File.getDefaultMimeType() = if (isVideoFast()) "video/*" else if (isImageFast()) "image/*" else if (isGif()) "image/gif" else absolutePath.getMimeTypeFromPath()

fun File.getDuration(): String? {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(absolutePath)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMs = java.lang.Long.parseLong(time)
        return (timeInMs / 1000).toInt().getFormattedDuration()
    } catch (e: Exception) {
        return null
    }
}

fun File.getArtist(): String? {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(absolutePath)
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    } catch (ignored: Exception) {
        return null
    }
}

fun File.getAlbum(): String? {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(absolutePath)
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    } catch (ignored: Exception) {
        return null
    }
}

fun File.getResolution(): Point {
    return if (isImageFast() || isImageSlow()) {
        getImageResolution()
    } else if (isVideoFast() || isVideoSlow()) {
        getVideoResolution()
    } else {
        return Point(0, 0)
    }
}

fun File.getVideoResolution(): Point {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(absolutePath)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
        return Point(width, height)
    } catch (ignored: Exception) {

    }
    return Point(0, 0)
}

fun File.getImageResolution(): Point {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(absolutePath, options)
    return Point(options.outWidth, options.outHeight)
}

fun File.getCompressionFormat(): Bitmap.CompressFormat {
    return when (extension.toLowerCase()) {
        "png" -> Bitmap.CompressFormat.PNG
        "webp" -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }
}
