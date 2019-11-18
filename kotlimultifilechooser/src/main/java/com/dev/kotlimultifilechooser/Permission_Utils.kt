package com.all.media.kotlin


import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by rashid on 11/6/17.
 */

fun Context.IsVersionAbove(Version: Int) = Build.VERSION.SDK_INT >= Version

//***************** Is Marshmallow OR Above **************************
fun Context.IsMarshmallowORAbove() = IsVersionAbove(Build.VERSION_CODES.M)

private var onAccepted: (() -> Unit)? = null
private var onDenied: (() -> Unit)? = null

fun Activity.onPermissionsResult(grantResults: IntArray) {
    if (grantResults.isNotEmpty()) {
        val result = grantResults.reduce { initialValue, item ->
            initialValue + item
        }
        if (result == PackageManager.PERMISSION_GRANTED) {
            onAccepted?.invoke()
        } else {
            onDenied?.invoke()
        }
    } else {
        onDenied?.invoke()
    }
}

//***************** Is Permission Granted ****************************
fun Activity.IsPermissionGranted(
    vararg permissions: String,
    onPermissionAccepted: (() -> Unit),
    onPermissionDenied: (() -> Unit)
): Boolean {
    onAccepted = onPermissionAccepted
    onDenied = onPermissionDenied
    return if (IsMarshmallowORAbove()) {
        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions, 1000)
            false
        } else {
            onAccepted?.invoke()
            true
        }
    } else {
        onAccepted?.invoke()
        true
    }
}

//******************** Is Contact Read Permission ***********************
fun Activity.isContactsReadAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.READ_CONTACTS,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//******************** Is Contacts Write Permission **********************
fun Activity.IsContactsWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_CONTACTS,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}
//******************** Is Contacts READ WRITE Permission ******************

fun Activity.IsContactsReadWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_CONTACTS,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}
//******************* Is Calendar Read Permission *************************

fun Activity.IsCalenderReadAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.READ_CALENDAR,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//****************** Calendar Write Permission ****************************
fun Activity.IsCalendarWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_CALENDAR,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//***************** Calendar Read Write Permission *************************
fun Activity.IsCalendarReadWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.READ_CALENDAR,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}
//***************** Storage Read Permission *******************************

fun Activity.IsStorageReadAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}
//******************* Storage Write Permission ***************************

fun Activity.IsStorageWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}
//******************* Storage Read Write Permission **********************

fun Activity.IsStorageReadWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}
//******************* Location Permission *******************************

fun Activity.IsLocationAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}
//******************* Camera Permission ********************************

fun Activity.IsCameraAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.CAMERA,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//******************* Microphone Permission *****************************
fun Activity.IsMicrophoneAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.RECORD_AUDIO,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//******************* Phone Read State *********************************
fun Activity.IsPhoneReadStateAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.READ_PHONE_STATE,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//******************* Call Permission ************************************
fun Activity.IsCallAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.CALL_PHONE,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//****************** Read Call Log Permission ****************************
fun Activity.IsCallLogReadAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.READ_CALL_LOG,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//******************* Write Call Log Permission **************************
fun Activity.IsCallLogWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_CALL_LOG,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//******************* Read Write Call Log *******************************
fun Activity.IsCallLogReadWriteAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.READ_CALL_LOG,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

fun Activity.IsSMSAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.SEND_SMS,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

fun AppCompatActivity.IsLockScreenAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.DISABLE_KEYGUARD,
        Manifest.permission.WAKE_LOCK,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

fun Activity.IsLockScreenAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return IsPermissionGranted(
        Manifest.permission.DISABLE_KEYGUARD,
        Manifest.permission.WAKE_LOCK,
        onPermissionAccepted = onPermissionAccepted,
        onPermissionDenied = onPermissionDenied
    )
}

//********************* Fragments Permission ********************

fun Fragment.IsLocationAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return activity.IsLocationAllow(onPermissionAccepted, onPermissionDenied)
}

fun Fragment.IsSMSAllow(onPermissionAccepted: (() -> Unit), onPermissionDenied: (() -> Unit)): Boolean {
    return activity.IsSMSAllow(onPermissionAccepted, onPermissionDenied)
}
