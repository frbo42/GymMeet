package org.fb.gym.meet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fb.gym.meet.data.Meet
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MeetScreen(
    meets: List<Meet> = listOf(Meet("1", "Meet 1", "2023-01-01")),
    onClick: (String) -> Unit = {},
    onNewClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meets") },
                actions = {
                    IconButton(onClick = { onNewClick() }) {
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
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
private fun MeetRow(
    meet: Meet,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(meet.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = meet.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}