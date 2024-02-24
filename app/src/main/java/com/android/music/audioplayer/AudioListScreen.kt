package com.android.music.audioplayer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import kotlin.math.log

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AudioListScreen(context : Context, navController: NavController, player: ExoPlayer  ) {

    val audioFiles = remember {
        getLocalAudioFiles(context)
    }
//    var player : ExoPlayer? = null


//    val playerView = PlayerView(context)
//    playerView.player = player
//    AndroidView(
//        modifier = Modifier.fillMaxSize(),
//        factory = { playerView }
//    )
    fun playSong(song : AudioMetaData){
        val mediaItem = MediaItem.fromUri(Uri.fromFile(song.filePath))
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true
    }


    
        LazyColumn() {
            items(audioFiles) { audioFile ->
                AudioFileListItem(audioFile = audioFile, navController){
                    playSong(song = audioFiles[6])
                    
                }
            }
        }
    }


@Composable
fun AudioFileListItem(audioFile : AudioMetaData, navController: NavController, onClick : ()-> Unit) {
    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 10.dp)
            .clickable {
                navController.navigate("player")
                onClick()
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