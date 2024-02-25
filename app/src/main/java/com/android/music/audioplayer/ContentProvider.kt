package com.android.music.audioplayer

import android.content.Context
import android.provider.MediaStore
import java.io.File

    fun getLocalAudioFiles(context: Context) : List<Song>{
        val audioFiles = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.ALBUM_ARTIST
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

            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)

            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)

            val fileColumn = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)

            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)

            while (cursor.moveToNext()){
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val filePath = cursor.getString(fileColumn)
                val duration = cursor.getLong(durationColumn)
                audioFiles.add(Song(title, artist, File(filePath), duration))
                println(audioFiles)
            }
        }
        return audioFiles
    }
