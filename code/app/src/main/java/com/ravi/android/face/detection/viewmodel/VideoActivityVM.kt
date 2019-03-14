package com.ravi.android.face.detection.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.otaliastudios.cameraview.Facing
import com.ravi.android.face.detection.data.repository.FirebaseVisionRepository
import com.ravi.android.face.detection.data.repository.PaintRepository
import com.ravi.android.face.detection.data.repository.VideoRepository

class VideoActivityVM(application: Application) : AndroidViewModel(application) {

    private val dotPaint by lazy { PaintRepository.getDotPaints() }
    private val linePaint by lazy { PaintRepository.getLinePaints() }

    private val videoRepository: VideoRepository by lazy {
        VideoRepository(
            linePaint,
            dotPaint
        )
    }

    val faceDetector: FirebaseVisionFaceDetector by lazy {
        FirebaseVisionRepository.getFaceDetectors(
            FirebaseVisionRepository.getFirebaseVideoOptions()
        )
    }

    var cameraFacing: Facing = Facing.FRONT

    fun getVisionMetaData(width: Int, height: Int): FirebaseVisionImageMetadata =
        FirebaseVisionRepository.getVisionMetaData(width, height, cameraFacing)

    fun getContourBitmap(width: Int, height: Int, faceList: MutableList<FirebaseVisionFace>): Bitmap {
        val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)


        for (face in faceList) {

            //Face Boundaries
            val faceContours: MutableList<FirebaseVisionPoint> = face.getContour(
                FirebaseVisionFaceContour.FACE
            ).points
            videoRepository.drawFaceCounters(canvas, faceContours)

            //Left Eyebrow
            val leftEyebrowTopContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).points
            videoRepository.drawContours(canvas, leftEyebrowTopContours)

            val leftEyebrowBottomContours =
                face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).points
            videoRepository.drawContours(canvas, leftEyebrowBottomContours)

            //Right Eyebrow
            val rightEyebrowTopContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).points
            videoRepository.drawContours(canvas, rightEyebrowTopContours)

            val rightEyebrowBottomContours =
                face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).points
            videoRepository.drawContours(canvas, rightEyebrowBottomContours)

            //Eyes
            val leftEyeContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
            videoRepository.drawEyeContours(canvas, leftEyeContours)

            val rightEyeContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points
            videoRepository.drawEyeContours(canvas, rightEyeContours)

            //Upper Lips
            val upperLipTopContours = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points
            videoRepository.drawContours(canvas, upperLipTopContours)

            val upperLipBottomContours = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points
            videoRepository.drawContours(canvas, upperLipBottomContours)

            //Bottom Lips
            val lowerLipTopContours = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).points
            videoRepository.drawContours(canvas, lowerLipTopContours)

            val lowerLipBottomContours = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points
            videoRepository.drawContours(canvas, lowerLipBottomContours)

            //Nose
            val noseBridgeContours = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points
            videoRepository.drawContours(canvas, noseBridgeContours)

            val noseBottomContours = face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points
            videoRepository.drawContours(canvas, noseBottomContours)

        }

        return bitmap
    }


}

