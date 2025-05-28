package com.example.unigym2.Fragments.Chat

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.Message
import com.example.unigym2.Fragments.Chat.Recyclerviews.MessageAdapter
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.io.Serializable

class ChatMain : Fragment() {

    private lateinit var mainChatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendImageButton: Button
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var backBtn: ImageView
    private lateinit var communicator: Communicator
    private lateinit var db: FirebaseFirestore
    private lateinit var chatName: TextView
    private lateinit var profileChatImage: ShapeableImageView
    private var loadedProfileImage: Bitmap ?= null
    private lateinit var imageConverted: String
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private var listenerRegistration: ListenerRegistration? = null

    private var chatRoomId: String? = null
    private var receiverUid: String? = null
    private var senderUid: String? = null
    var lockedSenderUid: String? = null
    var lockedReceiverUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val imageUri: Uri = uri
                imageConverted = AvatarManager.uriToBase64(imageUri, 20, requireContext())
                sendMessage(imageConverted, lockedSenderUid!!, lockedReceiverUid!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_main, container, false)

        chatName = view.findViewById(R.id.chatName)
        backBtn = view.findViewById(R.id.goBackBtn)
        profileChatImage = view.findViewById(R.id.profileChatImage)
        communicator = activity as Communicator

        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        db = FirebaseFirestore.getInstance()

        mainChatRecyclerView = view.findViewById(R.id.mainChat_RecycleView)
        messageBox = view.findViewById(R.id.messageBox)
        sendImageButton = view.findViewById(R.id.sendImageButton)
        sendButton = view.findViewById(R.id.enviarButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        mainChatRecyclerView.layoutManager = LinearLayoutManager(context)
        mainChatRecyclerView.adapter = messageAdapter

        parentFragmentManager.setFragmentResultListener("chat_name_key", viewLifecycleOwner) { _, bundle ->
            val userName = bundle.getString("name", "Unknown Username")
            val userImage = bundle.getString("imageBase64")
            val UID = bundle.getString("receiverID")
            chatName.text = userName
            receiverUid = UID
            loadedProfileImage = AvatarManager.base64ToBitmap(userImage!!)

            loadReceiverImage()
            setupChat()
            lockedSenderUid = senderUid
            lockedReceiverUid = receiverUid
            readUnreadMessages(lockedSenderUid!!, lockedReceiverUid!!)
        }

        sendImageButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        sendButton.setOnClickListener {
            val msg = messageBox.text.toString().trim()
            sendMessage(msg, lockedSenderUid!!, lockedReceiverUid!!)
            if (receiverUid == "BROK_AI_AGENT") {
                gerarRespostaIA(msg)
            }
        }

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }

