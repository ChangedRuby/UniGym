package com.example.unigym2.Managers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.security.MessageDigest
import kotlin.random.Random

object AvatarManager {
    public var imageCache: HashMap<String, Bitmap> = hashMapOf()

    public fun getUserAvatar(userId: String, email: String, userName: String, size: Int, lifecycle: LifecycleCoroutineScope, callback: (Bitmap?) -> Unit) {
        var db = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(userId).get().addOnSuccessListener { document ->
            var userImageBase64: String = document.get("avatar").toString()
            Log.d("avatar_manager", "Image base64 retrieved")
            if(userImageBase64 == "null" || userImageBase64 == ""){
                Log.d("avatar_manager", "getting profile image from Gravatar")
                getGravatarBitmap(email, userName, size, lifecycle) { bitmap ->
                    callback(bitmap)
                }
            } else{
                callback(base64ToBitmap(userImageBase64))
            }

        }
    }

    public fun getGravatarBitmap(email: String, userName: String, size: Int, lifecycle: LifecycleCoroutineScope, callback: (Bitmap?) -> Unit) {

        // essa função obtem a foto de usuario pelo hash SHA-256 do email utilizando a API do Gravatar
        val hashedEmail = generateHash(email.lowercase().trim())
        // val apiUrl = "https://gravatar.com/avatar/$hashedEmail?s=$size&d=initials&name=${userName.trim().replace(" ", "+")}"
        // val apiUrl = "https://gravatar.com/avatar/000000000000000000000000000000000000000000000000000000?s=$size&d=initials&name=${userName.trim().replace(" ", "+")}"
        val apiUrl = "https://gravatar.com/avatar/${Random.nextInt(50, 55)}?s=$size&d=initials&name=${userName.trim().replace(" ", "+")}"
        var profileBitmap: Bitmap? = null

        if(imageCache.contains(hashedEmail)){
            Log.d("avatar_manager", "-> avatar already loaded, loading from cache instead")
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

    public fun uriToBase64(uri: Uri?, quality: Int, context: Context): String{

        // converts uri to bitmap
        var inputStream = context.contentResolver.openInputStream(uri!!)
        var imageBitmap = BitmapFactory.decodeStream(inputStream)

        var outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // converts image to base64 after converting to bytearray
        var imageByteArray = outputStream.toByteArray()
        var convertedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
        inputStream?.close()

        return convertedImage
    }

    public fun base64ToBitmap(base64: String): Bitmap?{
        var decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        var decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        return decodedImage
    }

    public fun storeAvatarForUser(userId: String, imageBase64: String){
        var db = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(userId).update("avatar", imageBase64)
    }

    private fun generateHash(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val byteArray = input.toByteArray()
        val hashBytes = md.digest(byteArray)
        val hashString = hashBytes.joinToString("") {"%02x".format(it)}
        return hashString
    }
}