package com.ravi.android.face.detection.domain.model

data class Features(
    var trackingId: String? = "",
    var smileProb: String? = "",
    var rightEyeOpenProb: String? = "",
    var leftEyeOpenProb: String? = "",
    var color:Int = 0
)