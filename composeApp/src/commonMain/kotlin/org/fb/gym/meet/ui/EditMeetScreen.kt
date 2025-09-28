package org.fb.gym.meet.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditMeetScreen() {
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateMeetScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Meet") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                }
            )
        }
    )
    {
    }
}