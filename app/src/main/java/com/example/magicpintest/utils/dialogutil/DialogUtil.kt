package com.example.magicpintest.utils.dialogutil

import android.content.Context
import com.example.magicpintest.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtil {
    fun showTechnicalDialog(context: Context) {
        MaterialAlertDialogBuilder(context, R.style.AlertDialogMaterialTheme)
            .setTitle(R.string.technical_dialog_title)
            .setMessage(R.string.technical_messgae)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}