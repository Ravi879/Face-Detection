package com.ravi.android.face.detection.data.repository

import android.graphics.Canvas
import android.graphics.Paint
import com.google.firebase.ml.vision.common.FirebaseVisionPoint

class VideoRepository(private val linePaint: Paint, private val dotPaint: Paint){

    fun drawFaceCounters(canvas: Canvas, points:MutableList<FirebaseVisionPoint>){
        for ((i, contour) in points.withIndex()) {
            if (i != points.lastIndex)
                canvas.drawLine(
                    contour.x,
                    contour.y,
                    points[i + 1].x,
                    points[i + 1].y,
                    linePaint
                )
            else
                canvas.drawLine(contour.x, contour.y, points[0].x, points[0].y, linePaint)
            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
        }

    }

    fun drawEyeContours(canvas: Canvas, points:MutableList<FirebaseVisionPoint>){
        for ((i, contour) in points.withIndex()) {
            if (i != points.lastIndex)
                canvas.drawLine(
                    contour.x,
                    contour.y,
                    points[i + 1].x,
                    points[i + 1].y,
                    linePaint
                )
            else
                canvas.drawLine(contour.x, contour.y, points[0].x, points[0].y, linePaint)
            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
        }
    }


    fun drawContours(canvas: Canvas, points: MutableList<FirebaseVisionPoint>){
        for ((i, contour) in points.withIndex()) {
            if (i != points.lastIndex)
                canvas.drawLine(
                    contour.x,
                    contour.y,
                    points[i + 1].x,
                    points[i + 1].y,
                    linePaint
                )
            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
        }

    }

}
