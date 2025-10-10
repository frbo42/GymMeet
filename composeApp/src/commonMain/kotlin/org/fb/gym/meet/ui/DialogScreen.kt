package org.fb.gym.meet.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun DeleteConfirmDialog(
    visible: Boolean,
    itemName: String,
    title: String = "Delete",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Text(
                "Are you sure you want to delete \"$itemName\"? " +
                        "This action cannot be undone."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}