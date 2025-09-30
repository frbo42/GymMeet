package org.fb.gym.meet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fb.gym.meet.data.Meet
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MeetScreen(
    meets: List<Meet> = listOf(Meet("1", "Meet 1", "2023-01-01")),
    actions: MeetActions = MeetActions()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meets") },
                actions = {
                    IconButton(onClick = { actions.onCreateMeet() }) {
                        Icon(
                            imageVector = Icons.Default.Add, contentDescription = "New"
                        )
                    }
                }
            )
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(meets, key = { it.id }) { meet ->
                MeetRow(
                    meet = meet,
                    onSelect = actions.onSelectMeet,
                    onEdit = actions.onEditMeet
                )
            }
        }
    }
}

@Composable
private fun MeetRow(
    meet: Meet,
    onSelect: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(meet.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ---- Title (takes the remaining space) ----
            Text(
                text = meet.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)   // pushes the button to the end
            )

            // ---- Edit button ----
            IconButton(
                onClick = { onEdit(meet.id) },
                // Give the button a slightly larger hit‑area for accessibility
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,   // pencil icon
                    contentDescription = "Edit ${meet.name}"
                )
            }
        }
    }
}

data class MeetActions(
    val onSelectMeet: (String) -> Unit = {},
    val onCreateMeet: () -> Unit = {},
    val onEditMeet: (String) -> Unit = {}
)