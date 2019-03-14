package com.ravi.android.face.detection.util

import android.app.Activity
import android.content.Context
import android.widget.Toast


fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, text, duration).apply { show() }
}

fun Activity.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, text, duration).apply { show() }
}
