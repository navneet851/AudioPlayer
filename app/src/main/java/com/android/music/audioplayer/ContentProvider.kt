package com.android.music.audioplayer

import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File

fun getAlbumArtUri(context: Context, albumId: Long?): Uri? {
    if (albumId == null) return null
    val bitmap = BitmapFactory.decodeResource(Resources.getSystem(), albumId.toInt())
print(bitmap)
    return ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
}



    fun getLocalAudioFiles(context: Context) : List<Song>{


        val audioFiles = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
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

            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            var songIndex = 0
            while (cursor.moveToNext()){
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val filePath = cursor.getString(fileColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val duration = cursor.getLong(durationColumn)

                val coverUri = getAlbumArtUri(context, albumId)

                audioFiles.add(Song(title, artist, File(filePath), duration, songIndex, coverUri))
                songIndex++
            }
        }
        return audioFiles
    }

