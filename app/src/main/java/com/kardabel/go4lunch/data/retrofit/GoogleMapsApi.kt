package com.kardabel.go4lunch.data.retrofit

import retrofit2.http.GET
import com.kardabel.go4lunch.domain.pojo.NearbySearchResults
import com.kardabel.go4lunch.domain.pojo.RestaurantDetailsResult
import com.kardabel.go4lunch.domain.pojo.Predictions
import retrofit2.Call
import retrofit2.http.Query

interface GoogleMapsApi {
    @GET("maps/api/place/nearbysearch/json")
    fun searchRestaurant(
        @Query("key") key: String?,
        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: String?
    ): Call<NearbySearchResults?>?

    @GET("maps/api/place/details/json")
    fun searchRestaurantDetails(
        @Query("key") key: String?,
        @Query("place_id") place_id: String?,
        @Query("fields") fields: String?
    ): Call<RestaurantDetailsResult?>?

    @GET("maps/api/place/autocomplete/json")
    fun autocompleteResult(
        @Query("key") key: String?,
        @Query("type") type: String?,
        @Query("location") location: String?,
        @Query("radius") radius: String?,
        @Query("input") input: String?
    ): Call<Predictions?>?
}