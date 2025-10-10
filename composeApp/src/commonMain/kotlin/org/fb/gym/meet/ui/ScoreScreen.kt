package org.fb.gym.meet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import gymmeet.composeapp.generated.resources.*
import org.fb.gym.meet.data.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


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
            icon = Res.drawable.ic_floor,
            label = "Floor",
            score = scoreCard.floor,
            onScoreChanged = { updateCard(scoreCard.copy(floor = it)) },
        )

        // ----- RINGS ----------------------------------------------------
        ScoreRow(
            icon = Res.drawable.ic_rings,
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
                icon = Res.drawable.ic_parallel_bar,
                label = "Parallel Bars",
                score = scoreCard.parallel,
                onScoreChanged = { updateCard(scoreCard.copy(parallel = it)) },
            )
        }
        // ----- HORIZONTAL BAR -------------------------------------------
        ScoreRow(
            icon = Res.drawable.ic_horizontal_bar,
            label = "Horizontal Bar",
            score = scoreCard.bar,
            onScoreChanged = { updateCard(scoreCard.copy(bar = it)) }
        )
        TotalRow(scoreCard, gymnast)
    }
}

@Composable
private fun TotalRow(scoreCard: ScoreCard, gymnast: Gymnast) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SportsGymnastics,
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
                text = scoreCard.total(gymnast.category).toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .widthIn(min = 96.dp)
                    .padding(end = 12.dp)
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
                painter = painterResource(Res.drawable.ic_vault),
                contentDescription = "Vault icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))

            // Keep the label readable; don't let inputs steal all space
            Text(
                text = "Vault",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
                    .widthIn(min = 64.dp)
            )

            // Compact, fixed-width inputs
            ScoreInput(
                score = vault.firstJump,
                onCommitScore = { score ->
                    onVaultChanged(vault.copy(firstJump = score))
                },
                label = "1st",
                modifier = Modifier
                    .widthIn(min = 96.dp, max = 120.dp)
            )

            Spacer(Modifier.width(8.dp))

            ScoreInput(
                score = vault.secondJump,
                onCommitScore = { score ->
                    onVaultChanged(vault.copy(secondJump = score))
                },
                label = "2nd",
                modifier = Modifier
                    .widthIn(min = 96.dp, max = 120.dp)
            )
        }
    }
}

@Composable
private fun ScoreRow(
    icon: DrawableResource,
    label: String,
    score: Score,
    onScoreChanged: (Score) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scoreRow$label"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
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
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
                    .widthIn(min = 64.dp)
            )

            ScoreInput(
                score = score,
                onCommitScore = onScoreChanged,
                label = label,
                modifier = Modifier
                    .widthIn(min = 96.dp, max = 120.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
    }
}

@Composable
fun ScoreInput(
    score: Score,
    onCommitScore: (Score) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(score.toString()))
    }
    LaunchedEffect(score) { text = TextFieldValue(score.toString()) }

    val focusManager = LocalFocusManager.current

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val typingRegex = remember { Regex("^\\d{0,2}(\\.)?\\d{0,2}$") }

    OutlinedTextField(
        value = text,
        onValueChange = { new ->
            if (new.text.matches(typingRegex)) {
                text = new
                errorMessage = null
            }
        },
        isError = errorMessage != null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                val parsed = text.text.toDoubleOrNull() ?: 0.0
                onCommitScore(Score(parsed))
                focusManager.clearFocus()
            }
        ),
        modifier = modifier
            .heightIn(min = 56.dp)
            .onFocusChanged { state ->
                if (!state.isFocused) {
                    val raw = text.text
                    val parsed = raw.toDoubleOrNull()
                    when {
                        parsed == null -> {
                            errorMessage = "Please enter a number"
                        }

                        parsed < 0.0 -> {
                            errorMessage = "Value must be ≥ 0"
                        }

                        parsed > 10.0 -> {
                            errorMessage = "Value must be ≤ 10"
                        }

                        else -> {
                            // Value is valid – commit to the ViewModel
                            val s = Score(parsed)
                            onCommitScore(s)
                            errorMessage = null
                            // Optionally format the text to two decimals
                            text = TextFieldValue(s.toString().take(5))
                            focusManager.clearFocus()
                        }
                    }
                }
//                val lost = hadFocus && !state.isFocused
//                if (lost) {
//                    val parsed = text.text.toDoubleOrNull() ?: 0.0
//                    onCommitScore(Score(parsed))
//                }
//                hadFocus = state.isFocused
            }
            .testTag("scoreInput$label"),
        trailingIcon = {
            if (text.text.isNotEmpty()) {
                IconButton({
                    text = TextFieldValue("")
                    onCommitScore(Score(0.0))
                    errorMessage = null
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear $label")
                }
            }
        },
        supportingText = {
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}

