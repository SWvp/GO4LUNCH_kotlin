package com.kardabel.go4lunch.domain.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.go4lunch.BuildConfig
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.data.retrofit.GoogleMapsApi
import com.kardabel.go4lunch.domain.pojo.Predictions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutocompleteRepository(
    private val googleMapsApi: GoogleMapsApi,
    private val application: Application,
) {
    fun getAutocompleteResultListLiveData(
        location: String?,
        input: String?,
    ): LiveData<Predictions?> {

        val key = BuildConfig.GOOGLE_PLACES_KEY
        val type = application.getString(R.string.autocomplete_type)
        val radius = application.getString(R.string.radius)

        val autocompleteResultMutableLiveData = MutableLiveData<Predictions?>()

        googleMapsApi.autocompleteResult(key, type, location, radius, input)!!.enqueue(
            object : Callback<Predictions?> {
                override fun onResponse(
                    call: Call<Predictions?>,
                    response: Response<Predictions?>,
                ) {
                    if (response.body() != null) {
                        autocompleteResultMutableLiveData.value = response.body()
                    }
                }
                override fun onFailure(call: Call<Predictions?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        return autocompleteResultMutableLiveData
    }
}