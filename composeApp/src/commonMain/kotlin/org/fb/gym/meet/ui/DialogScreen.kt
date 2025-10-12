package org.fb.gym.meet.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import gymmeet.composeapp.generated.resources.Res
import gymmeet.composeapp.generated.resources.button_cancel
import gymmeet.composeapp.generated.resources.dialog_delete_message
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteConfirmDialog(
    visible: Boolean,
    itemName: String,
    title: StringResource,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(title)) },
        text = {
            Text(stringResource(Res.string.dialog_delete_message, itemName))
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
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.button_cancel)) }
        }
    )
}