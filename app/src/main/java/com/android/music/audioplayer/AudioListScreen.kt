package com.android.music.audioplayer

import android.Manifest
import android.app.Activity
import android.content.ContentProvider
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListMainScreen(context: Context, navController: NavController, player: ExoPlayer) {
    val audioFiles = remember {
        getLocalAudioFiles(context)
    }
    var currentIndex by rememberSaveable {
        mutableStateOf(0)
    }
    var isPlaying by rememberSaveable{
        mutableStateOf(false)
    }
    fun playSong(song : Song, context: Context) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(song.filePath))
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true

    }
    LaunchedEffect(Unit){
        if (!player.isPlaying){
            isPlaying = false
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D0925),
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                title = {
                    Text(text = "Electron")
                })
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .background(Color(0xFF1D0925))
                    .padding(20.dp, 0.dp),
                containerColor = Color(0xFF1D0925),
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x4D242424)),
                            model = audioFiles[currentIndex].albumArt ,
                            contentDescription = "",
                            contentScale = ContentScale.Crop)

                        Column(modifier = Modifier
                            .padding(start = 16.dp)
                            .widthIn(Dp.Unspecified, 190.dp)
                            .horizontalScroll(rememberScrollState())) {
                            Text(text = audioFiles[currentIndex].title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold )
                            Text(text = audioFiles[currentIndex].artist, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }


                    Icon(modifier = Modifier
                        .clickable {
                            if (player.isPlaying)
                                if (isPlaying){
                                    player.pause()
                                    isPlaying = false
                                }
                                else{
                                    player.play()
                                    isPlaying = true
                                }

                            else{
                                playSong(song = audioFiles[currentIndex], context)
                                isPlaying = true
                            }


                        },
                        painter = if (isPlaying)
                            painterResource(id = R.drawable.ic_playing)
                        else
                            painterResource(id = R.drawable.ic_paused),
                        tint = Color.White,
                        contentDescription = "")
                }
            }
        }
    ) {

        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(Color(0xFF141414)),) {
            items(audioFiles) { song ->
                Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 10.dp)
                        .clickable {
                            playSong(song, context)
                            currentIndex = song.songIndex
                            isPlaying = true
                            navController.navigate("player/${song.songIndex}")
                        }
                ){
                    AsyncImage(modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1D0925)),
                        model = song.albumArt ?: R.drawable.album,
                        contentDescription = "",
                        contentScale = ContentScale.Crop)

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(text = song.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold )
                        Text(text = song.artist, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

