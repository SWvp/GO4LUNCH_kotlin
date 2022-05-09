package com.kardabel.go4lunch.domain.usecase;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.domain.pojo.NearbySearchResults;
import com.kardabel.go4lunch.domain.repository.LocationRepository;
import com.kardabel.go4lunch.domain.repository.NearbySearchResponseRepository;

public class GetNearbySearchResultsUseCase {

    public static final String RESTAURANT = "restaurant";
    public static final String RADIUS = "1000";
    public LiveData<NearbySearchResults> NearbySearchResultsLiveData;

    // RETRIEVE NEARBY RESULTS FROM LOCATION
    public GetNearbySearchResultsUseCase(LocationRepository locationRepository,
                                         NearbySearchResponseRepository nearbySearchResponseRepository,
                                         Application application) {


        NearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
             String locationAsText = input.getLatitude() + "," + input.getLongitude();
             return nearbySearchResponseRepository.getRestaurantListLiveData(
                     RESTAURANT,
                    locationAsText,
                     application.getString(R.string.radius));

        });
    }
    public LiveData<NearbySearchResults> invoke() { return NearbySearchResultsLiveData; }
}
