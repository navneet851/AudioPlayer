package com.android.music.audioplayer

import java.io.File

data class AudioMetaData(
    val title : String,
    val artist : String,
    val filePath : File,
    val duration : Long
)