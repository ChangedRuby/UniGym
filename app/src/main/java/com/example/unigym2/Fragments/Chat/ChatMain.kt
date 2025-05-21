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

        chatRoomId = if (senderUid!! < receiverUid!!) {
            senderUid + receiverUid
        } else {
            receiverUid + senderUid
        }

        val messagesRef = db.collection("Chats")
            .document(chatRoomId!!)
            .collection("messages")
            .orderBy("timestamp")

        // Listener para receber mensagens antigas e novas em tempo real
        listenerRegistration = messagesRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshots != null && !snapshots.isEmpty) {
                messageList.clear()
                for (doc in snapshots.documents) {
                    val msg = doc.toObject(Message::class.java)
                    if (msg != null) {
                        messageList.add(msg)
                    }
                }
                messageAdapter.notifyDataSetChanged()
                mainChatRecyclerView.scrollToPosition(messageList.size - 1)
            }
        }

        // Enviar nova mensagem
        sendButton.setOnClickListener {
            val msgText = messageBox.text.toString().trim()
            if (msgText.isNotEmpty()) {
                val msgMap = hashMapOf(
                    "message" to msgText,
                    "senderId" to senderUid,
                    "receiverId" to receiverUid,
                    "timestamp" to System.currentTimeMillis()
                )

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

    override fun onDestroyView() {
        super.onDestroyView()
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
    }
}
