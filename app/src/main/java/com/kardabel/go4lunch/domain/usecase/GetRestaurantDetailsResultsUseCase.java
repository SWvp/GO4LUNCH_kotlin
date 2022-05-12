package com.kardabel.go4lunch.domain.usecase;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.domain.pojo.NearbySearchResults;
import com.kardabel.go4lunch.domain.pojo.Restaurant;
import com.kardabel.go4lunch.domain.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.domain.repository.LocationRepository;
import com.kardabel.go4lunch.domain.repository.NearbySearchResponseRepository;
import com.kardabel.go4lunch.domain.repository.RestaurantDetailsResponseRepository;

import java.util.ArrayList;
import java.util.List;

public class GetRestaurantDetailsResultsUseCase {

    public static final String RESTAURANT = "restaurant";

    private final MediatorLiveData<List<RestaurantDetailsResult>> restaurantsDetailsMediatorLiveData =
            new MediatorLiveData<>();
    private final MediatorLiveData<RestaurantDetailsResult> restaurantDetailsMediatorLiveData =
            new MediatorLiveData<>();
    private final List<RestaurantDetailsResult> restaurantDetailsList =
            new ArrayList<>();
    public LiveData<NearbySearchResults> nearbySearchResultsLiveData;
    private final RestaurantDetailsResponseRepository restaurantDetailsResponseRepository;

    public GetRestaurantDetailsResultsUseCase(LocationRepository locationRepository,
                                              NearbySearchResponseRepository nearbySearchResponseRepository,
                                              RestaurantDetailsResponseRepository restaurantDetailsResponseRepository,
                                              Application application) {

        this.restaurantDetailsResponseRepository = restaurantDetailsResponseRepository;

        // GET THE NEARBY SEARCH RESULT WITH USER LOCATION AS TRIGGER
        nearbySearchResultsLiveData = Transformations.switchMap(locationRepository.getLocationLiveData(), input -> {
            String locationAsText = input.getLatitude() + "," + input.getLongitude();
            return nearbySearchResponseRepository.getRestaurantListLiveData(
                    RESTAURANT,
                    locationAsText,
                    application.getString(R.string.radius));

        });

        // THE COMBINE METHOD ALLOW NULL ARGS, SO LETS NEARBY TRIGGER THE COMBINE,
        // THEN, WHEN DETAILS RESULT IS SEND BY REPO, TRIGGER COMBINE TO SET LIVEDATA VALUE
        restaurantsDetailsMediatorLiveData.addSource(nearbySearchResultsLiveData, nearbySearchResults ->
                combine(
                        nearbySearchResults,
                        null));

        restaurantsDetailsMediatorLiveData.addSource(restaurantDetailsMediatorLiveData, restaurantDetailsResult ->
                combine(
                        nearbySearchResultsLiveData.getValue(),
                        restaurantDetailsResult));

    }

    private void combine(@Nullable NearbySearchResults nearbySearchResults,
                         @Nullable RestaurantDetailsResult restaurantDetailsResult) {

        assert nearbySearchResults != null;
        if (nearbySearchResults.getResults() != null) {


            for (Restaurant restaurant: nearbySearchResults.getResults()) {

                if (!restaurantDetailsList.contains(restaurantDetailsResult) || restaurantDetailsResult == null) {

                    String placeId = restaurant.getRestaurantId();

                    restaurantDetailsMediatorLiveData.addSource(
                            restaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId), restaurantDetailsResult1 -> {
                                restaurantDetailsList.add(restaurantDetailsResult1);
                                restaurantDetailsMediatorLiveData.setValue(restaurantDetailsResult1);

                            });
                }
            }
            restaurantsDetailsMediatorLiveData.setValue(restaurantDetailsList);

        }
    }

    public LiveData<List<RestaurantDetailsResult>> invoke() {
        return restaurantsDetailsMediatorLiveData;

    }
}