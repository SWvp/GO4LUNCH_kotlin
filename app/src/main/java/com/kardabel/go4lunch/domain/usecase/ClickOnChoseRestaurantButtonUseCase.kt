package com.kardabel.go4lunch.domain.usecase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Clock
import java.time.LocalDate

class ClickOnChoseRestaurantButtonUseCase(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val clock: Clock,
) {

    private fun getDayCollection(): CollectionReference {
        return firebaseFirestore.collection(LocalDate.now(clock).toString())
    }

    fun onRestaurantSelectedClick(
        restaurantId: String,
        restaurantName: String,
        restaurantAddress: String,
    ){

        if (firebaseAuth.currentUser != null){

            val userId = firebaseAuth.currentUser!!.uid
            val userName = firebaseAuth.currentUser!!.displayName

            val userGotRestaurant: MutableMap<String, Any> = HashMap()
            userGotRestaurant["restaurantId"] = restaurantId
            userGotRestaurant["restaurantName"] = restaurantName
            userGotRestaurant["userId"] = userId
            userGotRestaurant["userName"] = userName!!
            userGotRestaurant["restaurantAddress"] = restaurantAddress

            getDayCollection()
                .document(userId)
                .get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        if (task.result["restaurantId"] == restaurantId) {
                            task.result.reference.delete()
                        } else {
                            task.result.reference.set(userGotRestaurant)
                        }
                    }
                }
        }
    }
}