package com.kardabel.go4lunch.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.go4lunch.BuildConfig
import com.kardabel.go4lunch.data.retrofit.GoogleMapsApi
import com.kardabel.go4lunch.domain.pojo.NearbySearchResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NearbySearchResponseRepository(
    private val googleMapsApi: GoogleMapsApi,
) {

    private val cache: MutableMap<String, NearbySearchResults> = HashMap(2000)

    fun getRestaurantListLiveData(
        type: String,
        location: String,
        radius: String,
    ): LiveData<NearbySearchResults?>{

        val key = BuildConfig.GOOGLE_PLACES_KEY

        val nearbySearchResultsMutableLiveData = MutableLiveData<NearbySearchResults?>()

        val nearbySearchResults = cache[location]

        if (nearbySearchResults != null) {

            nearbySearchResultsMutableLiveData.value = nearbySearchResults

        } else {
            googleMapsApi.searchRestaurant(key, type, location, radius)!!.enqueue(
                object : Callback<NearbySearchResults?> {
                    override fun onResponse(
                        call: Call<NearbySearchResults?>,
                        response: Response<NearbySearchResults?>,
                    ) {
                        if (response.body() != null) {
                            cache[location] = response.body()!!
                            nearbySearchResultsMutableLiveData.value = response.body()
                        }
                    }
                    override fun onFailure(call: Call<NearbySearchResults?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }
        return nearbySearchResultsMutableLiveData
    }
}