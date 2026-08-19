package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBarHeader
import com.example.ui.screens.*
import com.example.ui.theme.DDDrivingCenterTheme
import com.example.viewmodel.*

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val bookingViewModel: BookingViewModel by viewModels()
    private val simulatorViewModel: SimulatorViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()
            val currentTab by mainViewModel.currentTab.collectAsState()
            val snackbarState = remember { SnackbarHostState() }

            var showAdminPortal by remember { mutableStateOf(false) }

            DDDrivingCenterTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBarHeader(
                            currentTab = currentTab,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { mainViewModel.toggleTheme() },
                            onOpenCallSupport = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+918403050225"))
                                startActivity(intent)
                            },
                            onOpenAdmin = {
                                showAdminPortal = true
                                mainViewModel.selectTab(NavigationTab.MORE)
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            selectedTab = currentTab,
                            onSelectTab = {
                                showAdminPortal = false
                                mainViewModel.selectTab(it)
                            }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (showAdminPortal) {
                            AdminScreen(
                                adminViewModel = adminViewModel,
                                onCloseAdminPortal = { showAdminPortal = false }
                            )
                        } else {
                            Crossfade(
                                targetState = currentTab,
                                modifier = Modifier.fillMaxSize(),
                                label = "tab_crossfade"
                            ) { tab ->
                                when (tab) {
                                    NavigationTab.HOME -> {
                                        HomeScreen(
                                            mainViewModel = mainViewModel,
                                            onNavigateTo = { mainViewModel.selectTab(it) },
                                            onSelectCourseToBook = { course ->
                                                bookingViewModel.setSelectedCourse(course)
                                            }
                                        )
                                    }
                                    NavigationTab.COURSES -> {
                                        CoursesScreen(
                                            mainViewModel = mainViewModel,
                                            onNavigateToBookingWithCourse = { course ->
                                                bookingViewModel.setSelectedCourse(course)
                                                mainViewModel.selectTab(NavigationTab.BOOKING)
                                            }
                                        )
                                    }
                                    NavigationTab.SIMULATOR -> {
                                        SimulatorScreen(
                                            simulatorViewModel = simulatorViewModel,
                                            bookingViewModel = bookingViewModel,
                                            adminViewModel = adminViewModel,
                                            onNavigateToBooking = { mainViewModel.selectTab(NavigationTab.BOOKING) },
                                            onOpenAdminPortal = {
                                                showAdminPortal = true
                                                mainViewModel.selectTab(NavigationTab.MORE)
                                            }
                                        )
                                    }
                                    NavigationTab.BOOKING -> {
                                        BookingScreen(
                                            bookingViewModel = bookingViewModel,
                                            mainViewModel = mainViewModel
                                        )
                                    }
                                    NavigationTab.INSTRUCTORS -> {
                                        InstructorsScreen(
                                            mainViewModel = mainViewModel,
                                            onSelectInstructorToBook = { instructor ->
                                                bookingViewModel.setSelectedInstructor(instructor)
                                                mainViewModel.selectTab(NavigationTab.BOOKING)
                                            }
                                        )
                                    }
                                    NavigationTab.MORE -> {
                                        GalleryFaqContactScreen(
                                            mainViewModel = mainViewModel,
                                            onOpenAdminPortal = { showAdminPortal = true }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
