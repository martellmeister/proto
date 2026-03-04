package com.example.routineapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.routineapp.presentation.navigation.AppDestination
import com.example.routineapp.presentation.screens.PlaceholderScreen
import com.example.routineapp.presentation.screens.TodayScreen
import com.example.routineapp.presentation.viewmodel.TodayViewModel
import com.example.routineapp.ui.theme.RoutineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoutineTheme {
                RoutineAppScaffold()
            }
        }
    }
}

@Composable
private fun RoutineAppScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val destinations = AppDestination.entries

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination

                destinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Check, contentDescription = null) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.Today.route) {
                val viewModel: TodayViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                TodayScreen(uiState = uiState, onToggleItem = viewModel::toggleItem)
            }
            composable(AppDestination.Routines.route) { PlaceholderScreen("Routines") }
            composable(AppDestination.History.route) { PlaceholderScreen("History") }
            composable(AppDestination.Settings.route) { PlaceholderScreen("Settings") }
        }
    }
}
