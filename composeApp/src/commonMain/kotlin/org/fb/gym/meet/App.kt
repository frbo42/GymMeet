package org.fb.gym.meet

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.fb.gym.meet.data.DatabaseFactory
import org.fb.gym.meet.data.GymnastRepository
import org.fb.gym.meet.data.MeetRepository
import org.fb.gym.meet.db.AppDatabase
import org.fb.gym.meet.ui.AppNavHost
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    navController: NavHostController = rememberNavController()
) {
    MaterialTheme {
        val meetRepo = MeetRepository(db())
        val gymnastRepo = GymnastRepository()
        //        Screen.valueOf(
//            backStackEntry?.destination?.route ?: Screen.Meet.name
//        )
        AppNavHost(navController, meetRepo, gymnastRepo)
    }
}

private fun db(): AppDatabase {
    return AppDatabase(DatabaseFactory.createDriver())
}
