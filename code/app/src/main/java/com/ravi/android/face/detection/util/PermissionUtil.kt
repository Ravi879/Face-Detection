package com.ravi.android.face.detection.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ravi.android.face.detection.R
import java.lang.ref.WeakReference

class PermissionUtil(val a: Activity) {

    private val activityWeakReference = WeakReference<Activity>(a)

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        const val ACCESS_CAMERA = android.Manifest.permission.CAMERA
        const val ACCESS_RECORD_AUDIO = android.Manifest.permission.RECORD_AUDIO
    }

    fun isPermissionGranted(): Boolean {
        val activity = activityWeakReference.get()

        val cameraPermission = ContextCompat.checkSelfPermission(activity!!, ACCESS_CAMERA)
        val audioPermission = ContextCompat.checkSelfPermission(activity, ACCESS_RECORD_AUDIO)

        val listPermissionsNeeded = arrayListOf<String>()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(ACCESS_CAMERA)
        }
        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(ACCESS_RECORD_AUDIO)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun isUserGrantedPermission(permissions: Array<String>, grantResults: IntArray):Boolean {
        val activity = activityWeakReference.get()

        val perms = hashMapOf<String, Int>()
        // Initialize the map with both permissions
        perms[ACCESS_CAMERA] = PackageManager.PERMISSION_GRANTED
        perms[ACCESS_RECORD_AUDIO] = PackageManager.PERMISSION_GRANTED

        if (grantResults.isNotEmpty()) {
            for (i in 0 until permissions.size)
                perms[permissions[i]] = grantResults[i]

            if (perms[ACCESS_CAMERA] == PackageManager.PERMISSION_GRANTED
                && perms[ACCESS_RECORD_AUDIO] == PackageManager.PERMISSION_GRANTED
            ) {
                // process the normal flow
                return true
            } else {
                //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, ACCESS_CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        ACCESS_RECORD_AUDIO
                    )
                ) {
                    showDialogOK()
                }

                //permission is denied (and never ask again is  checked)
                else {
                    //toast("Go to settings and enable permissions", Toast.LENGTH_LONG)
                    showSettingDialog()
                }
            }
        }
        return false
    }

    private fun showSettingDialog() {
        val activity: Activity? = activityWeakReference.get()
        AlertDialog.Builder(activity!!)
            .setCancelable(false)
            .setMessage(R.string.permission_phone_rationale)
            .setPositiveButton("OPEN SETTING") { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

                activity.startActivity(intent)

            }
            .show()
    }

    private fun showDialogOK() {
        val activity: Activity? = activityWeakReference.get()
        AlertDialog.Builder(activity!!)
            .setMessage(R.string.permission_ask_again)
            .setPositiveButton("TRY AGAIN") { _, _ ->
                isPermissionGranted()
            }
            .create()
            .show()
    }

}