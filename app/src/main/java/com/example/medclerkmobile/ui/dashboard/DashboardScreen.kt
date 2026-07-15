package com.example.medclerkmobile.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.medclerkmobile.data.AppContainer
import com.example.medclerkmobile.ui.assessments.AssessmentsScreen
import com.example.medclerkmobile.ui.home.HomeScreen
import com.example.medclerkmobile.ui.library.LibraryScreen
import com.example.medclerkmobile.ui.logbook.LogbookScreen
import com.example.medclerkmobile.ui.profile.ProfileScreen

private data class DashboardTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    DashboardTab("Home", Icons.Filled.Home),
    DashboardTab("Reference", Icons.Filled.LocalLibrary),
    DashboardTab("Logbook", Icons.AutoMirrored.Filled.MenuBook),
    DashboardTab("Results", Icons.AutoMirrored.Filled.Assignment),
    DashboardTab("Profile", Icons.Filled.Person),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    container: AppContainer,
    onAddLogbookEntry: () -> Unit,
    onOpenLibrarySystem: (Int) -> Unit,
    onOpenLibrarySign: (Int) -> Unit,
    onOpenRotations: () -> Unit,
    onOpenFeedback: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStudentSearch: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tabs[selectedTab].label) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        val content = Modifier.padding(innerPadding)
        when (selectedTab) {
            0 -> HomeScreen(
                container = container,
                onAddLogbookEntry = onAddLogbookEntry,
                onOpenRotations = onOpenRotations,
                onOpenFeedback = onOpenFeedback,
                onSwitchToResults = { selectedTab = 3 },
                modifier = content,
            )
            1 -> LibraryScreen(container, onOpenLibrarySystem, onOpenLibrarySign, content)
            2 -> LogbookScreen(container, onAddLogbookEntry, content)
            3 -> AssessmentsScreen(container, content)
            else -> ProfileScreen(container, onOpenRotations, onOpenSettings, onOpenStudentSearch, onLoggedOut, content)
        }
    }
}
