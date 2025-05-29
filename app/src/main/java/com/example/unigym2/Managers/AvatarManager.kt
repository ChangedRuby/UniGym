package com.example.unigym2.Managers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.unigym2.Activities.Communicator
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
    var communicator: Communicator? = null

    fun getUserAvatar(userId: String, email: String, userName: String, size: Int, lifecycle: LifecycleCoroutineScope, callback: (Bitmap?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val hashedEmail = generateHash(email.lowercase().trim())
        communicator?.showLoadingOverlay()

        if (imageCache.containsKey(hashedEmail)) {
            Log.d("avatar_manager", "-> avatar already loaded, loading from cache instead")
            communicator?.hideLoadingOverlay()
            callback(imageCache[hashedEmail])
        } else {
            db.collection("Usuarios").document(userId).get().addOnSuccessListener { document ->
                val userImageBase64 = document.get("avatar").toString()
                Log.d("avatar_manager", "Image base64 retrieved")
                if (!isJpeg(userImageBase64)) {
                    Log.d("avatar_manager", "getting profile image from Gravatar")
                    getGravatarBitmap(email, userName, size, lifecycle) { bitmap ->
                        imageCache[hashedEmail] = bitmap!!
                        communicator?.hideLoadingOverlay()
                        callback(bitmap)
                    }
                } else {
                    val bitmapImage = base64ToBitmap(userImageBase64)
                    imageCache[hashedEmail] = bitmapImage!!
                    communicator?.hideLoadingOverlay()
                    callback(bitmapImage)
                }
            }
        }
    }

    fun getGravatarBitmap(email: String, userName: String, size: Int, lifecycle: LifecycleCoroutineScope, callback: (Bitmap?) -> Unit) {
        val hashedEmail = generateHash(email.lowercase().trim())
        val apiUrl = "https://gravatar.com/avatar/${Random.nextInt(50, 55)}?s=$size&d=initials&name=${userName.trim().replace(" ", "+")}"
        var profileBitmap: Bitmap? = null

        lifecycle.launch(Dispatchers.IO) {
            val url: URL = URI.create(apiUrl).toURL()
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 10000
            connection.setRequestProperty("Authorization", "Bearer 4182:gk-4fL9c25ISRDpREuWqFIewKMrXBB4DaHcZGLVMwQnpaEKhsh6RXzGP2hvkZhcr")

            val responseCode: Int = connection.responseCode
            Log.d("user_profile", "Response Code: $responseCode")

            if (responseCode == 200) {
                val inputStream = connection.inputStream
                val bufferedInputStream = BufferedInputStream(inputStream)
                profileBitmap = BitmapFactory.decodeStream(bufferedInputStream)
            } else {
                Log.d("gravatar_manager", "Error while getting data: $responseCode")
            }

            lifecycle.launch(Dispatchers.Main) {
                callback(profileBitmap)
            }
        }
    }

    fun uriToBase64(uri: Uri?, quality: Int, context: Context): String {
        val inputStream = context.contentResolver.openInputStream(uri!!)
        val imageBitmap = BitmapFactory.decodeStream(inputStream)

        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        val imageByteArray = outputStream.toByteArray()
        val convertedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
        inputStream?.close()

        return convertedImage
    }

    fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun storeAvatarForUser(userId: String, userEmail: String, imageBase64: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(userId).update("avatar", imageBase64)
        imageCache[generateHash(userEmail.lowercase().trim())] = base64ToBitmap(imageBase64)!!
    }

    fun setOverlayCommunicator(communicator: Communicator) {
        this.communicator = communicator
    }

    fun bitmapToBase64(bitmap: Bitmap, quality: Int): String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream) // Adjust format and quality
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun isJpeg(base64: String): Boolean {
        if (base64 == "null" || base64 == "") {
            Log.d("avatar_manager", "-> not a jpeg")
            return false
        }

        val image: Bitmap? = base64ToBitmap(base64)
        if (image == null) {
            Log.d("avatar_manager", "-> not a jpeg")
            return false
        }

        return true
    }

    private fun generateHash(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val byteArray = input.toByteArray()
        val hashBytes = md.digest(byteArray)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
