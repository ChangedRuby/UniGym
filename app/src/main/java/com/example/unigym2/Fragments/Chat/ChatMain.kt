package com.example.unigym2.Fragments.Chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
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
    private lateinit var backBtn: ImageView
    private lateinit var communicator : Communicator

    private lateinit var db: FirebaseFirestore

    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private lateinit var chatName : TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_chat_main, container, false)

        chatName = view.findViewById(R.id.chatName)
        backBtn = view.findViewById(R.id.goBackBtn)
        communicator = activity as Communicator

        var receiverUid: String? = null
        parentFragmentManager.setFragmentResultListener("chat_name_key", viewLifecycleOwner) { _, bundle ->
            val userName = bundle.getString("name", "Unknown Username")
            val UID = bundle.getString("recieverID")
            chatName.text = userName
            receiverUid = UID
        }

        val senderUid = FirebaseAuth.getInstance().currentUser ?.uid

        val currentInstant: Instant = Instant.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").withZone(ZoneId.of("UTC-3"))
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

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }

        // Adicionando a mensagem ao Database
        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val messageObject = Message(message = messageText, senderId = senderUid)
                val messageData = hashMapOf(
                    "receiver_name" to receiverUid,
                    "sender_name" to senderUid,
                    "message" to messageText,
                    "timestamp" to formattedTimestamp
                )

                // Verifica se a sala de chat já existe
                db.collection("Chats")
                    .document(receiverRoom!!)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Sala de chat existe, adicione a mensagem
                            db.collection("Chats")
                                .document(receiverRoom!!)
                                .collection("messages")
                                .add(messageData)
                                .addOnSuccessListener {
                                    messageBox.text.clear()
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                }
                        } else {
                            // Sala de chat não existe, crie um novo documento
                            db.collection("Chats")
                                .document(receiverRoom!!)
                                .set(hashMapOf("users" to listOf(senderUid, receiverUid))) // Adiciona alguma informação inicial
                                .addOnSuccessListener {
                                    // Agora adicione a mensagem
                                    db.collection("Chats")
                                        .document(receiverRoom!!)
                                        .collection("messages")
                                        .add(messageData)
                                        .addOnSuccessListener {
                                            messageBox.text.clear()
                                        }
                                        .addOnFailureListener { e ->
                                            e.printStackTrace()
                                        }
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }


        return view
    }
}
