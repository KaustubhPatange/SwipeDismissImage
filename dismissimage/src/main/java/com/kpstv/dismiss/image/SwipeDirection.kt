package com.kpstv.dismiss.image

internal sealed class SwipeDirection(val deltaX: Float, val deltaY: Float) {
    object Top: SwipeDirection(deltaX = 5f, deltaY = 1f)
    object Bottom: SwipeDirection(deltaX = 1f, deltaY = 5f)
    object Right: SwipeDirection(deltaX = 5f, deltaY = 5f)
    object Left: SwipeDirection(deltaX = 5f, deltaY = 5f)
    object None: SwipeDirection(deltaX = 1f, deltaY = 1f)

    companion object {
        fun detectSwipeDirection(translateX: Float, translateY: Float): SwipeDirection {
            if (translateX < 0 && translateY > 0)
                return Left
            else if (translateX < 0 && translateY < 0)
                return Left
            else if (translateX > 0 && translateY > 0)
                return Right
            else if (translateX > 0 && translateY < 0)
                return Right
            else if (translateX == 0f && translateY > 0)
                return Bottom
            else if (translateX == 0f && translateY < 0)
                return Top
            else if (translateX == 0f && translateY == 0f)
                return None
            else if (translateX < 0 && translateY == 0f)
                return Left
            else if (translateX > 0 && translateY == 0f)
                return Right
            throw IllegalStateException("This shouldn't reach here")
        }
    }
}