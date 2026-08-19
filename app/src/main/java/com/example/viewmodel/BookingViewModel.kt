package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookingEntity
import com.example.data.CourseEntity
import com.example.data.DrivingRepository
import com.example.data.InstructorEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class BookingUiState(
    val currentStep: Int = 1,
    val studentName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val selectedCourse: CourseEntity? = null,
    val selectedInstructor: InstructorEntity? = null,
    val preferredDate: String = "",
    val preferredTimeSlot: String = "08:00 AM - 09:00 AM",
    val pickupAddress: String = "",
    val paymentMode: String = "UPI (Google Pay / PhonePe)",
    val discountCode: String = "",
    val discountAmountInr: Double = 0.0,
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val confirmedBooking: BookingEntity? = null,
    val errorMessage: String? = null
)

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DrivingRepository(AppDatabase.getDatabase(application))

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    val savedBookings: StateFlow<List<BookingEntity>> = repository.allBookings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        // Set default date to tomorrow
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _uiState.value = _uiState.value.copy(preferredDate = dateFormat.format(calendar.time))
    }

    fun setStudentName(name: String) { _uiState.value = _uiState.value.copy(studentName = name) }
    fun setMobileNumber(mobile: String) { _uiState.value = _uiState.value.copy(mobileNumber = mobile) }
    fun setEmail(email: String) { _uiState.value = _uiState.value.copy(email = email) }
    fun setSelectedCourse(course: CourseEntity) { 
        _uiState.value = _uiState.value.copy(selectedCourse = course) 
        recalculatePrice()
    }
    fun setSelectedInstructor(instructor: InstructorEntity) { _uiState.value = _uiState.value.copy(selectedInstructor = instructor) }
    fun setPreferredDate(date: String) { _uiState.value = _uiState.value.copy(preferredDate = date) }
    fun setPreferredTimeSlot(slot: String) { _uiState.value = _uiState.value.copy(preferredTimeSlot = slot) }
    fun setPickupAddress(address: String) { _uiState.value = _uiState.value.copy(pickupAddress = address) }
    fun setPaymentMode(mode: String) { _uiState.value = _uiState.value.copy(paymentMode = mode) }
    fun setNotes(notes: String) { _uiState.value = _uiState.value.copy(notes = notes) }

    fun applyCoupon(code: String): Boolean {
        return if (code.trim().equals("DRIVE10", ignoreCase = true)) {
            val basePrice = _uiState.value.selectedCourse?.priceInr ?: 0.0
            val discount = basePrice * 0.10
            _uiState.value = _uiState.value.copy(discountCode = "DRIVE10", discountAmountInr = discount)
            true
        } else if (code.trim().equals("DDGOLD", ignoreCase = true)) {
            val basePrice = _uiState.value.selectedCourse?.priceInr ?: 0.0
            val discount = basePrice * 0.15
            _uiState.value = _uiState.value.copy(discountCode = "DDGOLD", discountAmountInr = discount)
            true
        } else {
            false
        }
    }

    private fun recalculatePrice() {
        val basePrice = _uiState.value.selectedCourse?.priceInr ?: 0.0
        val code = _uiState.value.discountCode
        val discount = if (code == "DRIVE10") basePrice * 0.10 else if (code == "DDGOLD") basePrice * 0.15 else 0.0
        _uiState.value = _uiState.value.copy(discountAmountInr = discount)
    }

    fun nextStep(): Boolean {
        val state = _uiState.value
        when (state.currentStep) {
            1 -> {
                if (state.studentName.isBlank() || state.mobileNumber.isBlank() || state.email.isBlank()) {
                    _uiState.value = state.copy(errorMessage = "Please enter your Name, Mobile Number, and Email.")
                    return false
                }
            }
            2 -> {
                if (state.selectedCourse == null) {
                    _uiState.value = state.copy(errorMessage = "Please select a Driving Course.")
                    return false
                }
            }
            3 -> {
                if (state.selectedInstructor == null) {
                    _uiState.value = state.copy(errorMessage = "Please select an Instructor.")
                    return false
                }
            }
            4 -> {
                if (state.pickupAddress.isBlank()) {
                    _uiState.value = state.copy(errorMessage = "Please specify your Doorstep Pickup Address.")
                    return false
                }
            }
        }
        _uiState.value = _uiState.value.copy(currentStep = state.currentStep + 1, errorMessage = null)
        return true
    }

    fun previousStep() {
        val state = _uiState.value
        if (state.currentStep > 1) {
            _uiState.value = state.copy(currentStep = state.currentStep - 1, errorMessage = null)
        }
    }

    fun submitBooking(onSuccess: (BookingEntity) -> Unit) {
        val state = _uiState.value
        val course = state.selectedCourse ?: return
        val instructor = state.selectedInstructor ?: return

        val finalPrice = (course.priceInr - state.discountAmountInr).coerceAtLeast(0.0)

        _uiState.value = state.copy(isSubmitting = true, errorMessage = null)

        viewModelScope.launch {
            val newBooking = BookingEntity(
                studentName = state.studentName,
                mobileNumber = state.mobileNumber,
                email = state.email,
                courseId = course.id,
                courseTitle = course.title,
                priceInr = finalPrice,
                instructorId = instructor.id,
                instructorName = instructor.name,
                preferredDate = state.preferredDate,
                preferredTimeSlot = state.preferredTimeSlot,
                pickupAddress = state.pickupAddress,
                paymentMode = state.paymentMode,
                status = "Confirmed",
                notes = state.notes
            )
            val bookingId = repository.createBooking(newBooking)
            val savedBooking = newBooking.copy(id = bookingId.toInt())

            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                confirmedBooking = savedBooking
            )
            onSuccess(savedBooking)
        }
    }

    fun resetForm() {
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _uiState.value = BookingUiState(preferredDate = dateFormat.format(calendar.time))
    }
}
