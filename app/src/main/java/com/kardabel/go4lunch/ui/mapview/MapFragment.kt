package com.kardabel.go4lunch.ui.mapview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kardabel.go4lunch.R
import com.kardabel.go4lunch.di.ViewModelFactory
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity
import com.kardabel.go4lunch.util.SvgToBitmapConverter

class MapFragment : SupportMapFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    init {
        getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // CUSTOM MAP WITHOUT POI WE DON'T NEED
        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        // CHECK IF USER CHOSE TO SHARE HIS LOCATION
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // CONFIGURE MAPVIEWMODEL
            val mapViewModelFactory = ViewModelFactory.getInstance()
            val mapViewModel = ViewModelProvider(this, mapViewModelFactory).get(
                MapViewModel::class.java)

            // SET USER LOCATION AND THEN POI
            setUserLocation(mapViewModel, googleMap)

            // SET A LISTENER FOR MARKER CLICK
            googleMap.setOnMarkerClickListener(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUserLocation(mapViewModel: MapViewModel, googleMap: GoogleMap) {
        mapViewModel.mapViewStatePoiMediatorLiveData.observe(this) { (poiList, userLocation, zoom) ->
            googleMap.clear()

            // MOVE THE CAMERA TO THE USER LOCATION
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoom))

            // DISPLAY BLUE DOT FOR USER LOCATION
            googleMap.isMyLocationEnabled = true

            // ZOOM IN, ANIMATE CAMERA
            googleMap.animateCamera(CameraUpdateFactory.zoomIn())

            // CAMERA POSITION
            val cameraPosition = CameraPosition.Builder()
                .target(userLocation) // Sets the center of the map to Mountain View
                .zoom(17f) // Sets the zoom
                .bearing(90f) // Sets the orientation of the camera to east
                .tilt(30f) // Sets the tilt of the camera to 30 degrees
                .build() // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            setPoi(poiList, googleMap, requireContext())

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val placeId = marker.tag as String?
        startActivity(RestaurantDetailsActivity.navigate(requireContext(), placeId))
        return false
    }

    private fun setPoi(poiList: List<Poi>, googleMap: GoogleMap, context: Context) {
        for (poi in poiList) {
            val marker: Marker = if (poi.isFavorite) {

                setPoiColor(poi, googleMap, context, R.drawable.restaurant_poi_icon_green)

            } else {

                // SET TAG TO RETRIEVE THE MARKER IN onMarkerClick METHOD
                setPoiColor(poi, googleMap, context, R.drawable.restaurant_poi_icon_red)

            }
            marker.tag = poi.poiPlaceId
        }
    }

    private fun setPoiColor(poi: Poi, googleMap: GoogleMap, context: Context, color: Int): Marker {

        return googleMap.addMarker(MarkerOptions()
            .position(poi.poiLatLng!!)
            .title(poi.poiName)
            .snippet(poi.poiAddress)
            .icon(BitmapDescriptorFactory
                .fromBitmap(SvgToBitmapConverter
                    .getBitmapFromVectorDrawable(context,
                        color))))!!
    }
}
