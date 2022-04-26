package com.kardabel.go4lunch.ui.autocomplete

import com.kardabel.go4lunch.ui.autocomplete.PredictionViewState
import java.util.*

data class PredictionViewState constructor(
    val predictionDescription: String,
    val predictionPlaceId: String,
    val predictionName: String
) 