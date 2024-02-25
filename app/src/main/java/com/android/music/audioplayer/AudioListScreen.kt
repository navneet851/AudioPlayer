package com.android.music.audioplayer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AudioListScreen(context: Context, navController: NavController, player: ExoPlayer ){
    val REQUEST_CODE_READ_AUDIO = 101
    fun requestReadAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above: Request READ_MEDIA_AUDIO
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), REQUEST_CODE_READ_AUDIO)
            }

        } else {
            // Below Android 13: Request READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_AUDIO)
            }
//            else{
//                navController.navigate("list")
//            }
        }
    }
    LaunchedEffect(Unit){
        requestReadAudioPermission()
    }
    val audioFiles = remember {
        getLocalAudioFiles(context)
    }
    fun playSong(song : Song, context: Context) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(song.filePath))
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true

    }


        LazyColumn() {
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
        Image(modifier = Modifier
            .size(60.dp),
            painter = painterResource(id = R.drawable.album),
            contentScale = ContentScale.Crop,
            contentDescription = "")
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(text = audioFile.title, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold )
            Text(text = audioFile.artist, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}