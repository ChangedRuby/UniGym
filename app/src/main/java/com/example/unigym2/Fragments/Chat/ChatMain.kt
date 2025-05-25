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
import com.google.firebase.firestore.DocumentChange
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
    private var listenerRegistration: ListenerRegistration? = null

    private var chatRoomId: String? = null
    private var receiverUid: String? = null
    private var senderUid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_main, container, false)

        // Inicializa os componentes
        chatName = view.findViewById(R.id.chatName)
        backBtn = view.findViewById(R.id.goBackBtn)
        communicator = activity as Communicator

        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        db = FirebaseFirestore.getInstance()

        mainChatRecyclerView = view.findViewById(R.id.mainChat_RecycleView)
        messageBox = view.findViewById(R.id.messageBox)
        sendButton = view.findViewById(R.id.enviarButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        mainChatRecyclerView.layoutManager = LinearLayoutManager(context)
        mainChatRecyclerView.adapter = messageAdapter

        // Ouve o Bundle correto
        parentFragmentManager.setFragmentResultListener("chat_name_key", viewLifecycleOwner) { _, bundle ->
            val userName = bundle.getString("name", "Unknown Username")
            val UID = bundle.getString("receiverID")  // Corrigido: "receiverID" com I maiúsculo
            chatName.text = userName
            receiverUid = UID
            setupChat()
        }

        // Enviar mensagem ao clicar
        sendButton.setOnClickListener {
            val msgText = messageBox.text.toString().trim()
            if (msgText.isNotEmpty() && chatRoomId != null) {
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

        backBtn.setOnClickListener {
            communicator.replaceFragment(if (communicator.getMode()) ChatPersonal() else ChatUser())
        }

        return view
    }

    private fun setupChat() {
        if (senderUid.isNullOrEmpty() || receiverUid.isNullOrEmpty()) {
            return
        }

        // Cria um chatRoomId único baseado nos dois UIDs
        chatRoomId = listOf(senderUid!!, receiverUid!!).sorted().joinToString("")

        val messagesRef = db.collection("Chats")
            .document(chatRoomId!!)
            .collection("messages")
            .orderBy("timestamp")

        // Carrega mensagens antigas
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

        // Escuta novas mensagens em tempo real
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

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
    }
}
