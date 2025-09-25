package org.fb.gym.meet.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fb.gym.meet.data.Gymnast
import org.fb.gym.meet.data.Score

@Composable
fun ScoreScreen(
    gymnast: Gymnast,
    score: Score,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Simple header – you could replace this with TopAppBar later
        Text(
            text = gymnast.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
//        LazyColumn(
//            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
//        ) {
//            items(gymnasts) { gymnast ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(vertical = 4.dp),
//                    onClick = onBack
//                ) {
//                    Text(
//                        text = gymnast.name,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(12.dp)
//                    )
//                }
//            }
//        }
    }
}