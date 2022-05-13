package com.kardabel.go4lunch.di

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kardabel.go4lunch.MainApplication
import com.kardabel.go4lunch.data.retrofit.GoogleMapsApi
import com.kardabel.go4lunch.domain.repository.*
import com.kardabel.go4lunch.domain.usecase.*
import com.kardabel.go4lunch.presentation.ui.chat.ChatViewModel
import com.kardabel.go4lunch.presentation.ui.detailsview.RestaurantDetailsViewModel
import com.kardabel.go4lunch.presentation.ui.main.MainActivityViewModel
import com.kardabel.go4lunch.presentation.ui.mapview.MapViewModel
import com.kardabel.go4lunch.presentation.ui.restaurants.RestaurantsViewModel
import com.kardabel.go4lunch.presentation.ui.setting.SettingViewModel
import com.kardabel.go4lunch.presentation.ui.workmates.WorkMatesViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalArgumentException
import java.time.Clock


@Suppress("UNCHECKED_CAST")
class ViewModelFactory: ViewModelProvider.Factory {

    private val application: Application
    private val context: Context
    private val locationRepository: LocationRepository
    private val workmatesRepository: WorkmatesRepository
    private val mUserSearchRepository: UserSearchRepository
    private val favoriteRestaurantsRepository: FavoriteRestaurantsRepository
    private val mUsersWhoMadeRestaurantChoiceRepository: UsersWhoMadeRestaurantChoiceRepository
    private val chatMessageRepository: ChatMessageRepository
    private val notificationsRepository: NotificationsRepository
    private val getNearbySearchResultsUseCase: GetNearbySearchResultsUseCase
    private val getNearbySearchResultsByIdUseCase: GetNearbySearchResultsByIdUseCase
    private val getRestaurantDetailsResultsUseCase: GetRestaurantDetailsResultsUseCase
    private val getRestaurantDetailsResultsByIdUseCase: GetRestaurantDetailsResultsByIdUseCase
    private val getPredictionsUseCase: GetPredictionsUseCase
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
    private val addChatMessageToFirestoreUseCase: AddChatMessageToFirestoreUseCase
    private val clickOnChoseRestaurantButtonUseCase: ClickOnChoseRestaurantButtonUseCase
    private val clickOnFavoriteRestaurantUseCase: ClickOnFavoriteRestaurantUseCase


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var factory: ViewModelFactory? = null
        val instance: ViewModelFactory?
            get() {
                if (factory == null) {
                    synchronized(ViewModelFactory::class.java) {
                        if (factory == null) {
                            factory = ViewModelFactory()
                        }
                    }
                }
                return factory
            }
    }

    init {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val googleMapsApi = retrofit.create(GoogleMapsApi::class.java)

        val firebaseFirestore = FirebaseFirestore.getInstance()

        val firebaseAuth = FirebaseAuth.getInstance()

        application = MainApplication.getApplication()

        context = application.applicationContext

        val nearbySearchResponseRepository = NearbySearchResponseRepository(
            googleMapsApi
        )

        val restaurantDetailsResponseRepository = RestaurantDetailsResponseRepository(
            googleMapsApi,
            application)

        val autocompleteRepository = AutocompleteRepository(
            googleMapsApi,
            application)

        locationRepository = LocationRepository()
        workmatesRepository = WorkmatesRepository()
        mUserSearchRepository = UserSearchRepository()
        favoriteRestaurantsRepository = FavoriteRestaurantsRepository()
        mUsersWhoMadeRestaurantChoiceRepository =
            UsersWhoMadeRestaurantChoiceRepository(Clock.systemDefaultZone())
        chatMessageRepository = ChatMessageRepository()
        notificationsRepository = NotificationsRepository(context)
        getNearbySearchResultsUseCase = GetNearbySearchResultsUseCase(
            locationRepository,
            nearbySearchResponseRepository,
            application)
        getNearbySearchResultsByIdUseCase = GetNearbySearchResultsByIdUseCase(
            locationRepository,
            nearbySearchResponseRepository,
            application)
        getRestaurantDetailsResultsUseCase = GetRestaurantDetailsResultsUseCase(
            locationRepository,
            nearbySearchResponseRepository,
            restaurantDetailsResponseRepository,
            application)
        getRestaurantDetailsResultsByIdUseCase = GetRestaurantDetailsResultsByIdUseCase(
            restaurantDetailsResponseRepository
        )
        getPredictionsUseCase = GetPredictionsUseCase(
            locationRepository,
            autocompleteRepository)
        getCurrentUserIdUseCase = GetCurrentUserIdUseCase()
        addChatMessageToFirestoreUseCase = AddChatMessageToFirestoreUseCase(
            firebaseFirestore,
            firebaseAuth,
            Clock.systemDefaultZone())
        clickOnChoseRestaurantButtonUseCase = ClickOnChoseRestaurantButtonUseCase(
            firebaseFirestore,
            firebaseAuth,
            Clock.systemDefaultZone())
        clickOnFavoriteRestaurantUseCase = ClickOnFavoriteRestaurantUseCase(firebaseFirestore)
    }



    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        when {
            modelClass.isAssignableFrom(RestaurantsViewModel::class.java) -> {
                return RestaurantsViewModel(
                    application,
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    getRestaurantDetailsResultsUseCase,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    mUserSearchRepository,
                    Clock.systemDefaultZone()) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                return MapViewModel(
                    locationRepository,
                    getNearbySearchResultsUseCase,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    mUserSearchRepository) as T
            }
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {
                return MainActivityViewModel(
                    application,
                    locationRepository,
                    getPredictionsUseCase,
                    mUserSearchRepository,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    getCurrentUserIdUseCase
                ) as T
            }
            modelClass.isAssignableFrom(RestaurantDetailsViewModel::class.java) -> {
                return RestaurantDetailsViewModel(
                    application,
                    getNearbySearchResultsByIdUseCase,
                    getRestaurantDetailsResultsByIdUseCase,
                    mUsersWhoMadeRestaurantChoiceRepository,
                    workmatesRepository,
                    favoriteRestaurantsRepository,
                    getCurrentUserIdUseCase,
                    clickOnChoseRestaurantButtonUseCase,
                    clickOnFavoriteRestaurantUseCase) as T
            }
            modelClass.isAssignableFrom(WorkMatesViewModel::class.java) -> {
                return WorkMatesViewModel(
                    application,
                    workmatesRepository,
                    mUsersWhoMadeRestaurantChoiceRepository
                ) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                return ChatViewModel(
                    chatMessageRepository,
                    getCurrentUserIdUseCase,
                    addChatMessageToFirestoreUseCase
                ) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                return SettingViewModel(
                    notificationsRepository,
                    context,
                    Clock.systemDefaultZone()
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}