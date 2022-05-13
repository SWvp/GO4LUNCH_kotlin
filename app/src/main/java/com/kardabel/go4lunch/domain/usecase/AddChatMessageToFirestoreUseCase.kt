package com.kardabel.go4lunch.domain.usecase

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddChatMessageToFirestoreUseCase(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val clock: Clock,
) {

    companion object {
        const val MESSAGE = "message"
        const val SENDER = "sender"
        const val DATE = "date"
        const val TIMESTAMP = "timestamp"
        const val COLLECTION_CHAT = "chat"
    }

    private fun getChatCollection(): CollectionReference {
        return firebaseFirestore.collection(COLLECTION_CHAT)
    }

    fun createChatMessage(
        message: String,
        workmateId: String,
    ){

        val userId = firebaseAuth.currentUser!!.uid

        // CREATE A LIST OF USER TO SORT THEM
        val ids: MutableList<String> = ArrayList()
        ids.add(userId)
        ids.add(workmateId)
        ids.sort()

        // RETRIEVE THE CURRENT DATE AND TIME, AND FORMAT TO HUMAN READABLE
        val currentDateTime = LocalDateTime.now(clock)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd à HH:mm:ss")
        val formatDateTime = currentDateTime.format(formatter)

        // CREATE MAP TO SEND TO FIRESTORE
        val chatMessage: MutableMap<String, Any> = HashMap()
        chatMessage[MESSAGE] = message
        chatMessage[SENDER] = userId
        chatMessage[DATE] = formatDateTime
        chatMessage[TIMESTAMP] = Instant.now(clock).toEpochMilli() // HERE IS THE TIMESTAMP NEEDED TO SORT THE CHAT MESSAGE

        // CREATE MESSAGE IN DATA BASE
        getChatCollection()
            .document(ids[0] + "_" + ids[1])
            .collection(ids[0] + "_" + ids[1])
            .add(chatMessage)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG,
                    "DocumentSnapshot written with message: $message")
            }
            .addOnFailureListener { e: Exception? ->
                Log.d(ContentValues.TAG,
                    "Error adding document",
                    e)
            }
    }
}