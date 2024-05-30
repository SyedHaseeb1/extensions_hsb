package com.hsb.extensions_hsb.utils.permissionH

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

/**
 * Nov 14, 2023
 * Developed by Syed Haseeb
 * Github: https://github.com/syedhaseeb1
 *
 * Updated on Jan 08, 2024
 */
object PermissionH {
    fun checkRunTimePermissions(
        permission: List<String>,
        activity: Activity,
        callback: (Boolean) -> Unit,
    ) {
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
        deniedMsg: String = "Core fundamental are based on these permissions",
        forwardToSettingsMsg: String = "You need to allow necessary permissions in Settings manually",
        positiveBtnText: String = "OK",
        negativeBtnText: String = "Cancel",
        click: ((isGranted: Boolean) -> Unit)? = null,
    ) {
        PermissionX.init(activity)
            .permissions(permissions)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList, deniedMsg,
                    positiveBtnText,
                    negativeBtnText
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    forwardToSettingsMsg,
                    positiveBtnText,
                    negativeBtnText
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    click?.invoke(true)
                } else {
                    click?.invoke(false)
                }
            }
    }
}