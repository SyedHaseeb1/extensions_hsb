package com.hsb.extensions_hsb.utils.permissionH

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.hsb.extensions_hsb.utils.globalextensions.Extensions.isAndroid13OrAbove
import com.permissionx.guolindev.PermissionX

/**
 * Nov 14, 2023
 * Developed by Syed Haseeb
 * Github: https://github.com/syedhaseeb1
 *
 * Updated on Jan 08, 2024
 * Updated on August 22, 2024
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

    fun requestPermissions(
        permissions: List<String>,
        activity: FragmentActivity,
        deniedMsg: String = "Core fundamental are based on these permissions",
        forwardToSettingsMsg: String = "You need to allow necessary permissions in Settings manually",
        positiveBtnText: String = "OK",
        negativeBtnText: String = "Cancel",
        click: ((isGranted: Boolean, allowedList: List<String>, deniedList: List<String>) -> Unit)? = null,
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
                    click?.invoke(true, grantedList, deniedList)
                } else {
                    click?.invoke(false, grantedList, deniedList)
                }
            }
    }

    fun requestGalleryPermission(
        activity: FragmentActivity,
        callback: ((isGranted: Boolean) -> Unit)
    ) {
        val permissionsList: ArrayList<String> = ArrayList()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionsList.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        }
        PermissionH.requestPermissions(
            permissionsList,
            activity
        ) { isGranted, allowedList, deniedList ->
            if (isGranted || allowedList.contains(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                callback.invoke(isGranted)
            }
        }
    }

    fun Context.doWeHaveMediaStoragePermission(): Boolean {
        return if (isAndroid13OrAbove()) {
            checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun Context.doWeHaveReadWritePermission(): Boolean {
        return if (isAndroid13OrAbove()) {
            true
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}