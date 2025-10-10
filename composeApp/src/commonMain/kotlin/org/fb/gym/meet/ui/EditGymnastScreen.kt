package org.fb.gym.meet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.fb.gym.meet.data.Category
import org.fb.gym.meet.data.Gender
import org.fb.gym.meet.data.Gymnast
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGymnastScreen(
    /** Null → create new; non‑null → edit existing (id passed from navigation) */
    gymnastId: String? = null,
    vm: EditGymnastContract,
    /** Called when the user wants to go back (e.g., cancel). */
    onBack: () -> Unit = {},
    /** Called after a successful save – usually pop the back stack. */
    onSaved: () -> Unit = {},
    onDelete: () -> Unit = {}
) {

    val uiState by vm.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (gymnastId == null) "Create Gymnast" else "Edit Gymnast")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                        onClick = { vm.onSave(onSaved) }
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
            // ----- First name -------------------------------------------------
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = vm::onFirstNameChanged,
                label = { Text("First name") },
                isError = uiState.firstNameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.firstNameError != null) {
                Text(
                    text = uiState.firstNameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // ----- Last name --------------------------------------------------
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = vm::onLastNameChanged,
                label = { Text("Last name") },
                isError = uiState.lastNameError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.lastNameError != null) {
                Text(
                    text = uiState.lastNameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // ----- Gender (radio buttons) ------------------------------------
            Text("Gender", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.gender == Gender.M,
                    onClick = { vm.onGenderChanged(Gender.M) }
                )
                Text("Male", modifier = Modifier.padding(end = 16.dp))
                RadioButton(
                    selected = uiState.gender == Gender.F,
                    onClick = { vm.onGenderChanged(Gender.F) }
                )
                Text("Female")
            }

            // ----- Category (dropdown) ---------------------------------------
            Text("Category", style = MaterialTheme.typography.bodyMedium)
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(uiState.category.name)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Category.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                vm.onCategoryChanged(cat)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Meet") },
            text = {
                Text(
                    "Are you sure you want to delete \"${uiState.firstName}\"? " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.delete()
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@OptIn(ExperimentalUuidApi::class)
data class EditGymnastUiState(
    val firstName: String = "",
    val lastName: String = "",
    val gender: Gender = Gender.M,
    val category: Category = Category.C5,
    val firstNameError: String? = null,
    val lastNameError: String? = null
) {
    /** Helper to convert the UI state back into a Gymnast entity */
    fun toGymnast(existingId: String? = null): Gymnast = Gymnast(
        id = existingId ?: Uuid.random().toString(),
        firstName = firstName.trim(),
        lastName = lastName.trim(),
        gender = gender,
        category = category
    )
}