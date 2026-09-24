package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryIndigo

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Roleplay : Screen("roleplay", "Roleplay", Icons.Filled.Mic, Icons.Outlined.Mic)
    object SpeechLab : Screen("speech_lab", "Speech Lab", Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq)
    object LiveTutoring : Screen("live_tutoring", "Live Tutor", Icons.Filled.VideoCall, Icons.Outlined.VideoCall)
    object Curriculum : Screen("curriculum", "Lessons", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    object Community : Screen("community", "Community", Icons.Filled.Groups, Icons.Outlined.Groups)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)

    // Sub-screens without bottom bar
    object PeerPractice : Screen("peer_practice", "Peer Practice", Icons.Filled.Group, Icons.Outlined.Group)
    object PlacementTest : Screen("placement_test", "Placement", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle)
    object MicroLessons : Screen("micro_lessons", "Micro-Lessons", Icons.Filled.Bolt, Icons.Outlined.Bolt)
    object VocabDeck : Screen("vocab_deck", "Vocab SRS", Icons.Filled.Style, Icons.Outlined.Style)
    object LandingPage : Screen("landing_page", "Platform", Icons.Filled.Language, Icons.Outlined.Language)
}

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination

                val bottomNavItems = listOf(
                    Screen.Dashboard,
                    Screen.Roleplay,
                    Screen.SpeechLab,
                    Screen.LiveTutoring,
                    Screen.Curriculum,
                    Screen.Community,
                    Screen.Profile
                )

                val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bottom_nav_bar"),
                                tonalElevation = 8.dp
                            ) {
                                bottomNavItems.forEach { screen ->
                                    val isSelected = currentDestination?.route == screen.route
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = screen.title,
                                                fontSize = 10.sp,
                                                maxLines = 1
                                            )
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = PrimaryIndigo.copy(alpha = 0.2f),
                                            selectedIconColor = PrimaryIndigo,
                                            selectedTextColor = PrimaryIndigo
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                viewModel = mainViewModel,
                                onNavigateToRoleplay = { navController.navigate(Screen.Roleplay.route) },
                                onNavigateToSpeechLab = { navController.navigate(Screen.SpeechLab.route) },
                                onNavigateToPlacement = { navController.navigate(Screen.PlacementTest.route) },
                                onNavigateToMicroLessons = { navController.navigate(Screen.MicroLessons.route) },
                                onNavigateToLiveTutor = { navController.navigate(Screen.LiveTutoring.route) },
                                onNavigateToVocab = { navController.navigate(Screen.VocabDeck.route) },
                                onNavigateToLanding = { navController.navigate(Screen.LandingPage.route) }
                            )
                        }

                        composable(Screen.Roleplay.route) {
                            RoleplayScreen(
                                viewModel = mainViewModel,
                                onOpenPhonetics = { phoneme ->
                                    mainViewModel.selectPhoneme(phoneme)
                                    navController.navigate(Screen.SpeechLab.route)
                                }
                            )
                        }

                        composable(Screen.SpeechLab.route) {
                            SpeechLabScreen(
                                viewModel = mainViewModel
                            )
                        }

                        composable(Screen.LiveTutoring.route) {
                            LiveTutoringScreen(
                                viewModel = mainViewModel
                            )
                        }

                        composable(Screen.Curriculum.route) {
                            MicroLessonsScreen(
                                viewModel = mainViewModel
                            )
                        }

                        composable(Screen.Community.route) {
                            CommunityScreen(
                                viewModel = mainViewModel,
                                onNavigateToPeer = { navController.navigate(Screen.PeerPractice.route) }
                            )
                        }

                        composable(Screen.Profile.route) {
                            ProfileScreen(
                                viewModel = mainViewModel
                            )
                        }

                        // Sub routes
                        composable(Screen.PeerPractice.route) {
                            PeerPracticeScreen(
                                viewModel = mainViewModel
                            )
                        }

                        composable(Screen.PlacementTest.route) {
                            PlacementTestScreen(
                                viewModel = mainViewModel,
                                onFinishAndGoDashboard = {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.MicroLessons.route) {
                            MicroLessonsScreen(
                                viewModel = mainViewModel
                            )
                        }

                        composable(Screen.VocabDeck.route) {
                            VocabDeckScreen(
                                viewModel = mainViewModel
                            )
                        }

                        composable(Screen.LandingPage.route) {
                            LandingPageScreen(
                                viewModel = mainViewModel,
                                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) },
                                onNavigateToRoleplay = { navController.navigate(Screen.Roleplay.route) }
                            )
                        }
                    }
                }
            }
        }
    }
}
