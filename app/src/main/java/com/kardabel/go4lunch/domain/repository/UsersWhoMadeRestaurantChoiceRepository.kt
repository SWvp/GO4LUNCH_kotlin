package com.kardabel.go4lunch.domain.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.kardabel.go4lunch.domain.model.UserWhoMadeRestaurantChoice
import java.time.Clock
import java.time.LocalDate

class UsersWhoMadeRestaurantChoiceRepository(
    private val clock: Clock,
) {

    private val db = FirebaseFirestore.getInstance()

    fun getWorkmatesWhoMadeRestaurantChoice(): LiveData<List<UserWhoMadeRestaurantChoice>> {

        val userModelMutableLiveData = MutableLiveData<List<UserWhoMadeRestaurantChoice>>()

        val today = LocalDate.now(clock)

        val usersWithRestaurant = mutableListOf<UserWhoMadeRestaurantChoice>()

        db.collection(today.toString())
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("restaurant choice error", error.message!!)
                    return@addSnapshotListener
                }
                assert(value != null)
                for (document in value!!.documentChanges) {

                    Log.d("pipo",
                        "onEvent() called with: value = [" + document.document.toObject(UserWhoMadeRestaurantChoice::class.java) + "], error = [" + null + "]")

                    when (document.type) {
                        DocumentChange.Type.ADDED -> {
                            usersWithRestaurant.add(document.document.toObject(
                                UserWhoMadeRestaurantChoice::class.java))
                        }
                        DocumentChange.Type.MODIFIED -> {
                            usersWithRestaurant.removeIf { user ->
                                user.userId == document.document.toObject(UserWhoMadeRestaurantChoice::class.java).userId
                            }
                            usersWithRestaurant.add(document.document.toObject(UserWhoMadeRestaurantChoice::class.java))
                        }
                        DocumentChange.Type.REMOVED -> {
                            usersWithRestaurant.remove(document.document.toObject(UserWhoMadeRestaurantChoice::class.java))
                        }
                    }
                }
                userModelMutableLiveData.setValue(usersWithRestaurant)
            }
        return userModelMutableLiveData
    }
}