package com.kardabel.go4lunch.domain.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.kardabel.go4lunch.domain.pojo.FavoriteRestaurant

class FavoriteRestaurantsRepository {

    companion object {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_FAVORITE_RESTAURANTS = "favorite restaurants"
    }

    fun getFavoriteRestaurants(): LiveData<List<FavoriteRestaurant>> {

        val db = FirebaseFirestore.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val favoriteRestaurantsLiveData = MutableLiveData<List<FavoriteRestaurant>>()

        db.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_FAVORITE_RESTAURANTS)
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    Log.e("no favorite", error.message!!)
                    return@addSnapshotListener
                }
                val favoriteRestaurants: MutableList<FavoriteRestaurant> =
                    ArrayList()
                assert(value != null)
                for (document in value!!.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        favoriteRestaurants.add(document.document.toObject(
                            FavoriteRestaurant::class.java))
                    } else if (document.type == DocumentChange.Type.REMOVED) {
                        favoriteRestaurants.remove(document.document.toObject(
                            FavoriteRestaurant::class.java))
                    }
                }
                favoriteRestaurantsLiveData.setValue(favoriteRestaurants)
            }
        return favoriteRestaurantsLiveData
    }
}