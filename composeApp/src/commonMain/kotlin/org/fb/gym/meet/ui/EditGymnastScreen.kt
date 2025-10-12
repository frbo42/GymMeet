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
import gymmeet.composeapp.generated.resources.*
import org.fb.gym.meet.data.Category
import org.fb.gym.meet.data.Gender
import org.fb.gym.meet.data.Gymnast
import org.jetbrains.compose.resources.stringResource
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
                    Text(
                        if (gymnastId == null)
                            stringResource(Res.string.title_create_gymnast)
                        else
                            stringResource(Res.string.title_edit_meet)
                    )
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
                label = { Text(stringResource(Res.string.label_gymnast_first_name)) },
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
                label = { Text(stringResource(Res.string.label_gymnast_last_name)) },
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
            Text(stringResource(Res.string.label_gymnast_gender), style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.gender == Gender.M,
                    onClick = { vm.onGenderChanged(Gender.M) }
                )
                Text(stringResource(Res.string.label_gymnast_male), modifier = Modifier.padding(end = 16.dp))
                RadioButton(
                    selected = uiState.gender == Gender.F,
                    onClick = { vm.onGenderChanged(Gender.F) }
                )
                Text(stringResource(Res.string.label_gymnast_female))
            }

            // ----- Category (dropdown) ---------------------------------------
            Text(stringResource(Res.string.label_gymnast_category), style = MaterialTheme.typography.bodyMedium)
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(uiState.category.toStringRes()))
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
                            text = { Text(stringResource(cat.toStringRes())) },
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
    DeleteConfirmDialog(
        visible = showDeleteDialog,
        itemName = uiState.firstName,
        title = Res.string.dialog_gymnast_delete_title,
        onConfirm = {
            showDeleteDialog = false
            vm.delete()
            onDelete()
        },
        onDismiss = { showDeleteDialog = false }
    )
}

fun Category.toStringRes() = when (this) {
    Category.C1 -> Res.string.category_c1
    Category.C2 -> Res.string.category_c2
    Category.C3 -> Res.string.category_c3
    Category.C4 -> Res.string.category_c4
    Category.C5 -> Res.string.category_c5
    Category.C6 -> Res.string.category_c6
    Category.C7 -> Res.string.category_c7
    Category.WOMEN_MEN -> Res.string.category_women_men
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