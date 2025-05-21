package com.example.unigym2.Fragments.Chat

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.ListenerRegistration

class ChatMain : Fragment() {

    private lateinit var mainChatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var backBtn: ImageView
    private lateinit var communicator: Communicator
    private lateinit var db: FirebaseFirestore
    private lateinit var chatName: TextView
    private lateinit var listenerRegistration: ListenerRegistration

    private var chatRoomId: String? = null
    private var receiverUid: String? = null
    private var senderUid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_main, container, false)

        chatName = view.findViewById(R.id.chatName)
        backBtn = view.findViewById(R.id.goBackBtn)
        communicator = activity as Communicator

        parentFragmentManager.setFragmentResultListener("chat_name_key", viewLifecycleOwner) { _, bundle ->
            val userName = bundle.getString("name", "Unknown Username")
            val UID = bundle.getString("recieverID")
            chatName.text = userName
            receiverUid = UID
            setupChat()
        }

        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        db = FirebaseFirestore.getInstance()

        mainChatRecyclerView = view.findViewById(R.id.mainChat_RecycleView)
        messageBox = view.findViewById(R.id.messageBox)
        sendButton = view.findViewById(R.id.enviarButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        mainChatRecyclerView.layoutManager = LinearLayoutManager(context)
        mainChatRecyclerView.adapter = messageAdapter

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }

        return view
    }

    private fun setupChat() {
        if (senderUid == null || receiverUid == null) return

        // Gera um ID de sala único baseado nos UIDs
        chatRoomId = if (senderUid!! < receiverUid!!) {
            senderUid + receiverUid
        } else {
            receiverUid + senderUid
        }

        // Ouve as mensagens em tempo real
        listenerRegistration = db.collection("Chats")
            .document(chatRoomId!!)
            .collection("messages")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                messageList.clear()
                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }
            }

        // Envia mensagem
        sendButton.setOnClickListener {
            Log.d("porra","não foi clicado")
            val messageText = messageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val messageData = hashMapOf(
                    "message" to messageText,
                    "senderId" to senderUid,
                    "receiverId" to receiverUid,
                    "timestamp" to System.currentTimeMillis()
                )

                // Cria documento da sala se não existir
                db.collection("Chats")
                    .document(chatRoomId!!)
                    .get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            db.collection("Chats")
                                .document(chatRoomId!!)
                                .set(hashMapOf("users" to listOf(senderUid, receiverUid)))
                        }

                        // Adiciona a mensagem
                        db.collection("Chats")
                            .document(chatRoomId!!)
                            .collection("messages")
                            .add(messageData)
                            .addOnSuccessListener {
                                messageBox.text.clear()
                            }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
    }
}
