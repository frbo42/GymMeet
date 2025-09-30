package org.fb.gym.meet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.fb.gym.meet.data.GymnastRepository
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.data.ScoreCardId

sealed class Screen(val route: String) {
    object Meet : Screen("meets")
    object NewMeet : Screen("meets/new")
    data class Gymnast(val meetId: String) : Screen("gymnasts/meets/$meetId")
    object GymnastCreate : Screen("gymnasts/create")
    data class Score(
        val meetId: String,
        val gymnastId: String
    ) : Screen("scores/$meetId/${gymnastId}")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Meet.route
    ) {
        meetRoute(meetRepo, navController)
        createMeetRoute(meetRepo, gymnastRepo, navController)
        gymnastRoute(meetRepo, gymnastRepo, navController)
        scoreRoute(meetRepo, gymnastRepo, navController)
    }
}

private fun NavGraphBuilder.meetRoute(
    meetRepo: MeetRepository,
    navController: NavHostController
) {
    composable(Screen.Meet.route) {
        val vm = DisplayMeetViewModel(meetRepo)
        MeetScreen(
            meets = vm.meets.collectAsState().value,
            onClick = { meetId -> navController.navigate(Screen.Gymnast(meetId).route) },
            onNewClick = { navController.navigate(Screen.NewMeet.route) }
        )
    }
}

private fun NavGraphBuilder.createMeetRoute(
    meetRepository: MeetRepository,
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(Screen.NewMeet.route) {
        val vm = CreateMeetViewModel(meetRepository, gymnastRepo)
        val uiState = vm.uiState.collectAsState()
        val actions = CreateMeetActions(
            onBack = { navController.popBackStack() },
            onSave = {
                vm.onSave()
                navController.popBackStack()
            },
            onAddGymnast = { navController.navigate(Screen.GymnastCreate) }
        )
        CreateMeetScreen(
            state = uiState,
            actions = actions,
            onNameChanged = vm::onNameChanged,
            onDateChanged = vm::onDateChanged,
            onGymnastToggle = vm::toggleGymnastSelection,
        )
    }
}

private fun NavGraphBuilder.gymnastRoute(
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(
        route = Screen.Gymnast("{meetId}").route
    ) { backStackEntry ->
        val handle = backStackEntry.savedStateHandle.get<String>("meetId")
        val meetId = handle ?: return@composable
        val meet = meetRepo.observeMeet(meetId).collectAsState(null).value
        val gymnasts = gymnastRepo.observeGymnasts().collectAsState(emptySet()).value
        val actions = GymnastActions(
            onBack = { navController.popBackStack() },
            onGymnastSelected = { meetId, gymnastId -> navController.navigate(Screen.Score(meetId, gymnastId).route) }
        )
        GymnastScreen(
            meet,
            gymnasts,
            actions,
        )
    }
}

private fun NavGraphBuilder.scoreRoute(
    meetRepository: MeetRepository,
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(Screen.Score("{meetId}", "{gymnastId}").route) { backStackEntry ->
        val handleMeetId = backStackEntry.savedStateHandle.get<String>("meetId")
        val handleGymnastId = backStackEntry.savedStateHandle.get<String>("gymnastId")
        val meetId = handleMeetId ?: return@composable
        val gymnastId = handleGymnastId ?: return@composable

        ScoreScreen(
            gymnastRepo.observeGymnast(gymnastId).collectAsState(null).value,
            ScoreViewModel(ScoreCardId(meetId, gymnastId), meetRepository),
            onBackClick = { navController.popBackStack() }
        )
    }
}
