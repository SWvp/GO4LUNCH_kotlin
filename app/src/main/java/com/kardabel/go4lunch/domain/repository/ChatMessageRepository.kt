package com.kardabel.go4lunch.domain.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.kardabel.go4lunch.domain.model.ChatMessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*

class ChatMessageRepository {

    companion object {
        const val COLLECTION_CHAT = "chat"
    }

    fun getChatMessages(workmateId: String): LiveData<List<ChatMessageModel>> {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val chatMessagesMutableLiveData = MutableLiveData<List<ChatMessageModel>>()
        val chatMessages: MutableSet<ChatMessageModel> = HashSet()

        // CREATE A LIST OF USER TO SORT THEM,
        // IT WILL GIVE THE INDEX FOR DOCUMENT AND COLLECTION
        val ids: MutableList<String> = ArrayList()
        ids.add(userId)
        ids.add(workmateId)
        ids.sort()

        // LISTEN THE CHAT COLLECTION
        db.collection(COLLECTION_CHAT)
            .document(ids[0] + "_" + ids[1])
            .collection(ids[0] + "_" + ids[1])
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("messages error", error.message!!)
                    return@addSnapshotListener
                }
                assert(value != null)
                for (document in value!!.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        chatMessages.add(document.document.toObject(ChatMessageModel::class.java))
                    }
                }
                val chatMessageModels: List<ChatMessageModel> = ArrayList(chatMessages)
                chatMessagesMutableLiveData.setValue(chatMessageModels)
            }
        return chatMessagesMutableLiveData
    }
}