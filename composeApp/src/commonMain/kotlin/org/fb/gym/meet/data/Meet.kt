package org.fb.gym.meet.data

data class MeetOverview(
    val id: String,
    val name: String,
    val date: String,
)

data class Meet(
    val overview: MeetOverview,
    val participants: List<Participant> = emptyList()
)

data class Participant(
    val gymnastId: String,
    val scoreCard: ScoreCard = ScoreCard()
)
