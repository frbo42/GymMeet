package org.fb.gym.meet.ui

import androidx.compose.ui.test.*
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
                FakeScoreContract()
            )
        }

        onNodeWithTag("parallelBarRow").assertExists()
    }

    @Test
    fun `when female parallel bar not present`() = runComposeUiTest {
        setContent {
            ScoreScreen(
                female,
                FakeScoreContract()
            )
        }

        onNodeWithTag("parallelBarRow").assertDoesNotExist()
    }

    @Test
    fun `when score is 8_6 expect 8_60`() = runComposeUiTest {
        val vm = FakeScoreContract()
        setContent {
            ScoreScreen(
                viewModel = vm
            )
        }
        val floorRow = onNodeWithTag("scoreInputFloor")
        floorRow.performTextClearance()
        floorRow.performTextInput("8.6")
        floorRow.performImeAction()

        // Assert the saved score is 8.60 (as a Double equals 8.6; use string if you format to 2 decimals)
        val saved = vm.current()!!
        kotlin.test.assertEquals(8.60, saved.floor.value, 0.000001, "floor double should be 8.60")
        // Optional: if Score.toString() formats to 2 decimals, assert that too:
        kotlin.test.assertEquals("8.60", saved.floor.toString())
    }

}

class FakeScoreContract(
    initialCard: ScoreCard? = ScoreCard()
) : ScoreContract {

    private val _scoreCard = MutableStateFlow(initialCard)

    override val scoreCard: StateFlow<ScoreCard?> = _scoreCard.asStateFlow()

    // The UI will call this; we just replace the value.
    override fun updateScoreCard(updated: ScoreCard) {
        _scoreCard.value = updated
    }

    fun current(): ScoreCard? = scoreCard.value
}