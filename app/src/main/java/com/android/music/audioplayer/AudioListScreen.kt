package com.android.music.audioplayer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun AudioListScreen() {
    val context = LocalContext.current
    val audioFiles = remember{
        getLocalAudioFiles(context)
    }
    LazyColumn(){
        items(audioFiles){ audioFile ->
            AudioFileListItem(audioFile = audioFile)
            Log.d("AudioFiles", "AudioFile $audioFile")
        }
    }
}


@Composable
fun AudioFileListItem(audioFile : AudioMetaData) {
    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 10.dp)
    ){
        Image(modifier = Modifier
            .size(60.dp),
            painter = painterResource(id = R.drawable.album),
            contentScale = ContentScale.Crop,
            contentDescription = "")
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(text = audioFile.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold )
            Text(text = audioFile.artist, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}