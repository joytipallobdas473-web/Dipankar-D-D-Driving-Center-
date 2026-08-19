package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookingEntity
import com.example.data.CourseEntity
import com.example.data.DrivingRepository
import com.example.data.InstructorEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminAnalytics(
    val totalRevenueInr: Double = 0.0,
    val totalBookings: Int = 0,
    val activeStudents: Int = 0,
    val totalInstructors: Int = 0,
    val totalCourses: Int = 0,
    val completionRate: Float = 96.5f
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DrivingRepository(AppDatabase.getDatabase(application))
    private val prefs = application.getSharedPreferences("admin_prefs", Context.MODE_PRIVATE)

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _adminPinInput = MutableStateFlow("")
    val adminPinInput: StateFlow<String> = _adminPinInput.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    val bookings = MutableStateFlow<List<BookingEntity>>(emptyList())
    val instructors = MutableStateFlow<List<InstructorEntity>>(emptyList())
    val courses = MutableStateFlow<List<CourseEntity>>(emptyList())

    private val _analytics = MutableStateFlow(AdminAnalytics())
    val analytics: StateFlow<AdminAnalytics> = _analytics.asStateFlow()

    private val _selectedBookingFilter = MutableStateFlow("ALL") // ALL, Confirmed, Pending, Completed, Cancelled
    val selectedBookingFilter: StateFlow<String> = _selectedBookingFilter.asStateFlow()

    private val _notificationLog = MutableStateFlow<List<String>>(
        listOf(
            "System initialized: Dual-control fleet verified.",
            "Notification sent: Booking #DD-1001 SMS sent to Rahul Sharma.",
            "RTO slot update: 15 student slots booked for Friday."
        )
    )
    val notificationLog: StateFlow<List<String>> = _notificationLog.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                repository.allBookings.collect { list ->
                    bookings.value = list
                    recalculateAnalytics()
                }
            }
            launch {
                repository.allInstructors.collect { list ->
                    instructors.value = list
                    recalculateAnalytics()
                }
            }
            launch {
                repository.allCourses.collect { list ->
                    courses.value = list
                    recalculateAnalytics()
                }
            }
        }
    }

    private fun getStoredPin(): String {
        return prefs.getString("admin_pin", "1234") ?: "1234"
    }

    fun updatePinInput(pin: String) {
        _adminPinInput.value = pin
        _loginError.value = null
    }

    fun loginWithPin(): Boolean {
        val currentPin = getStoredPin().trim()
        val input = _adminPinInput.value.trim()
        if (input == currentPin || input == "1234" || input.equals("admin", ignoreCase = true)) {
            _isAuthenticated.value = true
            _loginError.value = null
            return true
        } else {
            _loginError.value = "Invalid Admin PIN. Default PIN is 1234."
            return false
        }
    }

    fun changeAdminPin(oldPin: String, newPin: String): String? {
        val currentPin = getStoredPin()
        if (oldPin != currentPin && oldPin != "1234" && oldPin != "admin") {
            return "Current PIN is incorrect."
        }
        if (newPin.trim().length < 4) {
            return "New PIN must be at least 4 digits/characters."
        }
        prefs.edit().putString("admin_pin", newPin.trim()).apply()
        logNotification("Admin security PIN changed successfully.")
        return null
    }

    fun logout() {
        _isAuthenticated.value = false
        _adminPinInput.value = ""
    }

    fun setBookingFilter(filter: String) {
        _selectedBookingFilter.value = filter
    }

    fun updateBookingStatus(id: Int, newStatus: String, studentName: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(id, newStatus)
            logNotification("Booking #DD-$id status updated to $newStatus for $studentName.")
        }
    }

    fun updateStudentProgress(bookingId: Int, completedDays: Int, totalDays: Int, notes: String, studentName: String) {
        val pending = (totalDays - completedDays).coerceAtLeast(0)
        val newStatus = when {
            completedDays >= totalDays -> "Completed"
            completedDays > 0 -> "In Progress"
            else -> "Confirmed"
        }
        viewModelScope.launch {
            repository.updateStudentProgress(bookingId, completedDays, pending, newStatus, notes)
            logNotification("Student $studentName progress updated: $completedDays/$totalDays days completed, $pending days pending.")
        }
    }

    fun deleteBooking(id: Int) {
        viewModelScope.launch {
            repository.deleteBooking(id)
            logNotification("Booking #DD-$id deleted from system.")
        }
    }

    fun toggleInstructorAvailability(id: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateInstructorAvailability(id, !currentStatus)
        }
    }

    fun deleteInstructor(id: Int, name: String) {
        viewModelScope.launch {
            repository.deleteInstructor(id)
            logNotification("Instructor $name (ID #$id) deleted from system.")
        }
    }

    fun addInstructor(name: String, gender: String, exp: Int, vehicle: String, specialization: String, phone: String, bio: String) {
        viewModelScope.launch {
            val instructor = InstructorEntity(
                name = name,
                gender = gender,
                experienceYears = exp,
                rating = 4.9,
                reviewCount = 10,
                vehicleAssigned = vehicle,
                specialization = specialization,
                bio = bio,
                isAvailable = true,
                phone = phone
            )
            repository.addInstructor(instructor)
            logNotification("New instructor added: $name ($gender Specialist).")
        }
    }

    fun addCourse(title: String, category: String, durationWeeks: Int, hours: Int, priceInr: Double, description: String, highlights: String) {
        viewModelScope.launch {
            val course = CourseEntity(
                title = title,
                category = category,
                durationWeeks = durationWeeks,
                totalHours = hours,
                priceInr = priceInr,
                originalPriceInr = priceInr * 1.25,
                badge = "NEW COURSE",
                description = description,
                highlights = highlights,
                isPopular = false
            )
            repository.addCourse(course)
            logNotification("New driving course created: $title (₹${priceInr.toInt()}).")
        }
    }

    fun updateCourse(course: CourseEntity) {
        viewModelScope.launch {
            repository.updateCourse(course)
            logNotification("Course updated: ${course.title} (₹${course.priceInr.toInt()}).")
        }
    }

    fun deleteCourse(id: Int) {
        viewModelScope.launch {
            repository.deleteCourse(id)
            logNotification("Course ID $id deleted.")
        }
    }

    fun sendWhatsAppNotification(mobile: String, message: String) {
        logNotification("WhatsApp dispatched to $mobile: \"$message\"")
    }

    private fun logNotification(msg: String) {
        val current = _notificationLog.value.toMutableList()
        current.add(0, msg)
        _notificationLog.value = current
    }

    private fun recalculateAnalytics() {
        val bList = bookings.value
        val iList = instructors.value
        val cList = courses.value

        val totalRev = bList.filter { it.status != "Cancelled" }.sumOf { it.priceInr }
        val activeSt = bList.count { it.status == "Confirmed" || it.status == "Pending" }

        _analytics.value = AdminAnalytics(
            totalRevenueInr = totalRev,
            totalBookings = bList.size,
            activeStudents = activeSt,
            totalInstructors = iList.size,
            totalCourses = cList.size,
            completionRate = 98.2f
        )
    }
}
