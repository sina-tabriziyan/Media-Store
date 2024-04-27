package com.example.mediastore

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

class MediaStoreUtils(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun saveImage(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver

            val imageCollection = MediaStore.Images.Media.getContentUri(
                VOLUME_EXTERNAL_PRIMARY
            )

            val timeMillis = System.currentTimeMillis()

            val imageContentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.DISPLAY_NAME, "${timeMillis}_image.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.Images.Media.DATE_TAKEN, timeMillis)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val imageMediaStoreUri = resolver.insert(
                imageCollection, imageContentValues
            )

            imageMediaStoreUri?.let { uri ->
                try {
                    resolver.openOutputStream(uri)?.let { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    imageContentValues.clear()
                    imageContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, imageContentValues, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                }
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun saveVideo(file: File) {
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver

            val videoCollection = MediaStore.Video.Media.getContentUri(
                VOLUME_EXTERNAL_PRIMARY
            )

            val timeMillis = System.currentTimeMillis()

            val videoContentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                put(MediaStore.Video.Media.DISPLAY_NAME, "${timeMillis}_video.mp4")
                put(MediaStore.Video.Media.MIME_TYPE, "videos/mp4")
                put(MediaStore.Video.Media.DATE_ADDED, timeMillis)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }

            val videoMediaStoreUri = resolver.insert(
                videoCollection, videoContentValues
            )

            videoMediaStoreUri?.let { uri ->
                try {
                    resolver.openOutputStream(uri)?.use { outPutStream ->
                        resolver.openInputStream(Uri.fromFile(file))?.use { inputStream ->
                            inputStream.copyTo(outPutStream)
                        }
                    }
                    videoContentValues.clear()
                    videoContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, videoContentValues, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun saveAudio(file: File) {
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver

            val audioCollection = MediaStore.Audio.Media.getContentUri(
                VOLUME_EXTERNAL_PRIMARY
            )

            val timeMillis = System.currentTimeMillis()

            val audioContentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Video.Media.MIME_TYPE, "audio/mpeg")
                put(MediaStore.Video.Media.DATE_ADDED, timeMillis)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }

            val audioMediaStoreUri = resolver.insert(
                audioCollection, audioContentValues
            )

            audioMediaStoreUri?.let { uri ->
                try {
                    resolver.openOutputStream(uri)?.use { outPutStream ->
                        resolver.openInputStream(Uri.fromFile(file))?.use { inputStream ->
                            inputStream.copyTo(outPutStream)
                        }
                    }
                    audioContentValues.clear()
                    audioContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, audioContentValues, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                }
            }
        }

    }


    fun getRawAudioFile(resourceId: Int): File {
        val inputStream = context.resources.openRawResource(resourceId)
        val audioFile = File.createTempFile(
            "temp_audio", ".mp3", context.cacheDir
        )
        audioFile.outputStream().use { outputStream->
            inputStream.copyTo(outputStream)
        }
        return audioFile
    }
}