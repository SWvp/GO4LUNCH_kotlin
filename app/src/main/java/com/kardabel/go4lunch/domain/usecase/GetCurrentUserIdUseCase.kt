package com.kardabel.go4lunch.domain.usecase

import com.google.firebase.auth.FirebaseAuth

class GetCurrentUserIdUseCase {
    operator fun invoke(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}