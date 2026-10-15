package com.zonealarm.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zonealarm.app.ui.editor.ZoneEditorScreen
import com.zonealarm.app.ui.list.AlarmListScreen
import com.zonealarm.app.ui.settings.SettingsScreen

object Routes {
    const val LIST = "list"
    const val EDITOR = "editor"
    const val SETTINGS = "settings"
    const val ARG_ZONE_ID = "zoneId"

    fun editor(zoneId: String? = null): String =
        if (zoneId == null) EDITOR else "$EDITOR?$ARG_ZONE_ID=$zoneId"
}

@Composable
fun ZoneAlarmNavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            AlarmListScreen(
                onAdd = { nav.navigate(Routes.editor()) },
                onEdit = { id -> nav.navigate(Routes.editor(id)) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(
            route = "${Routes.EDITOR}?${Routes.ARG_ZONE_ID}={${Routes.ARG_ZONE_ID}}",
            arguments = listOf(
                navArgument(Routes.ARG_ZONE_ID) {
                    type = NavType.StringType
                    nullable = true
                },
            ),
        ) {
            ZoneEditorScreen(onDone = { nav.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
    }
}
