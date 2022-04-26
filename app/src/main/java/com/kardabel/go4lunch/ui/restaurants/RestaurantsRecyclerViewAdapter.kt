package com.kardabel.go4lunch.ui.restaurants

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kardabel.go4lunch.R

class RestaurantsRecyclerViewAdapter(
    private val listener: (placeId: String) -> Unit
): ListAdapter<RestaurantsViewState, RestaurantsRecyclerViewAdapter.RestaurantViewHolder>(ListComparator){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val restaurantName: TextView = itemView.findViewById(R.id.item_listview_restaurant_name)
        private val restaurantAddress: TextView = itemView.findViewById(R.id.item_listview_address)
        private val restaurantOpeningHours: TextView = itemView.findViewById(R.id.item_listview_opening_hour)
        private val restaurantDistanceTo: TextView = itemView.findViewById(R.id.item_listview_distance)
        private val restaurantRating: RatingBar = itemView.findViewById(R.id.rating_bar)
        private val interestedWorkmates: TextView = itemView.findViewById(R.id.item_listview_interested_workmates)
        private val photo: ImageView = itemView.findViewById(R.id.item_listview_restaurant_picture)

        @SuppressLint("ResourceAsColor")
        fun bind(restaurantsViewState: RestaurantsViewState, listener: (String) -> Unit) {

            restaurantName.text = restaurantsViewState.name
            restaurantAddress.text = restaurantsViewState.address
            restaurantOpeningHours.text = restaurantsViewState.openingHours
            //restaurantOpeningHours.setTextColor(restaurantsViewState.textColor)
            restaurantDistanceTo.text = restaurantsViewState.distanceText
            restaurantRating.rating = restaurantsViewState.rating.toFloat()
            interestedWorkmates.text = restaurantsViewState.usersWhoChoseThisRestaurant

            Glide
                .with(photo.context)
                .load(restaurantsViewState.photo)
                //.error(R.drawable.no_photo_available_yet)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(photo)


            itemView.setOnClickListener {
                listener.invoke(restaurantsViewState.placeId!!)
            }
        }


        companion object {
            fun create(parent: ViewGroup): RestaurantViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_restaurant, parent, false)
                return RestaurantViewHolder(view)
            }
        }
    }

    object ListComparator : DiffUtil.ItemCallback<RestaurantsViewState>() {
        override fun areItemsTheSame(
            oldItem: RestaurantsViewState,
            newItem: RestaurantsViewState
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: RestaurantsViewState,
            newItem: RestaurantsViewState
        ): Boolean = oldItem == newItem
    }
}