package com.example.devicelockcompanion.dialogs

import android.app.AlertDialog
import android.content.Context
import com.example.devicelockcompanion.R

/**
 * Dialog for confirming potentially dangerous operations
 */
object ConfirmationDialog {
    /**
     * Show a confirmation dialog with the specified title and message
     * 
     * @param context The context to show the dialog in
     * @param title The title of the dialog
     * @param message The message to display
     * @param onConfirm Called when the user confirms the action
     */
    fun show(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.proceed) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            
        builder.create().show()
    }
}