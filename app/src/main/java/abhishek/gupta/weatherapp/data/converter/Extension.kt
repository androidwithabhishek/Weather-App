package abhishek.gupta.weatherapp.data.converter

import android.content.Context
import android.net.Uri
import okio.IOException

@Throws(IOException::class)
fun Uri.uriToByteArray (context: Context) = context .contentResolver.openInputStream(this)?.use {
    it.buffered().readBytes()
}