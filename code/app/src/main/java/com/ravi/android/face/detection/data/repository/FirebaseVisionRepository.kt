package com.ravi.android.face.detection.data.repository

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.otaliastudios.cameraview.Facing

object FirebaseVisionRepository {

    fun getFaceDetectors(options: FirebaseVisionFaceDetectorOptions): FirebaseVisionFaceDetector =
        FirebaseVision.getInstance().getVisionFaceDetector(options)


    fun getFirebaseVideoOptions(): FirebaseVisionFaceDetectorOptions =
        FirebaseVisionFaceDetectorOptions.Builder()
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()

    fun getFirebaseImageOptions() =
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .enableTracking()
            .build()


    fun getVisionMetaData(width: Int, height: Int, cameraFacing: Facing): FirebaseVisionImageMetadata =
        FirebaseVisionImageMetadata.Builder()
            .setWidth(width)
            .setHeight(height)
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(
                if (cameraFacing == Facing.FRONT)
                    FirebaseVisionImageMetadata.ROTATION_270
                else
                    FirebaseVisionImageMetadata.ROTATION_90
            )
            .build()

}