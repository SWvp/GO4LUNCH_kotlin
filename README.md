<p align="center">
 	<img 
   		width="300"
   		height="300"
   		src="screenshot/go4lunch.png"
		alt="accueil" 
  	>
</p>

# GO4LUNCH, Kotlin version, workInProgress (currently being migrated to Flow)
Android application to find restaurants near you. Allow you to choose one for the next lunch and share your selection with your coworkers.
You'll also be able to check out where your coworkers are headed for lunch and decide if you want to go with them.
Be notified by a push notification before the lunch break: this will inform you of the address and the employees who will be at the same place.

<p align="center">
 	<img alt="demo" src="screenshot/demo.gif" width="30%">
</p>

## Features
* Authentification with Gmail and Facebook via Firebase Authentification
* Main activity:
	* Navigation Drawer (access to your lunch of the day, settings for the notification and logout)
	* SearchView with autocomplete from GoogleMap API

<p align="center">
 	<img alt="navDrawer" src="screenshot/navDrawer.png" width="30%">
</p>

* 3 fragments:
	* the listView (listAdapter), which displays a list of 20 restaurants with full opening hours
	* the mapView, which displays a GoogleMap with your position and the restaurant list
	* the workmatesView (listAdapter), which displays the coworkers list and if they have chosen a restaurant

<p align="center">
 	<img alt="mapView" src="screenshot/mapView.png" width="30%">
&nbsp; &nbsp; &nbsp; &nbsp;
 	<img alt="listView" src="screenshot/listView.png" width="30%">
&nbsp; &nbsp; &nbsp; &nbsp;
 	<img alt="workmatesView" src="screenshot/workmate.png" width="30%">
</p>

* A restaurant details activity that allows you to call the restaurant, visit website, add to favorite, select this place for today and see coworkers with the same choice
* A chat activity that can be launched from the workmatesView
* Every day, the database will create a collection with the actual date to store the user's choice
* The notification push is managed with a WorkManager, and will display the notification just before lunch break

## Library
* Material Design:
	* constraint layout
	* coordinator layout
	* collapsing toolbar
* Glide
* Firebase Auth
* Firebase Firestore
* Facebook SDK
* Retrofit
* WorkManager
* Mockito
* Desugar

## Architecture
* MVVM with an approach of clean Architecture (viewModel, ViewState, Usecase, Repository)
* LiveData (currently being migrated to Flow)

## Dependency injection
* ViewModel factory, handmade (soon replaced by Hilt)

## Unit tests
* ViewModel and usecase with Mockito

## API
* Google Map API:
	* place nearby search
	* place details
	* place autocomplete

## How to use this app
* Go to [build.gradle](https://github.com/SWvp/GO4LUNCH_kotlin/blob/main/app/build.gradle) line 24 & 25
* replace OC_P7_Google_Maps_Sdk_key by '"your-google-map-key-here"'
* or edit/create gradle.properties in user\.gradle folder with GOOGLE_MAP_API_KEY="your-google-map-key-here"

Feel free to send feedback about your experience !
Happy coding !

Stéphane
