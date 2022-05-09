package com.kardabel.go4lunch.domain.usecase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.domain.pojo.Predictions;
import com.kardabel.go4lunch.domain.repository.AutocompleteRepository;
import com.kardabel.go4lunch.domain.repository.LocationRepository;

public class GetPredictionsUseCase {

    private final LocationRepository locationRepository;
    private final AutocompleteRepository autocompleteRepository;

    // RETRIEVE AUTOCOMPLETE PREDICTIONS RESULTS FROM LOCATION
    public GetPredictionsUseCase(
            LocationRepository locationRepository,
            AutocompleteRepository autocompleteRepository) {

        this.locationRepository = locationRepository;
        this.autocompleteRepository = autocompleteRepository;

    }

    public LiveData<Predictions> invoke(String text) {
        return Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
            String locationAsText = input.getLatitude() + "," + input.getLongitude();
            return Transformations.map(autocompleteRepository.getAutocompleteResultListLiveData(
                    locationAsText,
                    text),
                    input1 -> input1);

        });
    }
}
