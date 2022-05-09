package com.kardabel.go4lunch.domain.model

data class ChatMessageModel constructor(
    var message: String? = null,
    var sender: String? = null,
    var date: String? = null,
    var timestamp: Long? = null,
)