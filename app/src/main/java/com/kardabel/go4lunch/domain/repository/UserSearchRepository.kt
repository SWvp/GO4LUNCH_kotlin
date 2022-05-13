package com.kardabel.go4lunch.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class UserSearchRepository {

    private val searchViewResultLiveData = MutableLiveData<String>()

    fun usersSearch(restaurantId: String) {
        searchViewResultLiveData.value = restaurantId
    }

    fun getUsersSearchLiveData(): LiveData<String> {
        return searchViewResultLiveData
    }
}