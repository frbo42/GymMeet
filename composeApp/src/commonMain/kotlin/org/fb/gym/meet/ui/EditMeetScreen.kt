package org.fb.gym.meet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.fb.gym.meet.data.Meet
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun EditMeetScreen() {
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Preview(showBackground = true)
@Composable
fun CreateMeetScreen(
    onBackClick: () -> Unit = {},
    /** Called with the newly created Meet when the user taps Save */
    onSaveClick: (Meet) -> Unit = {}
) {
    // -----------------------------------------------------------------
    // 1️⃣  UI state – rememberSaveable so it survives rotation / process kill
    // -----------------------------------------------------------------
    var name by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }          // simple text field for now
    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    // -----------------------------------------------------------------
    // 2️⃣  Helper that validates the inputs and, if ok, builds a Meet
    // -----------------------------------------------------------------
    fun trySave() {
        // Reset errors
        nameError = null
        dateError = null

        var valid = true

        if (name.isBlank()) {
            nameError = "Name cannot be empty"
            valid = false
        }

        // Very simple date validation – you can replace this with a proper date picker later
        val datePattern = Regex("""\d{4}-\d{2}-\d{2}""")   // YYYY‑MM‑DD
        if (!datePattern.matches(date)) {
            dateError = "Enter a date like 2025-09-28"
            valid = false
        }

        if (valid) {
            // Generate a UUID for the meet ID (multiplatform)
            val id = Uuid.random().toString()
            val newMeet = Meet(
                id = id,
                name = name.trim(),
                date = date.trim(),
                gymnasts = emptyList()   // we’ll add gymnasts later
            )
            onSaveClick(newMeet)
        }
    }

    // -----------------------------------------------------------------
    // 3️⃣  Scaffold with TopAppBar (Back on the left, Save on the right)
    // -----------------------------------------------------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Meet") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = ::trySave) {
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        // -----------------------------------------------------------------
        // 4️⃣  Form content – simple Column with two TextFields
        // -----------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)          // respect Scaffold insets
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ----- Meet name -------------------------------------------------
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Meet name") },
                isError = nameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError != null) {
                Text(
                    text = nameError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // ----- Meet date -------------------------------------------------
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY‑MM‑DD)") },
                placeholder = { Text("2025-09-28") },
                isError = dateError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (dateError != null) {
                Text(
                    text = dateError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}