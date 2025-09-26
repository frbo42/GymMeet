package org.fb.gym.meet.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCardId

sealed class Screen(val route: String) {
    object Meet : Screen("meets")
    data class Gymnast(val meetId: String) : Screen("gymnasts/meets/$meetId")
    data class Score(
        val meetId: String,
        val gymnastId: String
    ) : Screen("scores/$meetId/${gymnastId}")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    meetRepository: MeetRepository,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Meet.route
    ) {
        meetRoute(meetRepository, navController)
        gymnastRoute(meetRepository, navController)
        scoreRoute(meetRepository, navController)
    }
}

private fun NavGraphBuilder.meetRoute(
    meetRepository: MeetRepository,
    navController: NavHostController
) {
    composable(Screen.Meet.route) {
        MeetScreen(
            meetRepository.findMeets(),
            onClick = { meetId -> navController.navigate(Screen.Gymnast(meetId).route) }
        )
    }
}

private fun NavGraphBuilder.gymnastRoute(
    meetRepository: MeetRepository,
    navController: NavHostController
) {
    composable(
        route = Screen.Gymnast("{meetId}").route
    ) { backStackEntry ->
        val handle = backStackEntry.savedStateHandle.get<String>("meetId")
        val meetId = handle ?: return@composable
        GymnastScreen(
            meetId,
            meetRepository.findGymnastsForMeet(meetId),
            onClick = { meetId, gymnastId -> navController.navigate(Screen.Score(meetId, gymnastId).route) }
        )
    }
}

private fun NavGraphBuilder.scoreRoute(
    meetRepository: MeetRepository,
    navController: NavHostController
) {
    composable(Screen.Score("{meetId}", "{gymnastId}").route) { backStackEntry ->
        val handleMeetId = backStackEntry.savedStateHandle.get<String>("meetId")
        val handleGymnastId = backStackEntry.savedStateHandle.get<String>("gymnastId")
        val meetId = handleMeetId ?: return@composable
        val gymnastId = handleGymnastId ?: return@composable
        ScoreScreen(
            meetRepository.getGymnast(gymnastId),
            ScoreViewModel(ScoreCardId(meetId, gymnastId), meetRepository),
            onBackClick = { navController.popBackStack() }
        )
    }
}
