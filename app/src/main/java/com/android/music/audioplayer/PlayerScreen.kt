package com.android.music.audioplayer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.style.TextAlign
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.formatDuration


@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("DiscouragedApi")
@Composable
fun PlayerScreen(navController: NavController, player: ExoPlayer, context: Context, songIndex : Int) {

    val audioFiles = remember {
        getLocalAudioFiles(context)
    }
    var isPlaying by remember{
        mutableStateOf(true)
    }
    var currentSongIndex by remember {
        mutableStateOf(songIndex)
    }
    var currentPosition by remember { mutableStateOf(0L) }
    var dominantColor by remember { mutableStateOf(Color.Gray) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val resourceId = R.drawable.album
        //audioFiles[currentSongIndex].albumArt
    LaunchedEffect(resourceId) {
        bitmap = withContext(Dispatchers.IO) {
            ColorPalette().getBitmapFromImage(context, resourceId)
        }
        dominantColor = ColorPalette().getDominantColor(bitmap!!.asImageBitmap())

    }
    fun playSong(song : Song, context: Context) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(song.filePath))
        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true

    }
    fun formatDuration(millis: Long): String {
        val minutes = millis / (1000 * 60) % 60 // Get minutes within an hour
        val seconds = (millis / 1000) % 60 // Get seconds within a minute

        return String.format("%02d:%02d", minutes, seconds) // Format with leading zeroes
    }

        player.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                currentPosition = player.currentPosition
            }
        }
    })

    Column(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    dominantColor,
                    Color.Black
                ),
                startY = 250f
            )
        )
        .statusBarsPadding()
    ) {
        PlayerTopBar(navController)
        Spacer(modifier = Modifier.padding(16.dp))
        AsyncImage(
            modifier = Modifier
                .height(350.dp)
                .padding(20.dp, 10.dp)
                .clip(RoundedCornerShape(10.dp))
            ,model = audioFiles[currentSongIndex].albumArt,
            contentScale = ContentScale.Crop,
            contentDescription = "")

        Spacer(modifier = Modifier.padding(30.dp))

        PlayerInfo(audioFiles[currentSongIndex].title, audioFiles[currentSongIndex].artist)

        Slider(
            value = player.currentPosition.toFloat() / player.duration.toFloat(),
            valueRange = 0f..1f,
            onValueChange = { newValue ->
                    player.pause()
                    player.seekTo((newValue * player.duration).toLong())
                    player.playWhenReady = true

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            var currentMillisPosition by remember{
                mutableStateOf(player.currentPosition)
            }
            LaunchedEffect(player.currentPosition) {
                currentMillisPosition = player.currentPosition
            }
            Text(
                text = formatDuration(currentMillisPosition),
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
                Text(
                text = formatDuration(audioFiles[currentSongIndex].duration),
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

        }


        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 10.dp)
        ) {

            Icon(
                modifier = Modifier
                    .size(25.dp),
                tint = Color.Gray,
                painter = painterResource(id = R.drawable.ic_player_shuffle),
                contentDescription = "")
            Icon(
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        currentSongIndex -= 1
                        playSong(audioFiles[currentSongIndex], context)
                        if (!isPlaying) {
                            isPlaying = true
                        }
                    },
                tint = Color.White,
                painter = painterResource(id = R.drawable.ic_player_back),
                contentDescription = "")
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.White)
                    .clickable {
                        if (isPlaying) {
                            player.pause()
                            isPlaying = false
                        } else {
                            player.play()
                            isPlaying = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .heightIn(32.dp),
                    tint = Color.Black,
                    painter = if (isPlaying)
                        painterResource(id = R.drawable.ic_playing)
                    else
                        painterResource(id = R.drawable.ic_paused),
                    contentDescription = "")
            }

            Icon(
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        currentSongIndex += 1
                        playSong(audioFiles[currentSongIndex], context)
                        if (!isPlaying) {
                            isPlaying = true
                        }
                    },
                tint = Color.White,
                painter = painterResource(id = R.drawable.ic_player_skip),
                contentDescription = "")
            Icon(
                modifier = Modifier
                    .size(20.dp),
                tint = Color.Gray,
                painter = painterResource(id = R.drawable.ic_repeat),
                contentDescription = "")
        }
        //
        //
        //
        //

        PlayerEndInfo()
    }
}



@Composable
fun PlayerTopBar(navController: NavController) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(modifier = Modifier
            .clickable {
               navController.navigate("list")
            },
            painter = painterResource(id = R.drawable.ic_down),
            tint = Color.White,
            contentDescription = "")
        Text(text = "PLAYING SONG", color = Color.White)
        Icon(painter = painterResource(id = R.drawable.ic_dots), tint = Color.White, contentDescription = "")
    }
}

@Composable
fun PlayerInfo(albumName : String, singerName : String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)){
            Column(
                modifier = Modifier
                    .widthIn(Dp.Unspecified, 200.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = albumName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = singerName, color = Color.White, fontSize = 15.sp,)
            }
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.width(90.dp)) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(id = R.drawable.ic_minus),
                tint = Color.White,
                contentDescription = "")
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(id = R.drawable.ic_add),
                tint = Color.White,
                contentDescription = "")
        }
    }
}

@Composable
fun PlayerEndInfo() {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)){
        Icon(
            modifier = Modifier
                .size(25.dp),
            painter = painterResource(id = R.drawable.ic_devices),
            tint = Color.White,
            contentDescription = "")
        Icon(
            modifier = Modifier
                .size(18.dp),
            painter = painterResource(id = R.drawable.ic_share),
            tint = Color.White,
            contentDescription = "")
    }
}

@Composable
fun PlayerFull(player: ExoPlayer, context: Context, navController: NavController,songList : List<Song>, songIndex : Int) {


}