package com.kardabel.go4lunch.domain.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.go4lunch.BuildConfig
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.data.retrofit.GoogleMapsApi
import com.kardabel.go4lunch.domain.pojo.RestaurantDetailsResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailsResponseRepository(
    private val googleMapsApi: GoogleMapsApi,
    private val application: Application,
) {

    private val cache: MutableMap<String, RestaurantDetailsResult?> = HashMap(2000)

    fun getRestaurantDetailsLiveData(restaurantId: String): LiveData<RestaurantDetailsResult?> {

        val key = BuildConfig.GOOGLE_PLACES_KEY
        val FIELDS: String = application.getString(R.string.restaurant_details_fields)

        val placeDetailsResultMutableLiveData = MutableLiveData<RestaurantDetailsResult?>()

        val restaurantDetailsResult: RestaurantDetailsResult? = cache[restaurantId]

        if (restaurantDetailsResult != null) {

            placeDetailsResultMutableLiveData.value = restaurantDetailsResult

        } else {

            val call = googleMapsApi.searchRestaurantDetails(key, restaurantId, FIELDS)

            call!!.enqueue(object : Callback<RestaurantDetailsResult?> {
                override fun onResponse(
                    call: Call<RestaurantDetailsResult?>,
                    response: Response<RestaurantDetailsResult?>,
                ) {
                    if (response.body() != null) {
                        cache[restaurantId] = response.body()
                        placeDetailsResultMutableLiveData.setValue(response.body())
                    } else {
                        Log.d("Response errorBody", response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<RestaurantDetailsResult?>, t: Throwable) {
                    Log.d("pipo", "Detail called issues")
                }
            })
        }
        return placeDetailsResultMutableLiveData
    }
}