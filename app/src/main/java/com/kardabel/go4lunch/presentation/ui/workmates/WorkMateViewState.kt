package com.kardabel.go4lunch.presentation.ui.workmates

import androidx.annotation.ColorRes

data class WorkMateViewState constructor(
    val workmateName: String? = null,
    val workmateDescription: String? = null,
    val workmatePhoto: String? = null,
    val workmateId: String? = null,
    val gotRestaurant: Boolean = false,
    @ColorRes
    val textColor: Int = 0
)