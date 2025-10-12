package org.fb.gym.meet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gymmeet.composeapp.generated.resources.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.fb.gym.meet.data.Gymnast
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMeetScreen(
    state: State<EditMeetUiState>,
    actions: EditMeetActions,
    onNameChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onGymnastToggle: (String) -> Unit,
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.title_edit_meet)) },
                navigationIcon = {
                    IconButton(onClick = actions.onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Meet"
                        )
                    }
                    IconButton(
                        onClick = actions.onSave
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
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
                value = state.value.name,
                onValueChange = onNameChanged,
                label = { Text(stringResource(Res.string.label_meet_name)) },
                isError = state.value.nameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (state.value.nameError != null) {
                Text(
                    text = state.value.nameError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            OutlinedTextField(
                value = state.value.date,
                onValueChange = onDateChanged,
                label = { Text(stringResource(Res.string.label_meet_date)) },
                placeholder = { Text("28.09.2025") },
                isError = state.value.dateError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (state.value.dateError != null) {
                Text(
                    text = state.value.dateError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            // -------- Gymnast multi‑select --------------------------------------
            GymnastMultiSelect(
                allGymnasts = state.value.allGymnasts,
                selectedIds = state.value.selectedGymnastIds,
                onToggle = onGymnastToggle,
                onCreateGymnast = actions.onCreateGymnast,
                onEditGymnast = actions.onEditGymnast,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    DeleteConfirmDialog(
        visible = showDeleteDialog,
        itemName = state.value.name,
        title = Res.string.dialog_meet_delete_title,
        onConfirm = {
            showDeleteDialog = false
            actions.onDeleteMeet()
        },
        onDismiss = { showDeleteDialog = false }
    )
}

@Composable
fun GymnastMultiSelect(
    allGymnasts: List<Gymnast>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    onCreateGymnast: () -> Unit,
    onEditGymnast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.label_meet_gymnasts),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { onCreateGymnast() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Gymnast"
                )
                Spacer(Modifier.width(4.dp))
                Text(stringResource(Res.string.button_add))
            }
        }

        // If there are no gymnasts at all, show a hint
        if (allGymnasts.isEmpty()) {
            Text(
                text = stringResource(Res.string.meet_no_gymnasts),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
            return
        }

        // Scrollable list of check‑boxes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.heightIn(max = 250.dp) // limit height, scroll if needed
        ) {
            items(allGymnasts, key = { it.id }) { gymnast ->
                GymnastRow(gymnast, selectedIds, onToggle, onEditGymnast)
            }
        }
    }
}

@Composable
private fun GymnastRow(
    gymnast: Gymnast,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    onEditGymnast: (String) -> Unit
) {
    val isChecked = gymnast.id in selectedIds
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(gymnast.id) }
                .padding(vertical = 4.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onToggle(gymnast.id) }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(
                    Res.string.meet_gymnast,
                    gymnast.firstName,
                    gymnast.lastName,
                    stringResource(gymnast.category.toStringRes())
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)   // pushes the button to the end
            )
            // ---- Edit button ----
            IconButton(
                onClick = { onEditGymnast(gymnast.id) },
                // Give the button a slightly larger hit‑area for accessibility
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,   // pencil icon
                    contentDescription = "Edit ${gymnast.firstName} ${gymnast.lastName}"
                )
            }
        }
    }
}

data class EditMeetUiState(
    val name: String = "",
    val date: String = today(),               // formatted as DD.MM.YYYY
    val nameError: String? = null,
    val dateError: String? = null,
    /** IDs of the gymnasts that are currently selected */
    val selectedGymnastIds: Set<String> = emptySet(),
    /** Full list of gymnasts – supplied by the ViewModel */
    val allGymnasts: List<Gymnast> = emptyList()
)

data class EditMeetActions(
    val onBack: () -> Unit = {},
    /** Called when the user taps “Save”. */
    val onSave: () -> Unit = {},
    /** Called when the user wants to add a brand‑new gymnast (navigate). */
    val onCreateGymnast: () -> Unit = {},
    val onEditGymnast: (String) -> Unit = {},
    val onDeleteMeet: () -> Unit = {},
)

@OptIn(ExperimentalTime::class)
fun today(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dd = now.day.toString().padStart(2, '0')
    val mm = now.month.number.toString().padStart(2, '0')
    val yyyy = now.year.toString()
    return "$dd.$mm.$yyyy"
}