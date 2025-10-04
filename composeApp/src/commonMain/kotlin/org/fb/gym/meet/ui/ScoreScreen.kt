package org.fb.gym.meet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
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
    }
}

@Composable
private fun VaultRow(
    vault: VaultScore,
    onVaultChanged: (VaultScore) -> Unit,
    modifier: Modifier = Modifier
) {
    var firstJumpText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(vault.firstJump.toString()))
    }
    var secondJumpText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(vault.secondJump.toString()))
    }

    // Keep UI in sync when the parent updates the vault object
    LaunchedEffect(vault) {
        firstJumpText = TextFieldValue(vault.firstJump.toString())
        secondJumpText = TextFieldValue(vault.secondJump.toString())
    }

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
                    text = remember { mutableStateOf(firstJumpText) },
                    onScoreChanged = { score ->
                        firstJumpText = TextFieldValue(score.toString())
                        onVaultChanged(vault.copy(firstJump = score))
                    },
                    label = "1st"
                )

                Spacer(Modifier.width(8.dp))

                ScoreInput(
                    text = remember { mutableStateOf(secondJumpText) },
                    onScoreChanged = { score ->
                        secondJumpText = TextFieldValue(score.toString())
                        onVaultChanged(vault.copy(secondJump = score))
                    },
                    label = "2nd"
                )
            }
        }
    }
}

/**
 * Saves a [ScoreCard] as a flat list of six Double values:
 *   0 – floor
 *   1 – rings
 *   2 – vault pre‑flight
 *   3 – vault post‑flight
 *   4 – parallel bars
 *   5 – horizontal bar
 *
 * The list can be written to the platform‑specific saved‑state store
 * (Bundle on Android, JSON on JS/Wasm, etc.) without any extra
 * dependencies.
 */
private val ScoreCardSaver = Saver<ScoreCard, List<Double>>(
    save = { card ->
        listOf(
            card.floor.value,
            card.rings.value,
            card.vault.firstJump.value,
            card.vault.secondJump.value,
            card.parallel.value,
            card.bar.value
        )
    },
    restore = { list ->
        // Defensive: the list should always have exactly 6 elements,
        // but we guard against malformed data just in case.
        if (list.size != 6) {
            // Return a brand‑new empty card if the saved payload is corrupt.
            ScoreCard()
        } else {
            ScoreCard(
                floor = Score(list[0]),
                rings = Score(list[1]),
                vault = VaultScore(
                    firstJump = Score(list[2]),
                    secondJump = Score(list[3])
                ),
                parallel = Score(list[4]),
                bar = Score(list[5])
            )
        }
    }
)

@Composable
private fun ScoreRow(
    icon: ImageVector,
    label: String,
    score: Score,
    onScoreChanged: (Score) -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep the text representation in sync with the Score value
    val text = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(score.toString()))
    }

    // Whenever the external `score` changes (e.g. restored from a ViewModel)
    LaunchedEffect(score) {
        text.value = TextFieldValue(score.toString())
    }
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
                text = text,
                onScoreChanged = { onScoreChanged },
                label = label
            )
        }
    }
}


@Composable
private fun ScoreInput(
    text: MutableState<TextFieldValue>,
    onScoreChanged: (Score) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = text.value,

        onValueChange = { newValue ->
            val accept = newValue.text.matches(Regex("^\\d{0,2}(\\.\\d{0,2})?$"))
            if (accept) {
                text.value = newValue
                val parsed = newValue.text.toDoubleOrNull()
                onScoreChanged(Score(parsed ?: 0.0))
            }
        },
        label = { Text("Score") },
        placeholder = { Text("0.00") },
//                supportingText = { Text("0–10, max 2 decimals") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        singleLine = true,
        trailingIcon = {
            if (text.value.text.isNotEmpty()) {
                IconButton(onClick = {
                    text.value = TextFieldValue("")
                    onScoreChanged(Score(0.0))
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear $label score")
                }
            }
        },
        modifier = Modifier
            .widthIn(min = 96.dp)
            .padding(end = 12.dp)
    )
}