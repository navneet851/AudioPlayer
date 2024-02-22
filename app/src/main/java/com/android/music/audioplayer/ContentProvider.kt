package com.android.music.audioplayer

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import java.io.File

    fun getLocalAudioFiles(context: Context) : List<AudioMetaData>{
        val audioFiles = mutableListOf<AudioMetaData>()
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DURATION,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            Log.d("MediaStofdfrdde", "Title: $artistColumn")
            val fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            while (cursor.moveToNext()){
                val title = cursor.getString(titleColumn)
                Log.d("MediaStorestring", title)
                val artist = cursor.getString(artistColumn)
                val filePath = cursor.getString(fileColumn)
                val duration = cursor.getLong(durationColumn)
                audioFiles.add(AudioMetaData(title, artist, File(filePath), duration))
            }
        }
        return audioFiles
    }
