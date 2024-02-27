package com.android.music.audioplayer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AudioListScreen(context: Context, navController: NavController, player: ExoPlayer ){

    val audioFiles = remember {
        getLocalAudioFiles(context)
    }

    fun playSong(song : Song, context: Context) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(song.filePath))
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true

    }


        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141414))
            .statusBarsPadding()) {
        items(audioFiles) { song ->
            AudioFileListItem(audioFile = song, navController, context){
                playSong(song, context)
                navController.navigate("player/${song.songIndex}")
            }
        }
    }

}


@Composable
fun AudioFileListItem(audioFile : Song, navController: NavController, context: Context, onSongClick : (Song) -> Unit) {
    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 10.dp)
            .clickable {
                onSongClick(audioFile)
            }
    ){
        AsyncImage(modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x4D242424)),
            model = if (audioFile.albumArt == null){
                audioFile.albumArt
            }
            else{
                R.drawable.album
            },
            contentDescription = "",
            contentScale = ContentScale.Crop)
        Log.d("imageuri", audioFile.albumArt.toString())
//        Image(modifier = Modifier
//            .size(60.dp)
//            .clip(RoundedCornerShape(8.dp)),
//            painter = audioFile.albumArt,
//            contentScale = ContentScale.Crop,
//            contentDescription = "")
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(text = audioFile.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold )
            Text(text = audioFile.artist, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}