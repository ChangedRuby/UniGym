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
// import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unigym2.Activities.Communicator
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisAdapter
import com.example.unigym2.Fragments.Chat.Recyclerviews.ListaPersonaisItem
import com.example.unigym2.Managers.AvatarManager
import com.example.unigym2.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.firestore.Query

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: ListaPersonaisAdapter
    lateinit var communicator: Communicator
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore // Instance used by your existing logic
    private lateinit var searchView: SearchView
    private lateinit var chatsTabLayout: TabLayout

    private lateinit var originalItemArray: MutableList<ListaPersonaisItem>
    private lateinit var displayedItemArray: MutableList<ListaPersonaisItem>

    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: Current user ID from FirebaseAuth (onCreate): $currentUserId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_chat_user, container, false)
        db = FirebaseFirestore.getInstance() // Your existing db instance
        recyclerView = v.findViewById(R.id.listaUsuariosRecyclerview)
        communicator = activity as Communicator
        searchView = v.findViewById(R.id.conversasSearchView)
        chatsTabLayout = v.findViewById(R.id.chatsTabLayout)

        originalItemArray = mutableListOf()
        displayedItemArray = mutableListOf()

        setupSearchView()
        setupTabLayout()

        // Removed loadTreinadoresTab() from here to let onViewCreated handle initial load
        // or run the test first. We will call it after the test or in onResume.

        val layoutManager = LinearLayoutManager(context)
        adapter = ListaPersonaisAdapter(displayedItemArray, communicator, parentFragmentManager)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        return v
    }

    // This method is called immediately after onCreateView() has returned,
    // but before any saved state has been restored in to the view.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ChatUser_TEST_QUERY", "onViewCreated: Running diagnostic query.")
        runDiagnosticFirestoreQuery() // Run the diagnostic query

        // After the diagnostic query (or if you want to load normally regardless):
        // You might want to delay this or ensure it doesn't interfere with the test's async nature
        // For now, let's let the tabs handle their loading.
        // Consider calling loadTreinadoresTab() here if it's the default first tab
        // and if the diagnostic is just for one-time check.
        // If the first tab is "Treinadores", it will be loaded when setupTabLayout makes the first selection.
        // Or, if no tab is selected by default, explicitly select one.

        // If tabs are set up and one is selected by default, its onTabSelected will call load...
        // If not, you might need to explicitly load the default tab content here or in onResume
        // For now, let's assume the TabLayout's default selection will trigger the first load.
        // To be safe, if no tab is pre-selected and you expect "Treinadores" to load:
        if (chatsTabLayout.selectedTabPosition == -1 || chatsTabLayout.selectedTabPosition == 0) {
            Log.d("ChatUser", "onViewCreated: Defaulting to Treinadores tab load if needed.")
            //loadTreinadoresTab() // Be careful with calling this if test is async
        }
        // The initial loadTreinadoresTab() call in onCreateView might be better
        // or rely on onTabSelected of the default tab.
        // For now, the original call in onCreateView is restored below for normal operation,
        // the diagnostic is separate.
        if (originalItemArray.isEmpty() && displayedItemArray.isEmpty()) { // Simple check to see if initial load happened
            loadTreinadoresTab() // Restore initial load if it was removed from onCreateView
        }
    }

    private fun runDiagnosticFirestoreQuery() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            Log.e("ChatUser_TEST_QUERY", "User not authenticated! Cannot perform test.")
            return
        }
        Log.d("ChatUser_TEST_QUERY", "User authenticated: ${auth.currentUser!!.uid}. Proceeding with diagnostic query.")

        // Use a potentially "fresh" instance for the test, though it usually points to the same singleton
        val testFirestoreDb = FirebaseFirestore.getInstance()
        Log.d("ChatUser_TEST_QUERY", "Attempting to fetch ALL documents from 'Chats' collection with test instance...")

        testFirestoreDb.collection("Chats").get()
            .addOnSuccessListener { documents ->
                Log.i("ChatUser_TEST_QUERY", "DIAGNOSTIC SUCCESS: Fetched ${documents.size()} documents from 'Chats' collection.")
                if (documents.isEmpty) {
                    Log.i("ChatUser_TEST_QUERY", "DIAGNOSTIC: The 'Chats' collection appears empty based on this query.")
                } else {
                    val documentIds = documents.documents.map { it.id }
                    Log.i("ChatUser_TEST_QUERY", "DIAGNOSTIC: Fetched Document IDs: $documentIds")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChatUser_TEST_QUERY", "DIAGNOSTIC FAILURE: Error fetching 'Chats' collection: $e", e)
            }
    }


    private fun setupSearchView() {
        // ... (your existing setupSearchView code)
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
        // ... (your existing setupTabLayout code)
        chatsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: Tab 'Treinadores' selected")
                        loadTreinadoresTab()
                    }
                    1 -> {
                        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: Tab 'Conversas' selected")
                        loadConversasTab()
                    }
                }
                searchView.setQuery("", false)
                searchView.clearFocus()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { /* Not implemented */ }
            override fun onTabReselected(tab: TabLayout.Tab?) { /* Not implemented */ }
        })
    }

    private fun loadTreinadoresTab() {
        // ... (your existing loadTreinadoresTab code)
        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: Loading Treinadores tab")
        createItems(allTrainers = true)
    }

    private fun loadConversasTab() {
        // ... (your existing loadConversasTab code)
        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: Loading Conversas tab")
        createItems(allTrainers = false)
    }

    private fun filter(query: String) {
        // ... (your existing filter code with explicit casts)
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

    // ... (Companion object newInstance)
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun createItems(allTrainers: Boolean) {
        // ... (your existing createItems code, including the detailed logging for Chats.get() in the 'else' block)
        communicator.showLoadingOverlay()
        originalItemArray.clear() // Clear previous items specific to the tab
        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: createItems called. allTrainers: $allTrainers. Current User ID: $currentUserId")

        val brokImageBitmap = context?.resources?.let { res ->
            BitmapFactory.decodeResource(res, R.drawable.brok_logo)
        } ?: run {
            Log.e("ChatUser", "CONVERSAS_TAB_DEBUG: Failed to decode Brok_Logo")
            null
        }

        if (allTrainers) {
            // Add Brok for "Treinadores" tab
            if (brokImageBitmap != null) {
                originalItemArray.add(
                    ListaPersonaisItem(name = "Brok", userId = "BROK_AI_AGENT", image = brokImageBitmap)
                )
                Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: Added Brok AI Agent to originalItemArray (Treinadores tab).")
            }

            val userCollection = db.collection("Usuarios")
            userCollection.whereEqualTo("isPersonal", true).get().addOnSuccessListener { documents ->
                Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: Fetched ${documents.size()} trainer documents for Treinadores tab.")
                handleFetchedDocuments(documents.toMutableList()) // Pass a mutable list
            }.addOnFailureListener { exception ->
                Log.e("ChatUser", "CONVERSAS_TAB_DEBUG: Error getting trainer documents for Treinadores tab: ", exception)
                Handler(Looper.getMainLooper()).post {
                    filter("")
                    communicator.hideLoadingOverlay()
                }
            }
        } else { // This is the "Conversas" tab logic (allTrainers == false)
            if (currentUserId.isNullOrEmpty()) {
                Log.e("ChatUser", "CONVERSAS_TAB_DEBUG: Current User ID is null. Cannot fetch conversations. Hiding overlay.")
                Handler(Looper.getMainLooper()).post {
                    filter("")
                    communicator.hideLoadingOverlay()
                }
                return
            }
            // This is your existing logic that we know is problematic
            Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Starting fetch for conversations from 'Chats' collection.")

            val conversedUserIds = mutableSetOf<String>()
            // Using 'db' which is the instance initialized in onCreateView for the fragment's main operations
            db.collection("Chats")
                .get()
                .addOnSuccessListener { chatDocuments ->
                    // *** DETAILED LOGGING FOR CHAT DOCUMENT RETRIEVAL (EXISTING) ***
                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Successfully fetched from 'Chats' collection (Conversas Tab).")
                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Number of chat documents fetched: ${chatDocuments.size()}")
                    val fetchedDocIds = chatDocuments.documents.map { it.id }
                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] All fetched chatDoc IDs for Conversas: $fetchedDocIds")

                    if (chatDocuments.isEmpty) {
                        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] 'Chats' collection (or query result) is empty for Conversas tab.")
                    }

                    for (chatDoc in chatDocuments) {
                        val chatRoomId = chatDoc.id
                        if (chatRoomId.contains(currentUserId!!)) {
                            val otherUserId = chatRoomId.replace(currentUserId!!, "")
                            if (otherUserId.isNotEmpty()) {
                                conversedUserIds.add(otherUserId)
                            } else {
                                Log.w("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Other user ID empty after replacement for chatRoomId: $chatRoomId")
                            }
                        }
                    }

                    if (conversedUserIds.isEmpty()) {
                        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] No relevant conversed users found after processing chat documents. Updating UI.")
                        Handler(Looper.getMainLooper()).post {
                            filter("")
                            communicator.hideLoadingOverlay()
                        }
                        return@addOnSuccessListener
                    }

                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Total conversed participants (incl. BROK if any): $conversedUserIds")

                    if (conversedUserIds.contains("BROK_AI_AGENT") && brokImageBitmap != null) {
                        if (originalItemArray.none { it.userId == "BROK_AI_AGENT" }) {
                            originalItemArray.add(
                                ListaPersonaisItem(name = "Brok", userId = "BROK_AI_AGENT", image = brokImageBitmap)
                            )
                            Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Manually added Brok AI Agent to originalItemArray (Conversas tab - chat detected).")
                        } else {
                            Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Brok AI Agent already in originalItemArray (Conversas tab - chat detected).")
                        }
                    }

                    val userIdsToFetchFromFirestore = conversedUserIds.filter { it != "BROK_AI_AGENT" }.toList()

                    if (userIdsToFetchFromFirestore.isEmpty()) {
                        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] No actual user IDs to fetch from Firestore. Updating UI.")
                        Handler(Looper.getMainLooper()).post {
                            filter("")
                            communicator.hideLoadingOverlay()
                        }
                        return@addOnSuccessListener
                    }

                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Proceeding to fetch details from Firestore for users: $userIdsToFetchFromFirestore")
                    val fetchedConversedDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
                    var usersFetchedCount = 0

                    for (userIdToFetch in userIdsToFetchFromFirestore) {
                        db.collection("Usuarios").document(userIdToFetch).get()
                            .addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Fetched userDoc for '$userIdToFetch'. Name: ${userDoc.getString("name")}, isPersonal: ${userDoc.getBoolean("isPersonal")}")
                                    fetchedConversedDocs.add(userDoc)
                                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Added doc for '$userIdToFetch' to fetchedConversedDocs for 'Conversas' tab.")
                                } else {
                                    Log.w("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] User document for '$userIdToFetch' does not exist in 'Usuarios' collection.")
                                }
                                usersFetchedCount++
                                if (usersFetchedCount == userIdsToFetchFromFirestore.size) {
                                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] All conversed user details fetch attempts completed. Calling handleFetchedDocuments. Count: ${fetchedConversedDocs.size}")
                                    handleFetchedDocuments(fetchedConversedDocs)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] Error fetching conversed user details for '$userIdToFetch': ", e)
                                usersFetchedCount++
                                if (usersFetchedCount == userIdsToFetchFromFirestore.size) {
                                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] All conversed user details fetch attempts completed (some with errors). Calling handleFetchedDocuments.")
                                    handleFetchedDocuments(fetchedConversedDocs)
                                }
                            }
                    }
                }.addOnFailureListener { exception ->
                    Log.e("ChatUser", "CONVERSAS_TAB_DEBUG: [REGULAR LOGIC] CRITICAL Error getting chat documents from 'Chats' collection: ", exception)
                    Handler(Looper.getMainLooper()).post {
                        filter("")
                        communicator.hideLoadingOverlay()
                    }
                }
        }
    }

    // ... (handleFetchedDocuments and finalizeAndRefreshList with explicit casts)
    private fun handleFetchedDocuments(documents: MutableList<com.google.firebase.firestore.DocumentSnapshot>) {
        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments called with ${documents.size} documents. Current originalItemArray size: ${originalItemArray.size}")

        if (documents.isEmpty() && originalItemArray.all { it.userId == "BROK_AI_AGENT" || (it.userId as java.lang.String).isEmpty() }) {
            if (originalItemArray.isEmpty()) {
                Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments: No documents to process and original array is effectively empty of fetched users. Hiding overlay.")
            } else {
                Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments: No new documents from Firestore, Brok might be present. Updating UI.")
            }
            Handler(Looper.getMainLooper()).post {
                filter("")
                communicator.hideLoadingOverlay()
            }
            return
        }

        var itemsProcessed = 0
        val totalItemsToProcess = documents.size
        val fetchedFirestoreUserItems = mutableListOf<ListaPersonaisItem>()

        if (totalItemsToProcess == 0 && originalItemArray.isNotEmpty()) {
            Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments: No new user documents from Firestore to process, but originalItemArray not empty. Updating UI.")
            Handler(Looper.getMainLooper()).post {
                originalItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
                filter("")
                communicator.hideLoadingOverlay()
            }
            return
        }
        if (totalItemsToProcess == 0 && originalItemArray.isEmpty()) {
            Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments: Total items to process is 0 and originalItemArray is empty. Hiding overlay.")
            Handler(Looper.getMainLooper()).post {
                filter("")
                communicator.hideLoadingOverlay()
            }
            return
        }

        for (document in documents) {
            val userId = document.getString("id").orEmpty()
            val userEmail = document.getString("email").orEmpty()
            val userName = document.getString("name").orEmpty()

            if (userId == "BROK_AI_AGENT" && originalItemArray.any { it.userId == "BROK_AI_AGENT" }) {
                Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments: Brok AI Agent (from Firestore doc) already handled/present, skipping this document.")
                itemsProcessed++
                if (itemsProcessed == totalItemsToProcess) {
                    finalizeAndRefreshList(fetchedFirestoreUserItems)
                }
                continue
            }

            if (userId.isNotEmpty() && userName.isNotEmpty()) {
                if (originalItemArray.none { it.userId == userId }) {
                    AvatarManager.getUserAvatar(userId, userEmail, userName, 40, lifecycleScope) { bitmap ->
                        val newItem = ListaPersonaisItem(name = userName, userId = userId, image = bitmap)
                        fetchedFirestoreUserItems.add(newItem)
                        itemsProcessed++
                        if (itemsProcessed == totalItemsToProcess) {
                            finalizeAndRefreshList(fetchedFirestoreUserItems)
                        }
                    }
                } else {
                    Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments: User '$userName' (ID: '$userId') already in originalItemArray, skipping.")
                    itemsProcessed++
                    if (itemsProcessed == totalItemsToProcess) {
                        finalizeAndRefreshList(fetchedFirestoreUserItems)
                    }
                }
            } else {
                Log.w("ChatUser", "CONVERSAS_TAB_DEBUG: handleFetchedDocuments: Skipping user with Empty ID or name from Firestore: ID = '$userId', Name = '$userName'")
                itemsProcessed++
                if (itemsProcessed == totalItemsToProcess) {
                    finalizeAndRefreshList(fetchedFirestoreUserItems)
                }
            }
        }
    }

    private fun finalizeAndRefreshList(newlyFetchedItems: List<ListaPersonaisItem>) {
        originalItemArray.addAll(newlyFetchedItems)
        originalItemArray.sortBy { (it.name as java.lang.String?)?.toLowerCase(Locale.ROOT) }
        originalItemArray = originalItemArray.distinctBy { it.userId }.toMutableList()

        Log.d("ChatUser", "CONVERSAS_TAB_DEBUG: finalizeAndRefreshList: All items processed/added. Final originalItemArray size: ${originalItemArray.size}. Updating RecyclerView.")
        Handler(Looper.getMainLooper()).post {
            filter("")
            communicator.hideLoadingOverlay()
        }
    }
}