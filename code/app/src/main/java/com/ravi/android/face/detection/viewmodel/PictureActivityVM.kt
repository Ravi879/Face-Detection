package com.ravi.android.face.detection.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.ravi.android.face.detection.R
import com.ravi.android.face.detection.data.repository.FirebaseVisionRepository
import com.ravi.android.face.detection.data.repository.PaintRepository
import com.ravi.android.face.detection.domain.model.Features
import com.ravi.android.face.detection.viewmodel.contract.PictureActivityContract

class PictureActivityVM(application: Application) : AndroidViewModel(application) {

    private val facePaint by lazy { PaintRepository.getFacePaint() }
    private val faceTextPaint by lazy { PaintRepository.getFaceTextPaint() }
    private val landmarkPaint by lazy { PaintRepository.getLandmarkPaint() }

    var listFeatures: ArrayList<Features> = arrayListOf()

    lateinit var contract: PictureActivityContract

    private val circleRadius = 4F

    fun processVisionFaceList(faces: List<FirebaseVisionFace>, image: Bitmap) {

        val canvas = Canvas(image)

        val colors = getColors()
        var colorIndex = 0

        for ((index, face) in faces.withIndex()) {

            val features = Features()

            if (face.trackingId != FirebaseVisionFace.INVALID_ID) {
                features.apply {
                    trackingId = face.trackingId.toString()
                    leftEyeOpenProb = face.leftEyeOpenProbability.toString()
                    rightEyeOpenProb = face.rightEyeOpenProbability.toString()
                    smileProb = face.smilingProbability.toString()
                }

                if (colorIndex > 7)
                    colorIndex = 0

                features.color = colors[colorIndex]
                colorIndex += 1
            } else {
                features.color = Color.BLACK
            }

            facePaint.color = features.color
            faceTextPaint.color = features.color

            drawOnCanvas(index, canvas, face)

            listFeatures.add(features)
        }

        contract.notifyAdapter()
    }


    fun getFaceDetector() =
        FirebaseVisionRepository.getFaceDetectors(
            FirebaseVisionRepository.getFirebaseImageOptions()
        )


    private fun drawOnCanvas(index: Int, canvas: Canvas, face: FirebaseVisionFace) {
        canvas.drawRect(face.boundingBox, facePaint)
        canvas.drawText(
            "$index",
            (face.boundingBox.centerX() - face.boundingBox.width() / 2) + 8F,
            (face.boundingBox.centerY() + face.boundingBox.height() / 2) - 8F,
            faceTextPaint
        )

        drawCircles(canvas, face, circleRadius, landmarkPaint)
        drawLines(canvas, face)

    }

    private fun drawCircles(canvas: Canvas, face: FirebaseVisionFace, circleRadius: Float, landmarkPaint: Paint) {

        if (face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE) != null) {
            val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)!!
            canvas.drawCircle(leftEye.position.x, leftEye.position.y, circleRadius, landmarkPaint)
        }
        if (face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE) != null) {
            val rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)!!
            canvas.drawCircle(rightEye.position.x, rightEye.position.y, circleRadius, landmarkPaint)
        }
        if (face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE) != null) {
            val nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)!!
            canvas.drawCircle(nose.position.x, nose.position.y, circleRadius, landmarkPaint)
        }
        if (face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR) != null) {
            val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)!!
            canvas.drawCircle(leftEar.position.x, leftEar.position.y, circleRadius, landmarkPaint)
        }
        if (face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR) != null) {
            val rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)!!
            canvas.drawCircle(rightEar.position.x, rightEar.position.y, circleRadius, landmarkPaint)
        }

    }

    private fun drawLines(canvas: Canvas, face: FirebaseVisionFace) {
        if (face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT) != null && face.getLandmark(
                FirebaseVisionFaceLandmark.MOUTH_BOTTOM
            ) != null && face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT) != null
        ) {
            val leftMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)!!
            val bottomMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)!!
            val rightMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)!!
            canvas.drawLine(
                leftMouth.position.x,
                leftMouth.position.y,
                bottomMouth.position.x,
                bottomMouth.position.y,
                landmarkPaint
            )
            canvas.drawLine(
                bottomMouth.position.x,
                bottomMouth.position.y,
                rightMouth.position.x,
                rightMouth.position.y,
                landmarkPaint
            )
        }
    }

    private fun getColors(): Array<Int> {
        val context = getApplication<Application>()
        return arrayOf(
            ContextCompat.getColor(context, R.color.red),
            ContextCompat.getColor(context, R.color.yellow),
            ContextCompat.getColor(context, R.color.green),
            ContextCompat.getColor(context, R.color.maroon),
            ContextCompat.getColor(context, R.color.pink),
            ContextCompat.getColor(context, R.color.orange),
            ContextCompat.getColor(context, R.color.blue),
            ContextCompat.getColor(context, R.color.magenta),
            ContextCompat.getColor(context, R.color.lavender)
        )
    }

}

