package com.kardabel.go4lunch.presentation.ui.autocomplete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kardabel.go4lunch.R

class PredictionsAdapter(
    private val listener: (restaurantName: String) -> Unit,
) : ListAdapter<PredictionViewState, PredictionsAdapter.ViewHolder>(ListComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val predictionText = itemView.findViewById<TextView?>(R.id.prediction_text)

        fun bind(
            predictionViewState: PredictionViewState,
            listener: (String) -> Unit,
        ) {

            predictionText.text = predictionViewState.predictionDescription

            itemView.setOnClickListener {
                listener.invoke(predictionViewState.predictionName)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_prediction, parent, false)
                return ViewHolder(view)
            }
        }
    }

    object ListComparator : DiffUtil.ItemCallback<PredictionViewState>() {

        override fun areItemsTheSame(
            oldItem: PredictionViewState,
            newItem: PredictionViewState,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: PredictionViewState,
            newItem: PredictionViewState,
        ): Boolean = oldItem == newItem
    }
}