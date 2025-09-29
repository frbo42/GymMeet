package org.fb.gym.meet.data

data class Meet(
    val id: String,
    val name: String,
    val date: String,
    val participants: List<Participant> = emptyList()
)

data class Participant(
    val gymnastId: String,
    val scoreCard: ScoreCard = ScoreCard()
)
