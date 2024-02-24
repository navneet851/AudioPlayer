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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {




    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            var player : ExoPlayer? = null
            player = ExoPlayer.Builder(context).build()
            NavHost(navController = navController, startDestination = "home" ){
               composable("home"){
                   MainCompose(context, navController)
               }
                composable("list"){
                    AudioListScreen(context = context, navController, player!!)
                }
                composable("player"){
                    PlayerScreen(player!!)
                }
            }



            //AudioListScreen(context)
        }
    }
}


@Composable
fun MainCompose(context: Context, navController: NavController) {

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
    Button(onClick = {
        requestReadAudioPermission()


    }) {
        Text(text = "referfr")
    }
}

