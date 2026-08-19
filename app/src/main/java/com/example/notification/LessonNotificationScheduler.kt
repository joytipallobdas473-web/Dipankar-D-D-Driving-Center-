package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.data.BookingEntity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ScheduledReminderItem(
    val bookingId: Int,
    val studentName: String,
    val courseTitle: String,
    val dateStr: String,
    val timeSlot: String,
    val instructorName: String,
    val reminderLeadMinutes: Int,
    val scheduledTimeEpochMs: Long,
    val isEnabled: Boolean = true
)

object LessonNotificationScheduler {

    const val CHANNEL_ID = "driving_lesson_reminders"
    const val CHANNEL_NAME = "Driving Lesson Reminders"
    const val CHANNEL_DESC = "Notifications and reminders before scheduled driving lessons at D&D Driving Center"
    private const val PREFS_NAME = "lesson_reminder_prefs"
    private const val KEY_REMINDERS_JSON = "key_scheduled_reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun scheduleLessonReminder(
        context: Context,
        booking: BookingEntity,
        leadTimeMinutes: Int = 60
    ): Boolean {
        createNotificationChannel(context)

        // Parse date and time
        val triggerTimeMs = calculateTriggerTimeMs(booking.preferredDate, booking.preferredTimeSlot, leadTimeMinutes)

        val intent = Intent(context, LessonReminderReceiver::class.java).apply {
            action = "com.example.ACTION_LESSON_REMINDER"
            putExtra(LessonReminderReceiver.EXTRA_BOOKING_ID, booking.id)
            putExtra(LessonReminderReceiver.EXTRA_STUDENT_NAME, booking.studentName)
            putExtra(LessonReminderReceiver.EXTRA_COURSE_TITLE, booking.courseTitle)
            putExtra(LessonReminderReceiver.EXTRA_TIME_SLOT, booking.preferredTimeSlot)
            putExtra(LessonReminderReceiver.EXTRA_INSTRUCTOR_NAME, booking.instructorName)
            putExtra(LessonReminderReceiver.EXTRA_PICKUP_ADDRESS, booking.pickupAddress)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            booking.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for strict alarm permissions
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
        }

        // Save to SharedPreferences for UI component listing
        val reminderItem = ScheduledReminderItem(
            bookingId = booking.id,
            studentName = booking.studentName,
            courseTitle = booking.courseTitle,
            dateStr = booking.preferredDate,
            timeSlot = booking.preferredTimeSlot,
            instructorName = booking.instructorName,
            reminderLeadMinutes = leadTimeMinutes,
            scheduledTimeEpochMs = triggerTimeMs
        )
        saveScheduledReminder(context, reminderItem)
        return true
    }

    fun cancelLessonReminder(context: Context, bookingId: Int) {
        val intent = Intent(context, LessonReminderReceiver::class.java).apply {
            action = "com.example.ACTION_LESSON_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            bookingId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        removeScheduledReminder(context, bookingId)
    }

    fun sendImmediateTestReminder(
        context: Context,
        studentName: String = "Test Student",
        courseTitle: String = "Manual Car Mastery",
        timeSlot: String = "08:00 AM - 09:00 AM",
        instructorName: String = "Rajesh Sharma",
        pickupAddress: String = "D&D Training Grounds, Block B"
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, LessonReminderReceiver::class.java).apply {
            action = "com.example.ACTION_LESSON_REMINDER"
            putExtra(LessonReminderReceiver.EXTRA_BOOKING_ID, (1000..9999).random())
            putExtra(LessonReminderReceiver.EXTRA_STUDENT_NAME, studentName)
            putExtra(LessonReminderReceiver.EXTRA_COURSE_TITLE, courseTitle)
            putExtra(LessonReminderReceiver.EXTRA_TIME_SLOT, timeSlot)
            putExtra(LessonReminderReceiver.EXTRA_INSTRUCTOR_NAME, instructorName)
            putExtra(LessonReminderReceiver.EXTRA_PICKUP_ADDRESS, pickupAddress)
        }

        context.sendBroadcast(intent)
    }

    fun getScheduledReminders(context: Context): List<ScheduledReminderItem> {
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_REMINDERS_JSON, "[]") ?: "[]"
        val result = mutableListOf<ScheduledReminderItem>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                result.add(
                    ScheduledReminderItem(
                        bookingId = obj.getInt("bookingId"),
                        studentName = obj.getString("studentName"),
                        courseTitle = obj.getString("courseTitle"),
                        dateStr = obj.getString("dateStr"),
                        timeSlot = obj.getString("timeSlot"),
                        instructorName = obj.getString("instructorName"),
                        reminderLeadMinutes = obj.getInt("reminderLeadMinutes"),
                        scheduledTimeEpochMs = obj.getLong("scheduledTimeEpochMs"),
                        isEnabled = obj.optBoolean("isEnabled", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun saveScheduledReminder(context: Context, item: ScheduledReminderItem) {
        val currentList = getScheduledReminders(context).filter { it.bookingId != item.bookingId }.toMutableList()
        currentList.add(item)
        saveListToPrefs(context, currentList)
    }

    private fun removeScheduledReminder(context: Context, bookingId: Int) {
        val currentList = getScheduledReminders(context).filter { it.bookingId != bookingId }
        saveListToPrefs(context, currentList)
    }

    private fun saveListToPrefs(context: Context, list: List<ScheduledReminderItem>) {
        val jsonArray = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("bookingId", item.bookingId)
                put("studentName", item.studentName)
                put("courseTitle", item.courseTitle)
                put("dateStr", item.dateStr)
                put("timeSlot", item.timeSlot)
                put("instructorName", item.instructorName)
                put("reminderLeadMinutes", item.reminderLeadMinutes)
                put("scheduledTimeEpochMs", item.scheduledTimeEpochMs)
                put("isEnabled", item.isEnabled)
            }
            jsonArray.put(obj)
        }
        getPrefs(context).edit().putString(KEY_REMINDERS_JSON, jsonArray.toString()).apply()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun calculateTriggerTimeMs(dateStr: String, timeSlotStr: String, leadTimeMinutes: Int): Long {
        val calendar = Calendar.getInstance()
        try {
            // e.g. dateStr = "2026-08-15"
            val dateParts = dateStr.split("-")
            if (dateParts.size == 3) {
                val year = dateParts[0].toInt()
                val month = dateParts[1].toInt() - 1
                val day = dateParts[2].toInt()
                calendar.set(year, month, day)
            }

            // Parse time e.g. "08:00 AM - 09:00 AM" -> start time "08:00 AM"
            val startTimeStr = timeSlotStr.split("-").firstOrNull()?.trim() ?: "08:00 AM"
            val sdf = SimpleDateFormat("hh:mm a", Locale.US)
            val parsedTime = sdf.parse(startTimeStr)
            if (parsedTime != null) {
                val timeCal = Calendar.getInstance().apply { time = parsedTime }
                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Subtract lead time
        calendar.add(Calendar.MINUTE, -leadTimeMinutes)

        // If trigger time is already in the past, schedule for 1 minute from now for testing
        val now = System.currentTimeMillis()
        return if (calendar.timeInMillis <= now) {
            now + (60 * 1000)
        } else {
            calendar.timeInMillis
        }
    }
}
