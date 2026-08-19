package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val mobileNumber: String,
    val email: String,
    val courseId: Int,
    val courseTitle: String,
    val priceInr: Double,
    val instructorId: Int,
    val instructorName: String,
    val preferredDate: String,
    val preferredTimeSlot: String,
    val pickupAddress: String,
    val paymentMode: String,
    val status: String = "Confirmed",
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val totalDays: Int = 15,
    val completedDays: Int = 0,
    val pendingDays: Int = 15
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Manual, Automatic, Express, Refresher, License Prep
    val durationWeeks: Int,
    val totalHours: Int,
    val priceInr: Double,
    val originalPriceInr: Double,
    val badge: String,
    val description: String,
    val highlights: String,
    val isPopular: Boolean = false
)

@Entity(tableName = "instructors")
data class InstructorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: String, // Female, Male
    val experienceYears: Int,
    val rating: Double,
    val reviewCount: Int,
    val vehicleAssigned: String,
    val specialization: String,
    val bio: String,
    val isAvailable: Boolean = true,
    val phone: String
)

@Entity(tableName = "testimonials")
data class TestimonialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String,
    val courseTaken: String,
    val rating: Int,
    val reviewText: String,
    val date: String
)

@Entity(tableName = "gallery_items")
data class GalleryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Infrastructure, Lessons, Fleet, Happy Students
    val description: String
)
