package com.kardabel.go4lunch.domain.usecase

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kardabel.go4lunch.domain.pojo.Predictions
import com.kardabel.go4lunch.domain.repository.AutocompleteRepository
import com.kardabel.go4lunch.domain.repository.LocationRepository

class GetPredictionsUseCase(
    private val locationRepository: LocationRepository,
    private val autocompleteRepository: AutocompleteRepository,
) {

    // RETRIEVE AUTOCOMPLETE PREDICTIONS RESULTS FROM LOCATION
    fun invoke(text: String?) : LiveData<Predictions> =
        Transformations.switchMap(locationRepository.getLocationLiveData()) { input: Location ->
            val locationAsText = input.latitude.toString() + "," + input.longitude
            Transformations.map(autocompleteRepository.getAutocompleteResultListLiveData(
                locationAsText,
                text)
            ) { input1: Predictions? -> input1 }
        }
}