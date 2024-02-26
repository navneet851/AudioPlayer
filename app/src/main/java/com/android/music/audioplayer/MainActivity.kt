package com.android.music.audioplayer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.AccessController.checkPermission

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            var player : ExoPlayer? = null
            player = ExoPlayer.Builder(context).build()


            NavHost(navController = navController, startDestination = "splash" ){

                composable("splash"){
                    SplashScreen(navController = navController, context = context)
                }
                composable("list"){
                    AudioListScreen(context = context, navController, player)
                }
                composable(
                    route = "player/{songIndex}",
                    arguments = listOf(
                        navArgument("songIndex"){
                            type = NavType.IntType
                        }
                    )
                ){
                    PlayerScreen(navController, player, context, it.arguments!!.getInt("songIndex"))
                }
            }



            //AudioListScreen(context)
        }
    }
}

@Composable
fun SplashScreen(navController: NavController, context : Context) {
    val REQUEST_CODE_READ_AUDIO = 101
    fun requestReadAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above: Request READ_MEDIA_AUDIO
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), REQUEST_CODE_READ_AUDIO)
            }
            else{
                navController.navigate("list")
            }


        } else {
            // Below Android 13: Request READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_AUDIO)
            }
            else{
                navController.navigate("list")
            }

        }
    }


    LaunchedEffect(Unit) {
        requestReadAudioPermission()
    }

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)){
        Icon(
            painter = painterResource(id = R.drawable.ic_devices),
            contentDescription = "logo",
            tint = Color.Blue,
            modifier = Modifier.size(200.dp)
        )
    }
}

