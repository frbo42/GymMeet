package org.fb.gym.meet

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.ui.AppNavHost
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    navController: NavHostController = rememberNavController()
) {
    MaterialTheme {
        val meetRepository = MeetRepository()

        //        Screen.valueOf(
//            backStackEntry?.destination?.route ?: Screen.Meet.name
//        )
        AppNavHost(navController, meetRepository)
    }
}