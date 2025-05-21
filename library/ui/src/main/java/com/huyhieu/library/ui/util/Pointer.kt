package com.huyhieu.library.ui.util

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.util.VelocityTracker

//suspend fun PointerInputScope.detectTransformGesturesWithEnd(
//    onGestureEnd: () -> Unit,
//    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
//) {
//    awaitEachGesture {
//        val touchSlop = viewConfiguration.touchSlop
//        val down = awaitFirstDown(requireUnconsumed = false)
//
//        do {
//            val event = awaitPointerEvent()
//            val canceled = event.changes.any { it.isConsumed }
//            if (!canceled && event.changes.size > 1) {
//                val zoomChange = event.calculateZoom()
//                val panChange = event.calculatePan()
//                val rotationChange = event.calculateRotation()
//                val centroid = event.calculateCentroid()
//                onGesture(centroid, panChange, zoomChange, rotationChange)
//                event.changes.forEach { it.consume() }
//            }
//        } while (!canceled && event.changes.any { it.pressed })
//
//        onGestureEnd()
//    }
//}

suspend fun PointerInputScope.detectTransformGesturesWithEnd(
    onGestureEnd: (velocity: Offset) -> Unit = {},
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
) {
    awaitEachGesture {
        var zoom = 1f
        var rotation = 0f
        var pan = Offset.Zero

        val velocityTracker = VelocityTracker()

        val down = awaitFirstDown(requireUnconsumed = false)
        velocityTracker.addPosition(down.uptimeMillis, down.position)

        var pointerCount: Int

        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.any { it.isConsumed }

            if (!canceled) {
                val zoomChange = event.calculateZoom()
                val rotationChange = event.calculateRotation()
                val panChange = event.calculatePan()
                val centroid = event.calculateCentroid()

                val timeMillis = event.changes.firstOrNull()?.uptimeMillis
                if (timeMillis != null) {
                    velocityTracker.addPosition(timeMillis, centroid)
                }

                if (!zoomChange.isNaN() && !rotationChange.isNaN()) {
                    onGesture(centroid, panChange, zoomChange, rotationChange)
                }
            }

            pointerCount = event.changes.count { it.pressed }
        } while (pointerCount > 0)

        val velocity = velocityTracker.calculateVelocity()
        onGestureEnd(Offset(velocity.x, velocity.y))
    }
}

