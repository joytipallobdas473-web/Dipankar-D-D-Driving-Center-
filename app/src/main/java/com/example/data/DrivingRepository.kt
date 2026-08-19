package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DrivingRepository(private val db: AppDatabase) {
    val allBookings: Flow<List<BookingEntity>> = db.bookingDao().getAllBookings()
    val allCourses: Flow<List<CourseEntity>> = db.courseDao().getAllCourses()
    val allInstructors: Flow<List<InstructorEntity>> = db.instructorDao().getAllInstructors()
    val allTestimonials: Flow<List<TestimonialEntity>> = db.testimonialDao().getAllTestimonials()
    val allGalleryItems: Flow<List<GalleryItemEntity>> = db.galleryDao().getAllGalleryItems()

    suspend fun createBooking(booking: BookingEntity): Long {
        return db.bookingDao().insertBooking(booking)
    }

    suspend fun updateBookingStatus(id: Int, status: String) {
        db.bookingDao().updateBookingStatus(id, status)
    }

    suspend fun updateStudentProgress(id: Int, completedDays: Int, pendingDays: Int, status: String, notes: String) {
        db.bookingDao().updateStudentProgress(id, completedDays, pendingDays, status, notes)
    }

    suspend fun deleteBooking(id: Int) {
        db.bookingDao().deleteBookingById(id)
    }

    suspend fun addInstructor(instructor: InstructorEntity): Long {
        return db.instructorDao().insertInstructor(instructor)
    }

    suspend fun updateInstructorAvailability(id: Int, isAvailable: Boolean) {
        db.instructorDao().updateAvailability(id, isAvailable)
    }

    suspend fun deleteInstructor(id: Int) {
        db.instructorDao().deleteInstructorById(id)
    }

    suspend fun addCourse(course: CourseEntity): Long {
        return db.courseDao().insertCourse(course)
    }

    suspend fun updateCourse(course: CourseEntity) {
        db.courseDao().updateCourse(course)
    }

    suspend fun deleteCourse(id: Int) {
        db.courseDao().deleteCourseById(id)
    }

    suspend fun addTestimonial(testimonial: TestimonialEntity) {
        db.testimonialDao().insertTestimonial(testimonial)
    }

    suspend fun initializeDefaultDataIfEmpty() {
        val existingCourses = db.courseDao().getAllCourses().first()
        if (existingCourses.isEmpty()) {
            db.courseDao().insertAllCourses(defaultCourses)
        }

        val existingInstructors = db.instructorDao().getAllInstructors().first()
        if (existingInstructors.isEmpty()) {
            db.instructorDao().insertAllInstructors(defaultInstructors)
        }

        val existingTestimonials = db.testimonialDao().getAllTestimonials().first()
        if (existingTestimonials.isEmpty()) {
            db.testimonialDao().insertAllTestimonials(defaultTestimonials)
        }

        val existingGallery = db.galleryDao().getAllGalleryItems().first()
        if (existingGallery.isEmpty()) {
            db.galleryDao().insertAllGalleryItems(defaultGallery)
        }

        val existingBookings = db.bookingDao().getAllBookings().first()
        if (existingBookings.isEmpty()) {
            db.bookingDao().insertBooking(
                BookingEntity(
                    studentName = "Rahul Sharma",
                    mobileNumber = "+91 98765 43210",
                    email = "rahul.sharma@example.com",
                    courseId = 1,
                    courseTitle = "Comprehensive Hatchback (Manual)",
                    priceInr = 5500.0,
                    instructorId = 1,
                    instructorName = "Rajesh Kumar (Senior Trainer)",
                    preferredDate = "2026-08-10",
                    preferredTimeSlot = "08:00 AM - 09:00 AM",
                    pickupAddress = "Sector 14, Connaught Place, New Delhi",
                    paymentMode = "UPI (Google Pay)",
                    status = "In Progress",
                    notes = "Doorstep pickup requested at main gate. Good progress on clutch.",
                    totalDays = 15,
                    completedDays = 6,
                    pendingDays = 9
                )
            )
            db.bookingDao().insertBooking(
                BookingEntity(
                    studentName = "Ananya Roy",
                    mobileNumber = "+91 91234 56789",
                    email = "ananya.roy@example.com",
                    courseId = 2,
                    courseTitle = "Automatic SUV Masterclass",
                    priceInr = 7800.0,
                    instructorId = 2,
                    instructorName = "Priya Sharma (Female Specialist)",
                    preferredDate = "2026-08-11",
                    preferredTimeSlot = "10:00 AM - 11:00 AM",
                    pickupAddress = "B-402, Green Park Extension, New Delhi",
                    paymentMode = "Credit Card",
                    status = "In Progress",
                    notes = "Prefers female trainer. Steering and reverse parking covered.",
                    totalDays = 12,
                    completedDays = 2,
                    pendingDays = 10
                )
            )
        }
    }

    companion object {
        val defaultCourses = listOf(
            CourseEntity(
                id = 1,
                title = "Manual Hatchback Foundations",
                category = "Manual",
                durationWeeks = 3,
                totalHours = 15,
                priceInr = 5500.0,
                originalPriceInr = 7000.0,
                badge = "MOST POPULAR",
                description = "Master clutch control, gear shifting, steep incline holding, and tight city lane navigation with Dual-Control Maruti Swift.",
                highlights = "15 Hours Practical, Doorstep Pickup, Hill-Hold Practice, RTO Test Preparation, Free Mock Test",
                isPopular = true
            ),
            CourseEntity(
                id = 2,
                title = "Automatic SUV City & Highway Masterclass",
                category = "Automatic",
                durationWeeks = 2,
                totalHours = 12,
                priceInr = 7800.0,
                originalPriceInr = 9500.0,
                badge = "LUXURY FLEET",
                description = "Learn effortlessly on modern Automatic Hyundai Creta / Brezza with advanced parking sensors, cruise control, and night driving.",
                highlights = "12 Hours SUV Driving, Automatic Transmission, Reverse Camera Parking, Expressway Driving, Dual Airbags Safety",
                isPopular = false
            ),
            CourseEntity(
                id = 3,
                title = "Express 7-Day License Fast-Track",
                category = "Express",
                durationWeeks = 1,
                totalHours = 14,
                priceInr = 8900.0,
                originalPriceInr = 11000.0,
                badge = "FAST TRACK",
                description = "Intensive daily 2-hour sessions tailored for working professionals and students needing rapid certification and confidence.",
                highlights = "Daily 2-Hour Intensive, Dedicated Senior Instructor, Unlimited Simulator Sessions, Priority RTO Assistance",
                isPopular = false
            ),
            CourseEntity(
                id = 4,
                title = "Refresher & Defensive Driving Package",
                category = "Refresher",
                durationWeeks = 1,
                totalHours = 6,
                priceInr = 3200.0,
                originalPriceInr = 4500.0,
                badge = "QUICK REFRESH",
                description = "For license holders who need to regain confidence in heavy traffic, parallel parking, flyovers, and rainy weather conditions.",
                highlights = "6 Hours Highway & Traffic Driving, Parallel & Angular Parking, Traffic Signals & Signage Mastery",
                isPopular = false
            ),
            CourseEntity(
                id = 5,
                title = "Complete RTO Road Test Prep & Trial",
                category = "License Prep",
                durationWeeks = 1,
                totalHours = 5,
                priceInr = 2800.0,
                originalPriceInr = 3800.0,
                badge = "100% PASS GUARANTEE",
                description = "Specialized track simulation focusing on Figure-8, H-Track, parallel parking, and official RTO evaluator checklist.",
                highlights = "Figure-8 & H-Track Track Practice, Official Vehicle for RTO Exam, Document Verification Guidance",
                isPopular = false
            )
        )

        val defaultInstructors = listOf(
            InstructorEntity(
                id = 1,
                name = "Rajesh Kumar",
                gender = "Male",
                experienceYears = 14,
                rating = 4.9,
                reviewCount = 420,
                vehicleAssigned = "Maruti Swift (Manual)",
                specialization = "Clutch Precision & Hill Start",
                bio = "Ex-Defense driver with 14+ years of driving instruction experience. Specializes in helping nervous beginners master manual transmission.",
                isAvailable = true,
                phone = "+91 8403050225"
            ),
            InstructorEntity(
                id = 2,
                name = "Priya Sharma",
                gender = "Female",
                experienceYears = 9,
                rating = 4.95,
                reviewCount = 380,
                vehicleAssigned = "Hyundai Creta (Automatic)",
                specialization = "Female Student Mentor & City Traffic",
                bio = "Certified female driving specialist. Patient, supportive teaching approach with expertise in high-density city traffic navigation.",
                isAvailable = true,
                phone = "+91 9101303239"
            ),
            InstructorEntity(
                id = 3,
                name = "Sunita Verma",
                gender = "Female",
                experienceYears = 11,
                rating = 4.88,
                reviewCount = 290,
                vehicleAssigned = "Tata Nexon (Manual/Auto)",
                specialization = "Defensive Driving & Parking Mastery",
                bio = "Specialist in parallel parking, tight alley maneuvers, and emergency handling. Over 1,200 successful female students trained.",
                isAvailable = true,
                phone = "+91 8403050225"
            ),
            InstructorEntity(
                id = 4,
                name = "Vikramaditya Singh",
                gender = "Male",
                experienceYears = 16,
                rating = 4.92,
                reviewCount = 510,
                vehicleAssigned = "Honda City (Manual)",
                specialization = "Expressway & Night Driving",
                bio = "Chief Driving Instructor at D&D. Certified Motor Driving School Master with advanced defensive driving credentials.",
                isAvailable = true,
                phone = "+91 9101303239"
            )
        )

        val defaultTestimonials = listOf(
            TestimonialEntity(
                id = 1,
                studentName = "Kavita Singhania",
                courseTaken = "Automatic SUV Masterclass",
                rating = 5,
                reviewText = "Priya Ma'am was so encouraging! I went from zero confidence to driving independently through Chandni Chowk traffic in just 10 days. Doorstep pickup was super convenient!",
                date = "2 days ago"
            ),
            TestimonialEntity(
                id = 2,
                studentName = "Amitabh Bannerjee",
                courseTaken = "Manual Hatchback Foundations",
                rating = 5,
                reviewText = "D&D Driving Center is top-notch! Rajesh sir taught me hill-hold and clutch biting point technique so clearly. Passed my RTO test on the first attempt!",
                date = "1 week ago"
            ),
            TestimonialEntity(
                id = 3,
                studentName = "Deepika Patel",
                courseTaken = "Express 7-Day Fast-Track",
                rating = 5,
                reviewText = "Having female instructors available was a huge plus for me. The 3D simulator practice before real road driving helped ease my anxiety completely.",
                date = "2 weeks ago"
            )
        )

        val defaultGallery = listOf(
            GalleryItemEntity(
                id = 1,
                title = "Dual-Control Hatchback & SUV Fleet",
                category = "Fleet",
                description = "State-of-the-art dual-control vehicles inspected daily for student safety."
            ),
            GalleryItemEntity(
                id = 2,
                title = "Private RTO Simulator Track",
                category = "Infrastructure",
                description = "Exclusive training ground equipped with Figure-8, H-Track, and gradient ramps."
            ),
            GalleryItemEntity(
                id = 3,
                title = "Interactive 3D Driving Simulator Lab",
                category = "Infrastructure",
                description = "High-tech realistic cockpit simulator with steering force feedback and weather simulation."
            ),
            GalleryItemEntity(
                id = 4,
                title = "License Celebration with Happy Students",
                category = "Happy Students",
                description = "Over 12,500+ successful drivers certified with 99.4% first-time pass rate."
            )
        )
    }
}
