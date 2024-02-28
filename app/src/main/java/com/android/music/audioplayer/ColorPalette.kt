package com.android.music.audioplayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import coil.util.CoilUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

class ColorPalette {
    fun getBitmapFromImage(context: Context, drawable: Int): Bitmap {
        // on below line we are getting drawable
        val db = ContextCompat.getDrawable(context, drawable)
        // in below line we are creating our bitmap and initializing it.
        val bit = Bitmap.createBitmap(
            10, 10, Bitmap.Config.ARGB_8888
        )
        // on below line we are
        // creating a variable for canvas.
        val canvas = android.graphics.Canvas(bit)
        // on below line we are setting bounds for our bitmap.
        db!!.setBounds(0, 0, canvas.width, canvas.height)
        // on below line we are simply
        // calling draw to draw our canvas.
        db.draw(canvas)
        // on below line we are
        // returning our bitmap.
        return bit
    }

    suspend fun getDominantColor(imageBitmap: ImageBitmap): Color {
        val palette = withContext(Dispatchers.Default) {
            Palette.Builder(imageBitmap.asAndroidBitmap()).maximumColorCount(10).generate()
        }
        val dominantSwatch = palette.dominantSwatch ?: return Color.White
        return Color(dominantSwatch.rgb)
    }
}