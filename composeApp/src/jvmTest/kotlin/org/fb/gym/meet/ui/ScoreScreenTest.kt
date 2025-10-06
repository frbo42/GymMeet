package org.fb.gym.meet.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.fb.gym.meet.data.Category
import org.fb.gym.meet.data.Gender
import org.fb.gym.meet.data.Gymnast
import org.fb.gym.meet.data.ScoreCard
import kotlin.test.Test

val male = Gymnast("ob", "Olivier", "Bommeli", Category.C5, Gender.M)
val female = Gymnast("la", "Louise", "Auriou", Category.C6, Gender.F)

@OptIn(ExperimentalTestApi::class)
class ScoreScreenTest {

    @Test
    fun `when male parallel bar present`() = runComposeUiTest {
        setContent {
            ScoreScreen(
                male,
                FakeScoreContract(ScoreCard())
            )
        }

        onNodeWithTag("parallelBarRow").assertExists()
    }

    @Test
    fun `when female parallel bar not present`() = runComposeUiTest {
        setContent {
            ScoreScreen(
                female,
                FakeScoreContract(ScoreCard())
            )
        }

        onNodeWithTag("parallelBarRow").assertDoesNotExist()
    }

}

class FakeScoreContract(
    initialCard: ScoreCard? = null
) : ScoreContract {

    private val _scoreCard = MutableStateFlow(initialCard)
    override val scoreCard: StateFlow<ScoreCard?> = _scoreCard.asStateFlow()

    // The UI will call this; we just replace the value.
    override fun updateScoreCard(updated: ScoreCard) {
        _scoreCard.value = updated
    }
}