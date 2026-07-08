package com.example.medclerkmobile.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.ui.assessments.AssessmentsScreen
import com.example.medclerkmobile.ui.feedback.FeedbackScreen
import com.example.medclerkmobile.ui.logbook.LogbookScreen
import com.example.medclerkmobile.ui.rotations.RotationsScreen
import kotlinx.coroutines.launch

private data class DashboardTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    DashboardTab("Rotations", Icons.Filled.CalendarMonth),
    DashboardTab("Logbook", Icons.AutoMirrored.Filled.MenuBook),
    DashboardTab("Assessments", Icons.AutoMirrored.Filled.Assignment),
    DashboardTab("Feedback", Icons.Filled.RateReview),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(container: AppContainer, onAddLogbookEntry: () -> Unit, onLoggedOut: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tabs[selectedTab].label) },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            container.authRepository.logout()
                            onLoggedOut()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log out")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        val content = Modifier.padding(innerPadding)
        when (selectedTab) {
            0 -> RotationsScreen(container, content)
            1 -> LogbookScreen(container, onAddLogbookEntry, content)
            2 -> AssessmentsScreen(container, content)
            else -> FeedbackScreen(container, content)
        }
    }
}
