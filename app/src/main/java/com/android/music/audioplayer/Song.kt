package com.android.music.audioplayer

import java.io.File

data class Song(
    val title : String,
    val artist : String,
    val filePath : File,
    val duration : Long,
    val songIndex : Int
)