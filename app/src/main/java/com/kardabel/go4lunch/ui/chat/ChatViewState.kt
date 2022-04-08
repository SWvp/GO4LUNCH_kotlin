package com.kardabel.go4lunch.ui.chat

data class ChatViewState constructor(
    val chatMessageViewState: String? = null,
    val isSender: Int = 0,
    val chatMessageTimeViewState: String? = null,
    val timestamp: Long? = null
)