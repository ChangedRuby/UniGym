package com.example.unigym2.Fragments.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Fragments.Chat.Recyclerviews.Message
import com.example.unigym2.Fragments.Chat.Recyclerviews.MessageAdapter
import com.example.unigym2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatMain : Fragment() {

    private lateinit var mainChatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    private lateinit var db: FirebaseFirestore

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_chat_main, container, false)

        val name = arguments?.getString("name")
        val receiverUid = arguments?.getString("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser ?.uid

        val currentInstant: Instant = Instant.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault())
        val formattedTimestamp: String = formatter.format(currentInstant)

        db = FirebaseFirestore.getInstance()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        mainChatRecyclerView = view.findViewById(R.id.mainChat_RecycleView)
        messageBox = view.findViewById(R.id.messageBox)
        sendButton = view.findViewById(R.id.enviarButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        // Configura o RecyclerView
        mainChatRecyclerView.layoutManager = LinearLayoutManager(context)
        mainChatRecyclerView.adapter = messageAdapter

        // Adicionando a mensagem ao Database
        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val messageObject = Message(message = messageText, senderId = senderUid)
                // Adiciona a mensagem ao Firestore
                val messageData = hashMapOf(
                    "receiver_name" to receiverUid,
                    "sender_name" to senderUid,
                    "message" to messageText,
                    "timestamp" to formattedTimestamp
                )
                db.collection("Chats")
                    .document(senderRoom!!)
                    .collection("messages")
                    .add(messageData)
                    .addOnSuccessListener {
                        // Limpa a caixa de texto após o envio
                        messageBox.text.clear()
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                        e.printStackTrace()
                    }
            }
        }

        return view
    }
}
