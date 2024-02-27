package com.android.music.audioplayer

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Window
import android.widget.HorizontalScrollView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import androidx.compose.ui.platform.AndroidUiDispatcher


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

    var dominantColor by remember { mutableStateOf(Color.Gray) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val resourceId = R.drawable.album


    LaunchedEffect(resourceId) {
         bitmap = withContext(Dispatchers.IO) {
            ColorPalette().getBitmapFromImage(context, resourceId)
        }
             dominantColor = ColorPalette().getDominantColor(bitmap!!.asImageBitmap())

    }



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
        Image(
            modifier = Modifier
                .height(385.dp)
                .padding(20.dp)
                .clip(RoundedCornerShape(10.dp))
            ,painter = painterResource(id = R.drawable.album),
            contentScale = ContentScale.Crop,
            contentDescription = "")
        Spacer(modifier = Modifier.padding(30.dp))
        PlayerInfo(audioFiles[currentSongIndex].title, audioFiles[currentSongIndex].artist)
//        Slider(
//            modifier = Modifier
//                .height(20.dp)
//                .padding(20.dp),
//            value = currentPosition.toFloat() / duration.toFloat(),
//            onValueChange = {value ->
//                seekTo(value.toLong() * (duration ?: 0L))
//            },
//            valueRange = 0f..1f
//        )
        Spacer(modifier = Modifier.padding(16.dp))
        //
        //
        //
        //

        fun playSong(song : Song, context: Context) {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(song.filePath))
            player!!.setMediaItem(mediaItem)
            player!!.prepare()
            player!!.playWhenReady = true

        }
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
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