        return view
    }

    private fun loadReceiverImage() {
        if (receiverUid == "BROK_AI_AGENT") {
            profileChatImage.setImageResource(R.drawable.brok_logo)
        } else {
            profileChatImage.setImageBitmap(loadedProfileImage)
            /*db.collection("Usuarios").document(receiverUid ?: "").get().addOnSuccessListener { document ->
                val avatarBase64 = document.getString("avatar")
                if (!avatarBase64.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(avatarBase64, Base64.DEFAULT)
                    val bitmap = AvatarManager.byteArrayToBitmap(imageBytes)
                    profileChatImage.setImageBitmap(bitmap)
                }
            }*/
        }
    }

    private fun setupChat() {
        if (senderUid.isNullOrEmpty() || receiverUid.isNullOrEmpty()) return

        chatRoomId = listOf(senderUid!!, receiverUid!!).sorted().joinToString("")

        val messagesRef = db.collection("Chats")
            .document(chatRoomId!!)
            .collection("messages")
            .orderBy("timestamp")

        messagesRef.get().addOnSuccessListener { snapshot ->
            messageList.clear()
            for (doc in snapshot.documents) {
                val msg = doc.toObject(Message::class.java)
                if (msg != null) {
                    messageList.add(msg)
                }
            }
            messageAdapter.notifyDataSetChanged()
            mainChatRecyclerView.scrollToPosition(messageList.size - 1)
        }

        listenerRegistration = messagesRef.addSnapshotListener { snapshots, error ->
            if (error != null || snapshots == null) return@addSnapshotListener

            for (change in snapshots.documentChanges) {
                if (change.type == DocumentChange.Type.ADDED) {
                    val msg = change.document.toObject(Message::class.java)
                    messageList.add(msg)
                    messageAdapter.notifyItemInserted(messageList.size - 1)
                    mainChatRecyclerView.scrollToPosition(messageList.size - 1)
                }
            }
        }
    }

    private fun sendMessage(msgText: String, lockedSenderUid: String, lockedReceiverUid: String) {
        if (msgText.isNotEmpty() && chatRoomId != null) {
            /*val msgMap = hashMapOf(
                "message" to msgText,
                "senderId" to senderUid,
                "receiverId" to receiverUid,
                "timestamp" to System.currentTimeMillis()
            )

            val chatRoomData = hashMapOf(
                "senderUid" to senderUid,
                "receiverUid" to receiverUid,
                "mensagensNaoLidas" to FieldValue.increment(0)
            )

            db.collection("Chats")
                .document(chatRoomId!!)
                .set(chatRoomData)

            db.collection("Chats")
                .document(chatRoomId!!).get().addOnSuccessListener { document ->
                    val mensagensNaoLidas = document.getLong("mensagensNaoLidas") ?: 0
                    document.reference.update("mensagensNaoLidas", mensagensNaoLidas+1)
                }

            db.collection("Chats")
                .document(chatRoomId!!)
                .collection("messages")
                .add(msgMap)
                .addOnSuccessListener {
                    messageBox.text.clear()
                }*/









            db.collection("Chats")
                .document(chatRoomId!!).get().addOnSuccessListener { chatDoc ->
                    val mensagensNaoLidasReceiver = chatDoc.getLong("mensagensNaoLidasReceiver") ?: 0
                    val mensagensNaoLidasSender = chatDoc.getLong("mensagensNaoLidasSender") ?: 0
                    val docReceiver = chatDoc.getString("receiverUid")
                    val docSender = chatDoc.getString("senderUid")
                    // chatDoc.reference.update("mensagensNaoLidas", mensagensNaoLidas+1)

                    val msgMap = hashMapOf(
                        "message" to msgText,
                        "senderId" to senderUid,
                        "receiverId" to receiverUid,
                        "timestamp" to System.currentTimeMillis()
                    )

                    var chatRoomData: HashMap<String, *>
                    if(docSender == lockedSenderUid){
                        chatRoomData = hashMapOf(
                            "senderUid" to senderUid,
                            "receiverUid" to receiverUid,
                            "mensagensNaoLidasReceiver" to mensagensNaoLidasReceiver,
                            "mensagensNaoLidasSender" to mensagensNaoLidasSender,
                        )
                    } else{
                        chatRoomData = hashMapOf(
                            "senderUid" to senderUid,
                            "receiverUid" to receiverUid,
                            "mensagensNaoLidasSender" to mensagensNaoLidasSender,
                            "mensagensNaoLidasReceiver" to mensagensNaoLidasReceiver,
                        )
                    }


                    db.collection("Chats")
                        .document(chatRoomId!!)
                        .set(chatRoomData)

                    db.collection("Chats")
                        .document(chatRoomId!!)
                        .collection("messages")
                        .add(msgMap)
                        .addOnSuccessListener {
                            messageBox.text.clear()
                        }
                }
        }
    }

    private fun gerarRespostaIA(prompt: String) {
        val gm = GenerativeModel(modelName = "gemini-2.0-flash", apiKey = "AIzaSyC76OQVLSLAYUfEFTus1MB0itOLQPFu1ag")
        lifecycleScope.launch {
            try {
                val response = gm.generateContent(prompt)
                val iaResposta = response.text ?: "Desculpe, não entendi sua pergunta."

                val msgMap = hashMapOf(
                    "message" to iaResposta,
                    "senderId" to "BROK_AI_AGENT",
                    "receiverId" to senderUid,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("Chats")
                    .document(chatRoomId!!)
                    .collection("messages")
                    .add(msgMap)
            } catch (e: Exception) {
                Log.e("GeminiError", "Erro ao gerar resposta da Gemini: ${e.message}")
            }
        }
    }

    fun readUnreadMessages(lockedSenderUid: String, lockedReceiverUid: String){
        Log.d("chat", "sender Uid -> " + senderUid.toString())
        Log.d("chat", "receiverUid -> " + receiverUid.toString())
        Log.d("chat", "auth User -> " + communicator.getAuthUser())
        if(senderUid == communicator.getAuthUser()){
            db.collection("Chats").whereEqualTo("receiverUid", receiverUid).whereEqualTo("senderUid", senderUid)
                .get().addOnSuccessListener { documents ->
                    for(document in documents){
                        val documentSenderUid = document.get("senderUid")
                        val documentReceiverUid = document.get("receiverUid")
                        Log.d("chat", "document sender Uid -> " + documentSenderUid.toString())
                        Log.d("chat", "document receiverUid -> " + documentReceiverUid.toString())
                        document.reference.update("mensagensNaoLidasSender", 0)
                        if(communicator.getAuthUser() == document.get("senderUid")){


                        }
                    }
                }
            db.collection("Chats").whereEqualTo("receiverUid", senderUid).whereEqualTo("senderUid", receiverUid)
                .get().addOnSuccessListener { documents ->
                    for(document in documents){
                        val documentSenderUid = document.get("senderUid")
                        val documentReceiverUid = document.get("receiverUid")
                        Log.d("chat", "document sender Uid -> " + documentSenderUid.toString())
                        Log.d("chat", "document receiverUid -> " + documentReceiverUid.toString())
                        document.reference.update("mensagensNaoLidasReceiver", 0)
                        if(communicator.getAuthUser() == document.get("senderUid")){


                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
    }
}
