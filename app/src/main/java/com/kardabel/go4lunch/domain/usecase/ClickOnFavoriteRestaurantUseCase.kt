package com.kardabel.go4lunch.domain.usecase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ClickOnFavoriteRestaurantUseCase(
    private val firebaseFirestore: FirebaseFirestore,
) {

    companion object{
        const val COLLECTION_USERS = "users"
        const val FAVORITE_RESTAURANTS = "favorite restaurants"
        const val RESTAURANT_NAME = "restaurantName"
        const val RESTAURANT_ID = "restaurantId"
    }

    fun onFavoriteRestaurantClick(
        restaurantId: String,
        restaurantName: String,
    ){

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val favoriteRestaurant: MutableMap<String, Any> = HashMap()
        favoriteRestaurant[RESTAURANT_ID] = restaurantId
        favoriteRestaurant[RESTAURANT_NAME] = restaurantName

        getDayCollection()
            .document(userId!!)
            .collection(ClickOnFavoriteRestaurantUseCase.FAVORITE_RESTAURANTS)
            .document(restaurantId)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    if (task.result.exists()) {
                        task.result.reference.delete()
                    } else {
                        task.result.reference.set(favoriteRestaurant)
                    }
                }
            }
    }

    private fun getDayCollection(): CollectionReference {
        return firebaseFirestore.collection(ClickOnFavoriteRestaurantUseCase.COLLECTION_USERS)
    }
}