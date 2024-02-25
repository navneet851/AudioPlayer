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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            var player : ExoPlayer? = null

            player = ExoPlayer.Builder(context).build()


            NavHost(navController = navController, startDestination = "list" ){

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



