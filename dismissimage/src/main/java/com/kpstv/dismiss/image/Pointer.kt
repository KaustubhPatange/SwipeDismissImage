package com.kpstv.dismiss.image

internal data class TranslationPointer(
    var firstX: Float = 0f,
    var firstY: Float = 0f,
    var totalSwipeDistanceX: Float = 0f,
    var totalSwipeDistanceY: Float = 0f,
    var swipeDistanceX: Float = 0f,
    var swipeDistanceY: Float = 0f
)

internal data class ScalePointer(
    var initialDistance: Float = 0f,
    var lastScaleFactor: Float = 0f
)