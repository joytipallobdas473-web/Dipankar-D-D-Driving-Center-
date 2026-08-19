package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DrivingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class NavigationTab(val title: String) {
    HOME("Home"),
    COURSES("Courses"),
    SIMULATOR("3D Simulator"),
    BOOKING("Book Lesson"),
    INSTRUCTORS("Instructors"),
    MORE("Explore & Admin")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository: DrivingRepository

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val courses = MutableStateFlow(emptyList<com.example.data.CourseEntity>())
    val instructors = MutableStateFlow(emptyList<com.example.data.InstructorEntity>())
    val testimonials = MutableStateFlow(emptyList<com.example.data.TestimonialEntity>())
    val galleryItems = MutableStateFlow(emptyList<com.example.data.GalleryItemEntity>())

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DrivingRepository(database)

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
            
            launch {
                repository.allCourses.collect { courses.value = it }
            }
            launch {
                repository.allInstructors.collect { instructors.value = it }
            }
            launch {
                repository.allTestimonials.collect { testimonials.value = it }
            }
            launch {
                repository.allGalleryItems.collect { galleryItems.value = it }
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
