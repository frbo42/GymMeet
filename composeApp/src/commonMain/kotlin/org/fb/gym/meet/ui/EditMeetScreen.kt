package org.fb.gym.meet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.fb.gym.meet.data.Meet
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
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
    onSaveClick: (Meet) -> Unit = {}
) {

    var name by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(today()) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    fun trySave() {
        nameError = null
        dateError = null
        var ok = true

        if (name.isBlank()) {
            nameError = "Name cannot be empty"
            ok = false
        }

        val datePattern = Regex("""\d{2}\.\d{2}\.\d{4}""")   // DD.MM.YYYY
        if (!datePattern.matches(date)) {
            dateError = "Enter a date like 28.09.2025"
            ok = false
        }

        if (ok) {
            // Generate a UUID (multiplatform)
            val id = Uuid.random().toString()
            val meet = Meet(
                id = id,
                name = name.trim(),
                date = date.trim(),
                gymnasts = emptyList()
            )
            onSaveClick(meet)
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Meet name") },
                isError = nameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
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

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (DD.MM.YYYY)") },
                placeholder = { Text("28.09.2025") },
                isError = dateError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
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

@OptIn(ExperimentalTime::class)
fun today(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dd = now.day.toString().padStart(2, '0')
    val mm = now.month.number.toString().padStart(2, '0')
    val yyyy = now.year.toString()
    return "$dd.$mm.$yyyy"

}