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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.fb.gym.meet.data.Gymnast
import org.fb.gym.meet.data.Score
import org.fb.gym.meet.data.ScoreCard
import org.fb.gym.meet.data.VaultScore
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ScoreScreen(
    gymnast: Gymnast = Gymnast("1", "Olivier Bommeli"),
    viewModel: ScoreViewModel = viewModel()
) {
    // Keep a mutable copy locally – this is the source of truth for the UI
    val scoreCard: ScoreCard by viewModel.scoreCard.collectAsState()

    // Helper to propagate changes upward (e.g., to a ViewModel)
    fun updateCard(updated: ScoreCard) = viewModel.updateScoreCard(updated)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gymnast.name) },
                navigationIcon = {
                    IconButton(onClick = { /* handle back */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
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
            ScoreRow(
                icon = Icons.Default.SportsGymnastics, // replace with parallel‑bars icon
                label = "Parallel Bars",
                score = scoreCard.parallel,
                onScoreChanged = { updateCard(scoreCard.copy(parallel = it)) }
            )

            // ----- HORIZONTAL BAR -------------------------------------------
            ScoreRow(
                icon = Icons.Default.Sports, // replace with horizontal‑bar icon
                label = "Horizontal Bar",
                score = scoreCard.bar,
                onScoreChanged = { updateCard(scoreCard.copy(bar = it)) }
            )
        }
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward, // any vault‑related icon you like
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))

        Text(
            text = "Vault",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // first field
        TextField(
            value = firstJumpText,
            onValueChange = {
                if (it.text.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                    firstJumpText = it
                    val parsed = it.text.toDoubleOrNull()
                    onVaultChanged(vault.copy(firstJump = Score(parsed ?: 0.0)))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            placeholder = { Text("First") },
            modifier = Modifier.width(70.dp)
        )
        Spacer(Modifier.width(8.dp))

        // second field
        TextField(
            value = secondJumpText,
            onValueChange = {
                if (it.text.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                    secondJumpText = it
                    val parsed = it.text.toDoubleOrNull()
                    onVaultChanged(vault.copy(secondJump = Score(parsed ?: 0.0)))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            placeholder = { Text("Second") },
            modifier = Modifier.width(70.dp)
        )
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
    var text = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(score.toString()))
    }

    // Whenever the external `score` changes (e.g. restored from a ViewModel)
    LaunchedEffect(score) {
        text.value = TextFieldValue(score.toString())
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // The numeric input – restrict to digits & dot
        TextField(
            value = text.value,
            onValueChange = { newValue ->
                // Allow only numbers, dot and up to 2 decimals (or empty)
                val accept = newValue.text.matches(Regex("^\\d*(\\.\\d{0,2})?$"))
                if (accept) {
                    text.value = newValue
                    val parsed = newValue.text.toDoubleOrNull()
                    onScoreChanged(Score(parsed ?: 0.0))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.width(80.dp)
        )
    }
}