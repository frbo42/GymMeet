package org.fb.gym.meet.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import gymmeet.composeapp.generated.resources.Res.string
import gymmeet.composeapp.generated.resources.title_edit_meet
import org.jetbrains.compose.resources.getString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class I18nTest {

    @Test
    fun `when french corresponding translation`() = runComposeUiTest {
        Locale.setDefault(Locale.FRENCH)
        assertEquals("Modifier le concours", getString(string.title_edit_meet))
    }
}