package com.ravi.android.face.detection.data.repository

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

object PaintRepository {
    fun getDotPaints() = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 4F
    }

    fun getLinePaints() = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 2F
    }

    fun getFacePaint() = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6F
        color = Color.RED
    }

    fun getFaceTextPaint() = Paint().apply {
        textSize = 20F
        typeface = Typeface.DEFAULT_BOLD
        color = Color.RED
    }

    fun getLandmarkPaint() = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 8F

    }


}