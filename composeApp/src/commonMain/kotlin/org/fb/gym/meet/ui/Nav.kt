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
    object Meet : Screen("/meets")
    object CreateMeet : Screen("/meets/create")
    data class EditMeet(val meetId: String) : Screen("/meets/edit/$meetId")
    data class Participant(val meetId: String) : Screen("/meets/$meetId/participants")
    object CreateGymnast : Screen("/gymnasts/create")
    data class EditGymnast(val gymnastId: String) : Screen("/gymnasts/edit/$gymnastId")
    data class Score(
        val meetId: String,
        val gymnastId: String
    ) : Screen("/scores/$meetId/${gymnastId}")
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
        participantRoute(meetRepo, gymnastRepo, navController)
        scoreRoute(meetRepo, gymnastRepo, navController)
        createGymnastRoute(gymnastRepo, navController)
        editGymnastRoute(gymnastRepo, navController)
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
                onSelectMeet = { meetId -> navController.navigate(Screen.Participant(meetId).route) },
                onCreateMeet = { navController.navigate(Screen.CreateMeet.route) },
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
        val vm = EditMeetViewModel(
            meetRepo = meetRepo,
            gymnastRepo = gymnastRepo
        )
        val uiState = vm.uiState.collectAsState()
        val actions = EditMeetActions(
            onBack = { navController.popBackStack() },
            onSave = {
                vm.onSave()
                navController.popBackStack()
            },
            onAddGymnast = { navController.navigate(Screen.CreateGymnast.route) }
        )
        EditMeetScreen(
            state = uiState,
            actions = actions,
            onNameChanged = vm::onNameChanged,
            onDateChanged = vm::onDateChanged,
            onGymnastToggle = vm::toggleGymnastSelection,
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun NavGraphBuilder.editMeetRoute(
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(Screen.EditMeet("{meetId}").route) { backStackEntry ->
        val handle = backStackEntry.savedStateHandle.get<String>("meetId")
        val meetId = handle ?: Uuid.random().toString()
        val vm = EditMeetViewModel(
            meetId,
            meetRepo,
            gymnastRepo
        )
        val uiState = vm.uiState.collectAsState()
        val actions = EditMeetActions(
            onBack = { navController.popBackStack() },
            onSave = {
                vm.onSave()
                navController.popBackStack()
            },
            onAddGymnast = { navController.navigate(Screen.CreateGymnast.route) }
        )
        EditMeetScreen(
            state = uiState,
            actions = actions,
            onNameChanged = vm::onNameChanged,
            onDateChanged = vm::onDateChanged,
            onGymnastToggle = vm::toggleGymnastSelection,
        )
    }
}

private fun NavGraphBuilder.participantRoute(
    meetRepo: MeetRepository,
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(
        route = Screen.Participant("{meetId}").route
    ) { backStackEntry ->
        val handle = backStackEntry.savedStateHandle.get<String>("meetId")
        val meetId = handle ?: return@composable
        val meet = meetRepo.observeMeet(meetId).collectAsState(null).value
        val gymnasts = gymnastRepo.observeGymnasts().collectAsState(emptySet()).value
        val actions = ParticipantActions(
            onBack = { navController.popBackStack() },
            onGymnastSelected = { meetId, gymnastId -> navController.navigate(Screen.Score(meetId, gymnastId).route) }
        )
        ParticipantScreen(
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

@OptIn(ExperimentalUuidApi::class)
private fun NavGraphBuilder.createGymnastRoute(
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(Screen.CreateGymnast.route) {
        val id = Uuid.random().toString()
        EditGymnastScreen(
            gymnastId = id,
            vm = EditGymnastViewModel(gymnastRepo, id),
            onBack = { navController.popBackStack() },
            onSaved = { navController.popBackStack() }   // go back after save
        )
    }
}

private fun NavGraphBuilder.editGymnastRoute(
    gymnastRepo: GymnastRepository,
    navController: NavHostController
) {
    composable(Screen.EditGymnast("{gymnastId}").route) { backStackEntry ->
        val id = backStackEntry.savedStateHandle.get<String>("gymnastId")
        EditGymnastScreen(
            gymnastId = id,
            vm = EditGymnastViewModel(gymnastRepo, id),
            onBack = { navController.popBackStack() },
            onSaved = { navController.popBackStack() }   // go back after save
        )
    }
}
