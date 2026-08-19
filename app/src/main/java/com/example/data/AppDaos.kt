package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY bookingTimestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: Int): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    @Query("UPDATE bookings SET completedDays = :completedDays, pendingDays = :pendingDays, status = :status, notes = :notes WHERE id = :id")
    suspend fun updateStudentProgress(id: Int, completedDays: Int, pendingDays: Int, status: String, notes: String)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY id ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCourses(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteCourseById(id: Int)
}

@Dao
interface InstructorDao {
    @Query("SELECT * FROM instructors ORDER BY rating DESC")
    fun getAllInstructors(): Flow<List<InstructorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInstructors(instructors: List<InstructorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructor(instructor: InstructorEntity): Long

    @Query("UPDATE instructors SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateAvailability(id: Int, isAvailable: Boolean)

    @Query("DELETE FROM instructors WHERE id = :id")
    suspend fun deleteInstructorById(id: Int)
}

@Dao
interface TestimonialDao {
    @Query("SELECT * FROM testimonials ORDER BY id DESC")
    fun getAllTestimonials(): Flow<List<TestimonialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestimonial(testimonial: TestimonialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTestimonials(testimonials: List<TestimonialEntity>)
}

@Dao
interface GalleryDao {
    @Query("SELECT * FROM gallery_items ORDER BY id ASC")
    fun getAllGalleryItems(): Flow<List<GalleryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGalleryItems(items: List<GalleryItemEntity>)
}
