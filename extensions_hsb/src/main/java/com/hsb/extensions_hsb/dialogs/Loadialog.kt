package com.hsb.extensions_hsb.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.hsb.extensions_hsb.databinding.LoadingDialogBinding
import com.hsb.extensions_hsb.utils.permissionH.PermissionH

class Loadialog(activity: Activity) : Dialog(activity) {
    private var binding = LoadingDialogBinding.inflate(layoutInflater)
    val progressBar = binding.pbr

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
    }


    fun showDialog(text: String = "Loading...") {
        if (!isShowing) {
            binding.tvTitle.text = text
            show()
        }
    }

    fun dismissDialog() {
        if (isShowing) {
            dismiss()
        }
    }
}