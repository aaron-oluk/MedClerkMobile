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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.navigation.Routes
import com.example.medclerkmobile.ui.assessments.NewAssessmentScreen
import com.example.medclerkmobile.ui.assessments.PendingAssessmentsScreen
import com.example.medclerkmobile.ui.auth.LoginScreen
import com.example.medclerkmobile.ui.dashboard.DashboardScreen
import com.example.medclerkmobile.ui.feedback.FeedbackScreen
import com.example.medclerkmobile.ui.feedback.NewFeedbackScreen
import com.example.medclerkmobile.ui.library.SignDetailScreen
import com.example.medclerkmobile.ui.library.SkillDetailScreen
import com.example.medclerkmobile.ui.library.SystemDetailScreen
import com.example.medclerkmobile.ui.logbook.NewLogbookEntryScreen
import com.example.medclerkmobile.ui.rotations.RotationsScreen
import com.example.medclerkmobile.ui.settings.SettingsScreen
import com.example.medclerkmobile.ui.students.StudentProfileScreen
import com.example.medclerkmobile.ui.students.StudentSearchScreen
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
                onOpenLibrarySystem = { id -> navController.navigate(Routes.systemDetail(id)) },
                onOpenLibrarySign = { id -> navController.navigate(Routes.signDetail(id)) },
                onOpenRotations = { navController.navigate(Routes.ROTATIONS) },
                onOpenFeedback = { navController.navigate(Routes.FEEDBACK) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenStudentSearch = { navController.navigate(Routes.STUDENT_SEARCH) },
                onAddAssessment = { navController.navigate(Routes.PENDING_ASSESSMENTS) },
                onLoggedOut = { navController.navigateToLogin() },
            )
        }

        composable(Routes.ROTATIONS) {
            RotationsScreen(container = container, onBack = { navController.popBackStack() })
        }

        composable(Routes.FEEDBACK) {
            FeedbackScreen(
                container = container,
                onBack = { navController.popBackStack() },
                onAddFeedback = { navController.navigate(Routes.NEW_FEEDBACK) },
            )
        }

        composable(Routes.PENDING_ASSESSMENTS) {
            PendingAssessmentsScreen(
                container = container,
                onBack = { navController.popBackStack() },
                onOpenEntry = { id -> navController.navigate(Routes.newAssessment(id)) },
            )
        }

        composable(
            Routes.NEW_ASSESSMENT,
            arguments = listOf(navArgument(Routes.LOGBOOK_ENTRY_ID_ARG) { type = NavType.IntType }),
        ) { backStackEntry ->
            val logbookEntryId = backStackEntry.arguments?.getInt(Routes.LOGBOOK_ENTRY_ID_ARG) ?: return@composable
            NewAssessmentScreen(
                container = container,
                logbookEntryId = logbookEntryId,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
            )
        }

        composable(Routes.NEW_FEEDBACK) {
            NewFeedbackScreen(
                container = container,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(container = container, onBack = { navController.popBackStack() })
        }

        composable(Routes.STUDENT_SEARCH) {
            StudentSearchScreen(
                container = container,
                onBack = { navController.popBackStack() },
                onOpenStudent = { id -> navController.navigate(Routes.studentDetail(id)) },
            )
        }

        composable(
            Routes.STUDENT_DETAIL,
            arguments = listOf(navArgument(Routes.STUDENT_ID_ARG) { type = NavType.IntType }),
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt(Routes.STUDENT_ID_ARG) ?: return@composable
            StudentProfileScreen(container = container, studentId = studentId, onBack = { navController.popBackStack() })
        }

        composable(Routes.NEW_LOGBOOK_ENTRY) {
            NewLogbookEntryScreen(
                container = container,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
            )
        }

        composable(
            Routes.SYSTEM_DETAIL,
            arguments = listOf(navArgument(Routes.SYSTEM_ID_ARG) { type = NavType.IntType }),
        ) { backStackEntry ->
            val systemId = backStackEntry.arguments?.getInt(Routes.SYSTEM_ID_ARG) ?: return@composable
            SystemDetailScreen(
                container = container,
                systemId = systemId,
                onBack = { navController.popBackStack() },
                onOpenSign = { id -> navController.navigate(Routes.signDetail(id)) },
                onOpenSkill = { id -> navController.navigate(Routes.skillDetail(id)) },
            )
        }

        composable(
            Routes.SIGN_DETAIL,
            arguments = listOf(navArgument(Routes.SIGN_ID_ARG) { type = NavType.IntType }),
        ) { backStackEntry ->
            val signId = backStackEntry.arguments?.getInt(Routes.SIGN_ID_ARG) ?: return@composable
            SignDetailScreen(container = container, signId = signId, onBack = { navController.popBackStack() })
        }

        composable(
            Routes.SKILL_DETAIL,
            arguments = listOf(navArgument(Routes.SKILL_ID_ARG) { type = NavType.IntType }),
        ) { backStackEntry ->
            val skillId = backStackEntry.arguments?.getInt(Routes.SKILL_ID_ARG) ?: return@composable
            SkillDetailScreen(container = container, skillId = skillId, onBack = { navController.popBackStack() })
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
