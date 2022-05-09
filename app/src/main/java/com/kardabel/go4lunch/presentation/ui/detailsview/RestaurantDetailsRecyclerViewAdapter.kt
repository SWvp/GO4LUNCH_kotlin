package com.kardabel.go4lunch.presentation.ui.detailsview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kardabel.go4lunch.R

class RestaurantDetailsRecyclerViewAdapter(
    private val listener: (placeId: String) -> Unit
) : ListAdapter<RestaurantDetailsWorkmatesViewState, RestaurantDetailsRecyclerViewAdapter.RestaurantDetailsViewHolder>(
    ListComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {
        return RestaurantDetailsViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(
            current,
            //listener
        )
    }

    class RestaurantDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val workmateDescription: TextView =
            itemView.findViewById(R.id.item_workmate_description)

        private val photo: ImageView = itemView.findViewById(R.id.item_workmate_avatar)

        @SuppressLint("ResourceAsColor")
        fun bind(
            restaurantDetailsViewState: RestaurantDetailsWorkmatesViewState,
            //listener: (String) -> Unit
        ) {

            workmateDescription.text = restaurantDetailsViewState.workmateName

            Glide
                .with(photo.context)
                .load(restaurantDetailsViewState.workmateDetailPhoto)
                //.error(R.drawable.no_photo_available_yet)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .circleCrop()
                .into(photo)


            // TODO: let click on an item and go to chat view
            // itemView.setOnClickListener {
            //     listener.invoke()
            // }
        }


        companion object {
            fun create(parent: ViewGroup): RestaurantDetailsViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_workmate, parent, false)
                return RestaurantDetailsViewHolder(view)
            }
        }
    }

    object ListComparator : DiffUtil.ItemCallback<RestaurantDetailsWorkmatesViewState>() {
        override fun areItemsTheSame(
            oldItem: RestaurantDetailsWorkmatesViewState,
            newItem: RestaurantDetailsWorkmatesViewState,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: RestaurantDetailsWorkmatesViewState,
            newItem: RestaurantDetailsWorkmatesViewState,
        ): Boolean = oldItem == newItem
    }
}