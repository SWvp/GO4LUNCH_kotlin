package com.kardabel.go4lunch.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class GetCurrentUserUseCase {

    companion object{
        fun getFirebaseAuth() : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    }
}