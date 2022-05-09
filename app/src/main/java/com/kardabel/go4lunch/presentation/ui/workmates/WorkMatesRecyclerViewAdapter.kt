package com.kardabel.go4lunch.presentation.ui.workmates

import android.annotation.SuppressLint
import android.graphics.Typeface
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

class WorkMatesRecyclerViewAdapter(
    private val listener: (workMate: WorkMateViewState) -> Unit,
) : ListAdapter<WorkMateViewState, WorkMatesRecyclerViewAdapter.WorkmatesViewHolder>(ListComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmatesViewHolder {
        return WorkmatesViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: WorkmatesViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class WorkmatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val workmateDescription: TextView =
            itemView.findViewById(R.id.item_workmate_description)

        private val photo: ImageView = itemView.findViewById(R.id.item_workmate_avatar)

        @SuppressLint("ResourceAsColor")
        fun bind(workMate: WorkMateViewState, listener: (WorkMateViewState) -> Unit) {

            workmateDescription.text = workMate.workmateDescription
            workmateDescription.setTextColor(workMate.textColor)
            Glide
                .with(photo.context)
                .load(workMate.workmatePhoto)
                //.error(R.drawable.no_photo_available_yet)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .circleCrop()
                .into(photo)
            // TODO : if there is a solution to pass styleRes to avoid "if" in view
            if (!workMate.gotRestaurant) {
                workmateDescription.setTypeface(null, Typeface.ITALIC)
            }

            itemView.setOnClickListener {
                listener.invoke(workMate)
            }
        }

        companion object {
            fun create(parent: ViewGroup): WorkmatesViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_workmate, parent, false)
                return WorkmatesViewHolder(view)
            }
        }
    }

    object ListComparator : DiffUtil.ItemCallback<WorkMateViewState>() {
        override fun areItemsTheSame(
            oldItem: WorkMateViewState,
            newItem: WorkMateViewState,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: WorkMateViewState,
            newItem: WorkMateViewState,
        ): Boolean = oldItem == newItem
    }
}