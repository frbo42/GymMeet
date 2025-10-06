package org.fb.gym.meet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.fb.gym.meet.data.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    gymnast: Gymnast? = Gymnast(),
    viewModel: ScoreContract,
    onBackClick: () -> Unit = {},
) {
    // Keep a mutable copy locally – this is the source of truth for the UI
    val scoreCard by viewModel.scoreCard.collectAsState()

    // Helper to propagate changes upward (e.g., to a ViewModel)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gymnast?.firstName + " " + gymnast?.lastName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (scoreCard == null || gymnast == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No score yet for this gymnast.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            ScoreCardContent(
                innerPadding,
                gymnast,
                scoreCard!!,
                viewModel
            )
        }
    }
}

@Composable
private fun ScoreCardContent(
    innerPadding: PaddingValues,
    gymnast: Gymnast,
    scoreCard: ScoreCard,
    viewModel: ScoreContract
) {
    fun updateCard(updated: ScoreCard) = viewModel.updateScoreCard(updated)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ----- FLOOR ----------------------------------------------------
        ScoreRow(
            icon = Icons.Default.FitnessCenter, // replace with a floor‑icon
            label = "Floor",
            score = scoreCard.floor,
            onScoreChanged = { updateCard(scoreCard.copy(floor = it)) }
        )

        // ----- RINGS ----------------------------------------------------
        ScoreRow(
            icon = Icons.Default.SportsBar, // replace with a rings‑icon
            label = "Rings",
            score = scoreCard.rings,
            onScoreChanged = { updateCard(scoreCard.copy(rings = it)) }
        )

        // ----- VAULT ----------------------------------------------------
        VaultRow(
            vault = scoreCard.vault,
            onVaultChanged = { updateCard(scoreCard.copy(vault = it)) }
        )

        // ----- PARALLEL BARS --------------------------------------------
        if (gymnast.gender == Gender.M) {
            ScoreRow(
                icon = Icons.Default.SportsGymnastics, // replace with parallel‑bars icon
                label = "Parallel Bars",
                score = scoreCard.parallel,
                onScoreChanged = { updateCard(scoreCard.copy(parallel = it)) },
                modifier = Modifier.testTag("parallelBarRow"),
            )
        }
        // ----- HORIZONTAL BAR -------------------------------------------
        ScoreRow(
            icon = Icons.Default.Sports, // replace with horizontal‑bar icon
            label = "Horizontal Bar",
            score = scoreCard.bar,
            onScoreChanged = { updateCard(scoreCard.copy(bar = it)) }
        )
        // ----- total -------
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sports,
                    contentDescription = "Total Score icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 12.dp)
                )
                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                )
                Text(
                    text = scoreCard.total().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .widthIn(min = 96.dp)
                        .padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun VaultRow(
    vault: VaultScore,
    onVaultChanged: (VaultScore) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Vault icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))

            Text(
                text = "Vault",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // first field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {

                ScoreInput(
                    score = vault.firstJump,
                    // commit only on focus lost or clear
                    onCommitScore = { score ->
                        onVaultChanged(vault.copy(firstJump = score))
                    },
                    label = "1st"
                )

                Spacer(Modifier.width(8.dp))

                ScoreInput(
                    score = vault.secondJump,
                    // commit only on focus lost or clear
                    onCommitScore = { score ->
                        onVaultChanged(vault.copy(secondJump = score))
                    },
                    label = "2nd"
                )
            }
        }
    }
}

@Composable
private fun ScoreRow(
    icon: ImageVector,
    label: String,
    score: Score,
    onScoreChanged: (Score) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$label icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 12.dp)
            )
            Spacer(Modifier.width(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            )

            ScoreInput(
                score = score,
                onCommitScore = onScoreChanged,
                label = label
            )
        }
    }
}

@Composable
fun ScoreInput(
    score: Score,
    onCommitScore: (Score) -> Unit,
    label: String
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(score.toString()))
    }
    LaunchedEffect(score) {
        text = TextFieldValue(score.toString())
    }
    var hasFocus by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = { new ->
            if (new.text.matches(Regex("^\\d{0,2}(\\.\\d{0,2})?$"))) {
                text = new
            }
        },
        // ...
        modifier = Modifier.onFocusChanged { f ->
            val lost = hasFocus && !f.isFocused
            hasFocus = f.isFocused
            if (lost) onCommitScore(Score(text.text.toDoubleOrNull() ?: 0.0))
        },
        trailingIcon = {
            if (text.text.isNotEmpty()) {
                IconButton({
                    text = TextFieldValue("")
                    onCommitScore(Score(0.0))
                }) { Icon(Icons.Filled.Clear, contentDescription = "Clear $label") }
            }
        }
    )
}