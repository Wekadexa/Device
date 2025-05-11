package com.example.devicelockcompanion.dialogs

import android.app.AlertDialog
import android.content.Context
import com.example.devicelockcompanion.R

/**
 * Helper class for creating confirmation dialogs.
 */
object ConfirmationDialog {
    /**
     * Show a confirmation dialog with the given message and callbacks.
     * @param context The context to use for the dialog
     * @param message The message to display in the dialog
     * @param positiveAction Action to perform when the user confirms
     */
    fun show(
        context: Context, 
        title: String, 
        message: String, 
        positiveAction: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.proceed) { _, _ -> 
                positiveAction() 
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> 
                dialog.dismiss() 
            }
            .setCancelable(true)
            .show()
    }
}