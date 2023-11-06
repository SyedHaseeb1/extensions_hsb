package com.hsb.extensions_hsb.utils.permissionH

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

class PermissionH {
    fun checkRunTimePermissions(permission: List<String>, activity: Activity,callback: (Boolean) -> Unit) {
        permission.forEach { perm ->
            val result = ContextCompat.checkSelfPermission(activity, perm)
            if (result == PackageManager.PERMISSION_GRANTED) {
                callback.invoke(true)
                println("Permission granted")
            } else {
                callback.invoke(false)
                // Permission is not granted
                println("Permission not granted")
            }
        }
    }

    fun requestPermissions(
        permissions: List<String>,
        activity: FragmentActivity,
        click: ((Boolean) -> Unit)? = null
    ) {
        PermissionX.init(activity)
            .permissions(permissions)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    click?.invoke(true)
                } else {
                }
            }
    }
}