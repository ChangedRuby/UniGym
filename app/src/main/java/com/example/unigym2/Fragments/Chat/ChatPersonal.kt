package com.example.unigym2.Fragments.Chat

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaUsuariosAdapter // Changed Adapter type
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaUsuariosItem
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatPersonal : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: ListaUsuariosAdapter // Changed Adapter type
    lateinit var communicator: Communicator
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var searchView: SearchView
    private lateinit var userListTabLayout: TabLayout // Added TabLayout

    private lateinit var originalItemArray: MutableList<ListaUsuariosItem>
    private lateinit var displayedItemArray: MutableList<ListaUsuariosItem>
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Current Personal ID from FirebaseAuth (onCreate): $currentUserId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_chat_trainer, container, false) // Assuming fragment_chat_trainer has a TabLayout R.id.userListTabLayout
        db = FirebaseFirestore.getInstance()
        recyclerView = v.findViewById(R.id.listaUsuariosRecyclerview) // Ensure this ID is correct in fragment_chat_trainer
        communicator = activity as Communicator
        searchView = v.findViewById(R.id.conversasSearchView) // Ensure this ID is correct
        userListTabLayout = v.findViewById(R.id.chatsPersonalTabLayout) // Make sure this ID exists in your XML layout

        originalItemArray = mutableListOf()
        displayedItemArray = mutableListOf()

        setupSearchView()
        setupTabLayout() // Setup for tabs

        // Initial tab load will be triggered by setupTabLayout's onTabSelected
        // or we can explicitly load the first tab here.
        // For consistency with ChatUser, relying on onTabSelected for first load.
        // If no tab is selected by default by TabLayout, we might need to:
        // userListTabLayout.getTabAt(0)?.select() or loadAlunosTab() directly.
        // Let's assume TabLayout selects the first tab by default.
        // If not, an explicit call might be needed in onViewCreated or here.
        if (originalItemArray.isEmpty() && displayedItemArray.isEmpty()) {
            loadAlunosTab() // Load the default tab explicitly
        }


        val layoutManager = LinearLayoutManager(context)
        adapter = ListaUsuariosAdapter(displayedItemArray, communicator, parentFragmentManager)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        return v
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupTabLayout() {
        // Add tabs dynamically or ensure they are in XML for userListTabLayout
        if (userListTabLayout.tabCount == 0) {
            userListTabLayout.addTab(userListTabLayout.newTab().setText("Alunos"))
            userListTabLayout.addTab(userListTabLayout.newTab().setText("Conversas"))
        }

        userListTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Tab 'Alunos' selected")
                        loadAlunosTab()
                    }
                    1 -> {
                        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Tab 'Conversas' selected")
                        loadPersonalConversasTab()
                    }
                }
                searchView.setQuery("", false)
                searchView.clearFocus()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadAlunosTab() {
        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Loading Alunos tab")
        createItems(fetchAlunos = true)
    }

    private fun loadPersonalConversasTab() {
        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Loading Personal's Conversas tab")
        createItems(fetchAlunos = false)
    }

    private fun filter(query: String) {
        displayedItemArray.clear()
        val lowerCaseQuery = (query as java.lang.String).toLowerCase(Locale.ROOT)

        if (lowerCaseQuery.isEmpty()) {
            displayedItemArray.addAll(originalItemArray)
        } else {
            for (item in originalItemArray) {
                val itemNameLowerCase = (item.name as java.lang.String?)?.toLowerCase(Locale.ROOT) ?: ""
                if (itemNameLowerCase.contains(lowerCaseQuery)) {
                    displayedItemArray.add(item)
                }
            }
        }
        displayedItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
        adapter.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatPersonal().apply { // Corrected to return ChatPersonal
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createItems(fetchAlunos: Boolean) {
        communicator.showLoadingOverlay()
        originalItemArray.clear()
        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: createItems called. fetchAlunos: $fetchAlunos. Current Personal ID: $currentUserId")

        val brokImageBitmap = context?.resources?.let { res ->
            BitmapFactory.decodeResource(res, R.drawable.brok_logo)
        } ?: run {
            Log.e("ChatPersonal", "CHAT_PERSONAL_DEBUG: Failed to decode Brok_Logo")
            null
        }

        if (fetchAlunos) { // Tab "Alunos"
            if (brokImageBitmap != null) {
                originalItemArray.add(
                    ListaUsuariosItem(name = "Brok", userId = "BROK_AI_AGENT", image = brokImageBitmap)
                )
                Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Added Brok AI Agent to originalItemArray (Alunos tab).")
            }

            db.collection("Usuarios")
                .whereEqualTo("isPersonal", false) // Fetch non-personals
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Fetched ${documents.size()} non-personal user documents for Alunos tab.")
                    handleFetchedUserDocuments(documents.toMutableList())
                }.addOnFailureListener { exception ->
                    Log.e("ChatPersonal", "CHAT_PERSONAL_DEBUG: Error getting non-personal user documents: ", exception)
                    Handler(Looper.getMainLooper()).post {
                        filter("")
                        communicator.hideLoadingOverlay()
                    }
                }
        } else { // Tab "Conversas" for the Personal Trainer
            if (currentUserId.isNullOrEmpty()) {
                Log.e("ChatPersonal", "CHAT_PERSONAL_DEBUG: Current Personal ID is null. Cannot fetch conversations.")
                Handler(Looper.getMainLooper()).post { filter(""); communicator.hideLoadingOverlay() }
                return
            }
            Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Starting fetch for Personal's conversations from 'Chats' collection.")

            val conversedUserIds = mutableSetOf<String>()
            db.collection("Chats").get()
                .addOnSuccessListener { chatDocuments ->
                    Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Successfully fetched ${chatDocuments.size()} chat documents from 'Chats'.")
                    val fetchedDocIds = chatDocuments.documents.map { it.id }
                    Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: All fetched chatDoc IDs for Personal's Conversas: $fetchedDocIds")

                    if (chatDocuments.isEmpty) {
                        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: 'Chats' collection is empty for Personal's Conversas tab.")
                    }

                    for (chatDoc in chatDocuments) {
                        val chatRoomId = chatDoc.id
                        if (chatRoomId.contains(currentUserId!!)) {
                            val otherUserId = chatRoomId.replace(currentUserId!!, "")
                            if (otherUserId.isNotEmpty()) {
                                conversedUserIds.add(otherUserId)
                            }
                        }
                    }

                    if (conversedUserIds.isEmpty()) {
                        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: No relevant conversed users found for Personal. Updating UI.")
                        Handler(Looper.getMainLooper()).post { filter(""); communicator.hideLoadingOverlay() }
                        return@addOnSuccessListener
                    }

                    Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Personal's conversed participants (incl. BROK if any): $conversedUserIds")

                    if (conversedUserIds.contains("BROK_AI_AGENT") && brokImageBitmap != null) {
                        if (originalItemArray.none { it.userId == "BROK_AI_AGENT" }) {
                            originalItemArray.add(
                                ListaUsuariosItem(name = "Brok", userId = "BROK_AI_AGENT", image = brokImageBitmap)
                            )
                            Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Manually added Brok to originalItemArray (Personal's Conversas).")
                        }
                    }

                    val userIdsToFetchFromFirestore = conversedUserIds.filter { it != "BROK_AI_AGENT" }.toList()

                    if (userIdsToFetchFromFirestore.isEmpty()) {
                        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: No actual user IDs to fetch from Firestore for Personal's Conversas. Updating UI.")
                        Handler(Looper.getMainLooper()).post { filter(""); communicator.hideLoadingOverlay() }
                        return@addOnSuccessListener
                    }

                    Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Proceeding to fetch details from Firestore for Personal's conversed users: $userIdsToFetchFromFirestore")
                    val fetchedConversedDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
                    var usersFetchedCount = 0

                    for (userIdToFetch in userIdsToFetchFromFirestore) {
                        db.collection("Usuarios").document(userIdToFetch).get()
                            .addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: Fetched userDoc for '${userDoc.getString("name")}' (Personal's Conversas).")
                                    fetchedConversedDocs.add(userDoc) // No isPersonal filter here, show everyone chatted with
                                } else {
                                    Log.w("ChatPersonal", "CHAT_PERSONAL_DEBUG: User document for '$userIdToFetch' does not exist (Personal's Conversas).")
                                }
                                usersFetchedCount++
                                if (usersFetchedCount == userIdsToFetchFromFirestore.size) {
                                    Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: All conversed user details fetch attempts completed for Personal. Calling handleFetchedUserDocuments.")
                                    handleFetchedUserDocuments(fetchedConversedDocs)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ChatPersonal", "CHAT_PERSONAL_DEBUG: Error fetching conversed user details for '$userIdToFetch' (Personal's Conversas): ", e)
                                usersFetchedCount++
                                if (usersFetchedCount == userIdsToFetchFromFirestore.size) {
                                    handleFetchedUserDocuments(fetchedConversedDocs)
                                }
                            }
                    }
                }.addOnFailureListener { exception ->
                    Log.e("ChatPersonal", "CHAT_PERSONAL_DEBUG: CRITICAL Error getting chat documents for Personal's Conversas: ", exception)
                    Handler(Looper.getMainLooper()).post { filter(""); communicator.hideLoadingOverlay() }
                }
        }
    }

    private fun handleFetchedUserDocuments(documents: MutableList<com.google.firebase.firestore.DocumentSnapshot>) {
        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: handleFetchedUserDocuments called with ${documents.size} documents. Current originalItemArray size: ${originalItemArray.size}")

        if (documents.isEmpty() && originalItemArray.all { it.userId == "BROK_AI_AGENT" || (it.userId as java.lang.String).isEmpty() }) {
            if (originalItemArray.isEmpty()) {
                Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: handleFetchedUserDocuments: No documents and original array effectively empty. Hiding overlay.")
            } else {
                Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: handleFetchedUserDocuments: No new documents from Firestore, Brok might be present. Updating UI.")
            }
            Handler(Looper.getMainLooper()).post { filter(""); communicator.hideLoadingOverlay() }
            return
        }

        var itemsProcessed = 0
        val totalItemsToProcess = documents.size
        val fetchedFirestoreUserItems = mutableListOf<ListaUsuariosItem>()

        if (totalItemsToProcess == 0 && originalItemArray.isNotEmpty()) {
            Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: handleFetchedUserDocuments: No new user documents from Firestore, but originalItemArray not empty (e.g. Brok). Updating UI.")
            Handler(Looper.getMainLooper()).post {
                originalItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
                filter("")
                communicator.hideLoadingOverlay()
            }
            return
        }
        if (totalItemsToProcess == 0 && originalItemArray.isEmpty()) {
            Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: handleFetchedUserDocuments: Total items to process is 0 and originalItemArray is empty. Hiding overlay.")
            Handler(Looper.getMainLooper()).post { filter(""); communicator.hideLoadingOverlay() }
            return
        }

        for (document in documents) {
            val userId = document.getString("id").orEmpty()
            val userEmail = document.getString("email").orEmpty()
            val userName = document.getString("name").orEmpty()

            if (userId == "BROK_AI_AGENT" && originalItemArray.any { it.userId == "BROK_AI_AGENT" }) {
                itemsProcessed++
                if (itemsProcessed == totalItemsToProcess) { finalizeAndRefreshList(fetchedFirestoreUserItems) }
                continue
            }

            if (userId.isNotEmpty() && userName.isNotEmpty()) {
                if (originalItemArray.none { it.userId == userId }) { // Avoid duplicates if already added (e.g. Brok)
                    AvatarManager.getUserAvatar(userId, userEmail, userName, 80, lifecycleScope) { bitmap ->
                        val newItem = ListaUsuariosItem(name = userName, userId = userId, image = bitmap)
                        fetchedFirestoreUserItems.add(newItem)
                        itemsProcessed++
                        if (itemsProcessed == totalItemsToProcess) {
                            finalizeAndRefreshList(fetchedFirestoreUserItems)
                        }
                    }
                } else {
                    itemsProcessed++
                    if (itemsProcessed == totalItemsToProcess) {
                        finalizeAndRefreshList(fetchedFirestoreUserItems)
                    }
                }
            } else {
                Log.w("ChatPersonal", "CHAT_PERSONAL_DEBUG: handleFetchedUserDocuments: Skipping user with Empty ID or name: ID = '$userId', Name = '$userName'")
                itemsProcessed++
                if (itemsProcessed == totalItemsToProcess) {
                    finalizeAndRefreshList(fetchedFirestoreUserItems)
                }
            }
        }
        // If loop finishes due to all items being skipped or already present, and not all itemsProcessed.
        if (itemsProcessed == totalItemsToProcess && fetchedFirestoreUserItems.isEmpty() && documents.isNotEmpty()) {
            finalizeAndRefreshList(fetchedFirestoreUserItems)
        }
    }

    private fun finalizeAndRefreshList(newlyFetchedItems: List<ListaUsuariosItem>) {
        originalItemArray.addAll(newlyFetchedItems)
        originalItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
        originalItemArray = originalItemArray.distinctBy { it.userId }.toMutableList()

        Log.d("ChatPersonal", "CHAT_PERSONAL_DEBUG: finalizeAndRefreshList: Final originalItemArray size: ${originalItemArray.size}. Updating RecyclerView.")
        Handler(Looper.getMainLooper()).post {
            filter("")
            communicator.hideLoadingOverlay()
        }
    }
}