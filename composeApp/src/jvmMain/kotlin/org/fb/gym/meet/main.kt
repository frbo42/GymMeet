package org.fb.gym.meet

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Gym Meet",
    ) {
        App()
    }
}
