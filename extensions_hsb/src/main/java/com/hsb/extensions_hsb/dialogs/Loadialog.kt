package com.hsb.extensions_hsb.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.hsb.extensions_hsb.databinding.LoadialogViewBinding

/**
 * Nov 14, 2023
 * Developed by Syed Haseeb
 * Github: https://github.com/syedhaseeb1
 */
class Loadialog(activity: Activity) : Dialog(activity) {
    private var binding = LoadialogViewBinding.inflate(layoutInflater)
    val progressBar = binding.pbr
    private var isCircular = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
    }

    fun setProgress(max: Int, progress: Int) {
        progressBar.apply {
            this.max = max
            this.progress = progress
            if (isCircular) {
                toggleProgressBarStyle()
            }
        }
    }

    private fun toggleProgressBarStyle() {
        if (isCircular) {
            // Change to horizontal style
            progressBar.isIndeterminate = false
            progressBar.setProgressDrawableTiled(null) // Clear previous drawable
            progressBar.progressDrawable =
                ContextCompat.getDrawable(context, android.R.drawable.progress_horizontal)
        } else {
            // Change to circular style
            progressBar.isIndeterminate = true
            progressBar.setProgressDrawableTiled(null) // Clear previous drawable
            progressBar.progressDrawable = ContextCompat.getDrawable(
                context,
                android.R.drawable.progress_indeterminate_horizontal
            )
        }
        isCircular = !isCircular
    }

    fun setLoadingText(text: String) {
        binding.tvTitle.text = text
    }

    fun showDialog(text: String = "Loading...") {
        if (!isShowing) {
            binding.tvTitle.text = text
            if (!isCircular) {
                isCircular = true
                toggleProgressBarStyle()
            }
            show()
        }
    }

    fun dismissDialog() {
        if (isShowing) {
            dismiss()
        }
    }
}