package com.ravi.android.face.detection.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.ravi.android.face.detection.R
import com.ravi.android.face.detection.ui.adapter.FeatureAdapter
import com.ravi.android.face.detection.util.toast
import com.ravi.android.face.detection.viewmodel.PictureActivityVM
import com.ravi.android.face.detection.viewmodel.contract.PictureActivityContract
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_picture.*
import kotlinx.android.synthetic.main.layout_app_bar.*

class PictureActivity : AppCompatActivity(), PictureActivityContract {
    private val imageView by lazy { findViewById<ImageView>(R.id.face_detection_image_view)!! }

    private val btnSelectImg by lazy { findViewById<ImageView>(R.id.btn_select_image)!! }
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(findViewById(R.id.bottom_sheet)!!) }
    private val listView by lazy { findViewById<ListView>(R.id.listView) }

    private lateinit var adapter: FeatureAdapter

    private lateinit var viewModel: PictureActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)

        setToolBar()

        initViewModel()

        adapter = FeatureAdapter(viewModel.listFeatures)
        listView.adapter = adapter

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(PictureActivityVM::class.java)
        viewModel.contract = this
    }

    override fun onStart() {
        super.onStart()

        btnSelectImg.setOnClickListener {
            selectImage()
        }

        isGooglePlayServicesAvailable(this)
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {
                val imageUri = result.uri
                val image = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                if (image == null) {
                    toast("Error occurred. Please try again.")
                    return
                }

                val mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)

                image.recycle()


                analyzeImage(mutableImage)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                toast("Error occurred. : ${result.error.message}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picture_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.action_video -> {
                startActivity(Intent(this, VideoActivity::class.java))
                return true
            }
        }

        return false
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            super.onBackPressed()
        }
    }

    override fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }

    private fun analyzeImage(image: Bitmap) {

        showLoadingView()

        setImageBitmap(null)

        viewModel.listFeatures.clear()
        notifyAdapter()

        setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)

        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(image)

        viewModel.getFaceDetector().detectInImage(firebaseVisionImage)
            .addOnSuccessListener {

                if (it == null) {
                    toast("Error occurred.. Please try again.")
                    return@addOnSuccessListener
                }


                viewModel.processVisionFaceList(it, image)

                setImageBitmap(image)
                hideLoadingDialog()

                setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
            }
            .addOnFailureListener {
                toast("Error occurred. Please try again.")
                hideLoadingDialog()
            }
    }

    private fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)

        return when (status) {

            ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ->
                if (googleApiAvailability.getApkVersion(this) >= GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE)
                    true
                else {
                    showErrorDialog("Google play service update required.")
                    false
                }

            ConnectionResult.SUCCESS -> true

            else -> {
                if (googleApiAvailability.isUserResolvableError(status)) {
                    googleApiAvailability.getErrorDialog(activity, status, 2404).show()
                }
                false
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)

            .setCancelable(false)
            .setPositiveButton("EXIT") { _, _ ->
                finish()
            }
            .create()
            .show()
    }

    private fun setBottomSheetState(state: Int) {
        bottomSheetBehavior.state = state
    }

    private fun setImageBitmap(bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
    }

    private fun showLoadingView() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loading_indicator.visibility = View.VISIBLE
    }

    private fun hideLoadingDialog() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loading_indicator.visibility = View.GONE
    }

    private fun selectImage() {
        CropImage.activity().start(this)
    }

    private fun setToolBar() {
        val appBar = toolbar as Toolbar
        val mTitle = appBar.findViewById(R.id.toolbar_title) as TextView
        title = ""

        setSupportActionBar(appBar)
        mTitle.text = getString(R.string.title_activity_picture)

    }
}