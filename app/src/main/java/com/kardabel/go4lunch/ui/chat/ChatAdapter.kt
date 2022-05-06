package com.kardabel.go4lunch.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kardabel.go4lunch.R


class ChatAdapter(
    activityContext: Context,
) : ListAdapter<ChatViewState, RecyclerView.ViewHolder>(ListComparator) {

    private val messageType = 1
    val context = activityContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == messageType) {
            ChatIncomingViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_chat_incoming, parent, false))
        } else ChatOutgoingViewHolder(LayoutInflater
            .from(context)
            .inflate(R.layout.item_chat_outgoing, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatIncomingViewHolder) {
            (holder).bind(getItem(position))
        } else if (holder is ChatOutgoingViewHolder) {
            (holder).bind(getItem(position))
        }
    }

    class ChatIncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val message: TextView = itemView.findViewById(R.id.incoming_message_text)

        private val date: TextView = itemView.findViewById(R.id.incoming_message_date)


        fun bind(chatViewState: ChatViewState) {

            message.text = chatViewState.chatMessageViewState
            date.text = chatViewState.chatMessageTimeViewState

        }
    }

    class ChatOutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message: TextView = itemView.findViewById(R.id.outgoing_message_text)

        private val date: TextView = itemView.findViewById(R.id.outgoing_message_date)


        fun bind(chatViewState: ChatViewState) {

            message.text = chatViewState.chatMessageViewState
            date.text = chatViewState.chatMessageTimeViewState

        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).isSender
    }

    object ListComparator : DiffUtil.ItemCallback<ChatViewState>() {

        override fun areItemsTheSame(
            oldItem: ChatViewState,
            newItem: ChatViewState,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ChatViewState,
            newItem: ChatViewState,
        ): Boolean = oldItem == newItem
    }
}