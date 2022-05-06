package com.kardabel.go4lunch.ui.chat

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.kardabel.go4lunch.model.ChatMessageModel
import com.kardabel.go4lunch.repository.ChatMessageRepository
import com.kardabel.go4lunch.usecase.AddChatMessageToFirestoreUseCase
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase
import java.util.*

class ChatViewModel constructor(
    private val chatMessageRepository: ChatMessageRepository,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val addChatMessageToFirestoreUseCase: AddChatMessageToFirestoreUseCase,
): ViewModel() {

    val getChatMessagesMediatorLiveData = MediatorLiveData<List<ChatViewState>>()

    fun init (workmateId: String){

        val chatMessageModelLiveData = chatMessageRepository.getChatMessages(workmateId)
        getChatMessagesMediatorLiveData.addSource(chatMessageModelLiveData
        ) { chatMessageModels: List<ChatMessageModel>? ->
            combineMessages(chatMessageModels)
        }
    }

    private fun combineMessages(chatMessageModels: List<ChatMessageModel>?) {
        if (chatMessageModels != null) {
            getChatMessagesMediatorLiveData.value = mapMessages(chatMessageModels)
        }
    }

    private fun mapMessages(chatMessages: List<ChatMessageModel>): List<ChatViewState> {

        val chatViewStates = mutableListOf<ChatViewState>()

        for (message in chatMessages) {
            chatViewStates.add(ChatViewState(
                chatMessageViewState = message.message!!,
                isSender = isSender(message.sender),
                chatMessageTimeViewState = message.date!!,
                timestamp = message.timestamp!!))
        }

        chatViewStates.sortWith { (_, _, _, timestamp), (_, _, _, timestamp1) ->
            timestamp.compareTo(timestamp1)
        }

        Collections.sort(chatViewStates, Comparator.comparingLong { it.timestamp })
        return chatViewStates
    }

    private fun isSender(sender: String?): Int {
        var senderType = 1
        val userId = getCurrentUserIdUseCase.invoke()!!
        if (userId == sender) {
            senderType = 2
        }
        return senderType
    }

    fun createChatMessage(message: String, workmateId: String) {
        addChatMessageToFirestoreUseCase.createChatMessage(message, workmateId)
    }

}