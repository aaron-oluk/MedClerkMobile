package com.example.medclerkmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.navigation.Routes
import com.example.medclerkmobile.ui.auth.LoginScreen
import com.example.medclerkmobile.ui.dashboard.DashboardScreen
import com.example.medclerkmobile.ui.logbook.NewLogbookEntryScreen
import com.example.medclerkmobile.ui.theme.MedClerkMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as MedClerkApplication).container

        setContent {
            MedClerkMobileTheme {
                MedClerkApp(container)
            }
        }
    }
}

@Composable
private fun MedClerkApp(container: AppContainer) {
    val navController = rememberNavController()
    val isLoggedIn by container.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.HOME else Routes.LOGIN,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(container) { navController.navigateToHome() }
        }

        composable(Routes.HOME) {
            DashboardScreen(
                container = container,
                onAddLogbookEntry = { navController.navigate(Routes.NEW_LOGBOOK_ENTRY) },
                onLoggedOut = { navController.navigateToLogin() },
            )
        }

        composable(Routes.NEW_LOGBOOK_ENTRY) {
            NewLogbookEntryScreen(
                container = container,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
            )
        }
    }
}

private fun NavHostController.navigateToHome() {
    navigate(Routes.HOME) {
        popUpTo(Routes.LOGIN) { inclusive = true }
    }
}

private fun NavHostController.navigateToLogin() {
    navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}
