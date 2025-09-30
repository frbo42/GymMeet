package org.fb.gym.meet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fb.gym.meet.data.Gymnast
import org.fb.gym.meet.data.Meet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantScreen(
    meet: Meet?,
    gymnasts: Collection<Gymnast>,
    actions: ParticipantActions,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (meet == null) "No meet selected" else "Gymnasts: ${meet.name}") },
                navigationIcon = {
                    IconButton(onClick = actions.onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (meet != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val participatingGymnasts =
                    gymnasts.filter { gymnast -> meet.participants.map { p -> p.gymnastId }.contains(gymnast.id) }

                items(participatingGymnasts) { gymnast ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp)
                            .clickable { actions.onGymnastSelected(meet.id, gymnast.id) }
                    ) {
                        Text(
                            text = gymnast.firstName + " " + gymnast.lastName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

class ParticipantActions(
    val onBack: () -> Unit = {},
    val onGymnastSelected: (String, String) -> Unit
)