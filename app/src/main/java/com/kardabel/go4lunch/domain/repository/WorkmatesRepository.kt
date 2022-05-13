package com.kardabel.go4lunch.domain.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.kardabel.go4lunch.domain.model.UserModel

class WorkmatesRepository {

    companion object {
        const val USERS = "users"
        const val USER_NAME = "userName"
    }

    fun getWorkmates(): LiveData<List<UserModel>> {

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()

        val userModelMutableLiveData = MutableLiveData<List<UserModel>>()

        // WITH SET, WE ENSURE THERE IS NO DUPLICATE, FOR EXAMPLE WHEN ANOTHER USER CHANGE NAME FIELD
        val workmates: MutableSet<UserModel> = HashSet()

        db.collection(USERS)
            .orderBy(USER_NAME)
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("Firestore error", error.message!!)
                    return@addSnapshotListener
                }
                assert(value != null)
                for (document in value!!.documentChanges) {
                    val (uid) = document.document.toObject(UserModel::class.java)
                    if (userId != uid) {
                        if (document.type == DocumentChange.Type.ADDED ||
                            document.type == DocumentChange.Type.MODIFIED
                        ) {
                            workmates.add(document.document.toObject(UserModel::class.java))
                        }
                    }
                }
                val workmatesList: List<UserModel> =
                    ArrayList(workmates)
                userModelMutableLiveData.setValue(workmatesList)
            }
        return userModelMutableLiveData
    }
}