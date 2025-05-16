package com.example.unigym2.Managers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.security.MessageDigest

object GravatarManager {
    public var imageCache: HashMap<String, Bitmap> = HashMap()

    public fun getGravatarBitmap(email: String, userName: String, size: Int, lifecycle: LifecycleCoroutineScope, callback: (Bitmap?) -> Unit) {

        // essa função obtem a foto de usuario pelo hash SHA-256 do email utilizando a API do Gravatar
        val hashedEmail = generateHash(email.lowercase().trim())
        val apiUrl = "https://gravatar.com/avatar/$hashedEmail?s=$size&d=initials&name=${userName.trim().replace(" ", "+")}"
        var profileBitmap: Bitmap? = null

        if(imageCache.contains(hashedEmail)){
            callback(imageCache.get(hashedEmail))
        } else{
            lifecycle.launch(Dispatchers.IO) {
                val url: URL = URI.create(apiUrl).toURL()
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 10000
                connection.setRequestProperty("Authorization", "Bearer 4182:gk-4fL9c25ISRDpREuWqFIewKMrXBB4DaHcZGLVMwQnpaEKhsh6RXzGP2hvkZhcr")

                val responseCode: Int = connection.responseCode
                Log.d("user_profile", "Response Code: $responseCode")

                if(responseCode == 200){
                    val inputStream = connection.inputStream
                    val bufferedInputStream = BufferedInputStream(inputStream)
                    Log.d("user_profile", "width; $size")

                    profileBitmap = BitmapFactory.decodeStream(bufferedInputStream)
                    imageCache.put(hashedEmail, profileBitmap)
                } else{
                    Log.d("gravatar_manager", "Error while getting data: $responseCode")
                }

                lifecycle.launch(Dispatchers.Main) {
                    callback(profileBitmap)
                }
            }
        }

    }

    private fun generateHash(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val byteArray = input.toByteArray()
        val hashBytes = md.digest(byteArray)
        val hashString = hashBytes.joinToString("") {"%02x".format(it)}
        return hashString
    }
}