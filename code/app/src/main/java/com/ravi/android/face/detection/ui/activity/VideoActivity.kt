package com.ravi.android.face.detection.ui.activity

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.Facing
import com.otaliastudios.cameraview.Frame
import com.otaliastudios.cameraview.FrameProcessor
import com.ravi.android.face.detection.R
import com.ravi.android.face.detection.util.PermissionUtil
import com.ravi.android.face.detection.viewmodel.VideoActivityVM
import kotlinx.android.synthetic.main.content_act_video.*
import kotlinx.android.synthetic.main.layout_app_bar.*


class VideoActivity : AppCompatActivity(), FrameProcessor {

    private lateinit var imageView: ImageView

    private lateinit var viewModel: VideoActivityVM
    private lateinit var cameraView: CameraView

    private var bitmap: Bitmap? = null
    private lateinit var frameProcessor: FrameProcessor

    private var isFrameProcessing = false

    private lateinit var permissionUtil: PermissionUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        setToolBar()
        imageView = findViewById<View>(R.id.lay_content_act_video).findViewById(R.id.face_detection_canvas_image_view)
        cameraView = findViewById<View>(R.id.lay_content_act_video).findViewById(R.id.face_detection_camera_view)

        permissionUtil = PermissionUtil(this)

        initViewModel()
    }

    private fun setToolBar() {
        val appBar: Toolbar = toolbar as Toolbar
        val mTitle = appBar.findViewById(R.id.toolbar_title) as TextView
        title = ""

        setSupportActionBar(appBar)
        mTitle.text = getString(R.string.title_activity_video)

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(VideoActivityVM::class.java)
    }

    override fun onStart() {
        super.onStart()

        if (permissionUtil.isPermissionGranted()) {
            initCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            PermissionUtil.REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val isGranted = permissionUtil.isUserGrantedPermission(permissions, grantResults)
                if (isGranted) {
                    initCamera()
                }
            }
        }

    }

    private fun initCamera() {
        cameraView.facing = viewModel.cameraFacing
        frameProcessor = this
        cameraView.addFrameProcessor(frameProcessor)
        cameraView.setLifecycleOwner(this)
        cameraView.open()
    }

    override fun onPause() {
        super.onPause()
        cameraView.close()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_video_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_switch_camera -> {

                isFrameProcessing = true
                imageView.setImageBitmap(null)
                imageView.setImageDrawable(null)

                if (bitmap != null && bitmap!!.isRecycled) {
                    bitmap!!.recycle()
                }
                bitmap = null

                viewModel.cameraFacing = if (viewModel.cameraFacing == Facing.FRONT) Facing.BACK else Facing.FRONT
                cameraView.facing = viewModel.cameraFacing

                isFrameProcessing = false

                true
            }

            else -> false
        }
    }

    override fun process(frame: Frame) {
        if (isFrameProcessing)
            return

        isFrameProcessing = true

        val width = frame.size.width
        val height = frame.size.height

        val firebaseVisionImage =
            FirebaseVisionImage.fromByteArray(frame.data, viewModel.getVisionMetaData(width, height))

        viewModel.faceDetector.detectInImage(firebaseVisionImage)
        viewModel.faceDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener { faceList ->
                imageView.setImageBitmap(null)

                if (bitmap != null) {
                    bitmap!!.recycle()
                    bitmap = null
                }

                bitmap = viewModel.getContourBitmap(width, height, faceList)

                if (viewModel.cameraFacing == Facing.FRONT) {

                    val matrix = Matrix()
                    matrix.preScale(-1F, 1F)
                    val flippedBitmap =
                        Bitmap.createBitmap(bitmap!!, 0, 0, bitmap!!.width, bitmap!!.height, matrix, true)
                    imageView.setImageBitmap(flippedBitmap)

                    if (flippedBitmap != bitmap) {
                        bitmap!!.recycle()
                        bitmap = null
                    }

                } else {
                    imageView.setImageBitmap(bitmap)
                }

                isFrameProcessing = false
            }
            .addOnFailureListener {
                face_detection_canvas_image_view.setImageBitmap(null)
                isFrameProcessing = false
            }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()


        cameraView.removeFrameProcessor(frameProcessor)
        cameraView.destroy()

        bitmap?.recycle()
        bitmap = null
    }

}