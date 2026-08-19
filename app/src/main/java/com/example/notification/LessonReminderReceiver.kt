package com.example.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

class LessonReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val studentName = intent.getStringExtra(EXTRA_STUDENT_NAME) ?: "Student"
        val courseTitle = intent.getStringExtra(EXTRA_COURSE_TITLE) ?: "Driving Lesson"
        val timeSlot = intent.getStringExtra(EXTRA_TIME_SLOT) ?: "Upcoming Slot"
        val instructorName = intent.getStringExtra(EXTRA_INSTRUCTOR_NAME) ?: "D&D Driving Instructor"
        val pickupAddress = intent.getStringExtra(EXTRA_PICKUP_ADDRESS) ?: "Assigned Pickup Location"
        val bookingId = intent.getIntExtra(EXTRA_BOOKING_ID, 1001)

        // Ensure channel exists
        LessonNotificationScheduler.createNotificationChannel(context)

        // Deep link intent back to MainActivity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TAB", "BOOKING")
            putExtra("BOOKING_ID", bookingId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            bookingId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(context, LessonNotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🚗 Driving Lesson Reminder • D&D Driving Center")
            .setContentText("Hi $studentName! Your '$courseTitle' lesson with $instructorName is scheduled for $timeSlot.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Hi $studentName! 🚗\n\nYour upcoming driving session ($courseTitle) with $instructorName is starting soon ($timeSlot).\n\n📍 Pickup Address: $pickupAddress\n\nRule Reminder: Please carry your Learner's License & wear comfortable footwear!"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bookingId, notification)
    }

    companion object {
        const val EXTRA_BOOKING_ID = "extra_booking_id"
        const val EXTRA_STUDENT_NAME = "extra_student_name"
        const val EXTRA_COURSE_TITLE = "extra_course_title"
        const val EXTRA_TIME_SLOT = "extra_time_slot"
        const val EXTRA_INSTRUCTOR_NAME = "extra_instructor_name"
        const val EXTRA_PICKUP_ADDRESS = "extra_pickup_address"
    }
}
