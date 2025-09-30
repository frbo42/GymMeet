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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed class Screen(val route: String) {
    object Meet : Screen("meets")
    object CreateMeet : Screen("meets/create")
    data class EditMeet(val meetId: String) : Screen("meets/edit/$meetId")
    data class Gymnast(val meetId: String) : Screen("gymnasts/meets/$meetId")
    object CreateGymnast : Screen("gymnasts/create")
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
        editMeetRoute(meetRepo, gymnastRepo, navController)
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
            MeetActions(
                onSelectMeet = { meetId -> navController.navigate(Screen.Gymnast(meetId).route) },
                onCreateMeet = {
                    println("clicked create")
                    navController.navigate(Screen.CreateMeet.route)
                },
                onEditMeet = { meetId -> navController.navigate(Screen.EditMeet(meetId).route) },
            )
        )
    }
}


@OptIn(ExperimentalUuidApi::class)
private fun NavGraphBuilder.createMeetRoute(
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(Screen.CreateMeet.route) {
        val vm = CreateMeetViewModel(
            Uuid.random().toString(),
            meetRepo,
            gymnastRepo
        )
        val uiState = vm.uiState.collectAsState()
        val actions = CreateMeetActions(
            onBack = { navController.popBackStack() },
            onSave = {
                vm.onSave()
                navController.popBackStack()
            },
            onAddGymnast = { navController.navigate(Screen.CreateGymnast) }
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

private fun NavGraphBuilder.editMeetRoute(
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(Screen.EditMeet("{meetId}").route) { backStackEntry ->
        val handle = backStackEntry.savedStateHandle.get<String>("meetId")
        val meetId = handle ?: return@composable
        val vm = CreateMeetViewModel(
            meetId,
            meetRepo,
            gymnastRepo
        )
        val uiState = vm.uiState.collectAsState()
        val actions = CreateMeetActions(
            onBack = { navController.popBackStack() },
            onSave = {
                vm.onSave()
                navController.popBackStack()
            },
            onAddGymnast = { navController.navigate(Screen.CreateGymnast) }
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
