package com.example.ui.components

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEntity
import com.example.notification.LessonNotificationScheduler
import com.example.notification.ScheduledReminderItem
import com.example.ui.theme.*

@Composable
fun LessonNotificationSchedulerComponent(
    bookings: List<BookingEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(LessonNotificationScheduler.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted! Lesson reminders enabled.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission denied. You can enable notifications in System Settings.", Toast.LENGTH_SHORT).show()
        }
    }

    var selectedLeadMinutes by remember { mutableIntStateOf(60) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var isSmsSimulated by remember { mutableStateOf(true) }
    var scheduledReminders by remember {
        mutableStateOf(LessonNotificationScheduler.getScheduledReminders(context))
    }

    // Auto-Sync active bookings into reminder scheduler if empty
    LaunchedEffect(bookings) {
        if (bookings.isNotEmpty()) {
            bookings.forEach { booking ->
                LessonNotificationScheduler.scheduleLessonReminder(context, booking, selectedLeadMinutes)
            }
            scheduledReminders = LessonNotificationScheduler.getScheduledReminders(context)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("notification_scheduler_component_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // HEADER ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ImperialGold.copy(alpha = 0.2f))
                            .border(1.dp, ImperialGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = "Lesson Reminder Scheduler",
                            tint = ImperialGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Lesson Reminder Scheduler",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Automated lesson alerts before practical sessions",
                            fontSize = 11.sp,
                            color = ImperialGold
                        )
                    }
                }

                Surface(
                    color = if (hasPermission) EmeraldGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (hasPermission) EmeraldGreen else Color.Red)
                ) {
                    Text(
                        text = if (hasPermission) "ACTIVE" else "REQUIRES PERMISSION",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasPermission) EmeraldGreen else Color.Red,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PERMISSION ALERT BANNER (IF NOT GRANTED ON ANDROID 13+)
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Surface(
                    color = MidnightNavy,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = BrightGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Allow system notifications to receive lesson countdowns.",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("ENABLE", fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }
                }
            }

            // LEAD TIME SELECTOR CHIPS
            Text(
                text = "Send Reminder Alert:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            val leadOptions = listOf(
                15 to "15 Mins Before",
                60 to "1 Hour Before",
                180 to "3 Hours Before",
                1440 to "1 Day Before"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(leadOptions) { (mins, label) ->
                    val isSelected = selectedLeadMinutes == mins
                    Surface(
                        onClick = {
                            selectedLeadMinutes = mins
                            // Reschedule active bookings with new lead time
                            bookings.forEach { booking ->
                                LessonNotificationScheduler.scheduleLessonReminder(context, booking, mins)
                            }
                            scheduledReminders = LessonNotificationScheduler.getScheduledReminders(context)
                            Toast.makeText(context, "Reminder timing set to $label", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ImperialGold else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ImperialGold else Color.Gray.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("lead_time_chip_$mins")
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            color = if (isSelected) ObsidianBlack else Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // NOTIFICATION CHANNEL TOGGLES ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sound & Vibration Alert", fontSize = 12.sp, color = Color.White)
                }
                Switch(
                    checked = isSoundEnabled,
                    onCheckedChange = { isSoundEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = ImperialGold)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sms, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp / SMS Preview Copy", fontSize = 12.sp, color = Color.White)
                }
                Switch(
                    checked = isSmsSimulated,
                    onCheckedChange = { isSmsSimulated = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = ImperialGold)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ACTIVE SCHEDULED REMINDERS LIST
            Text(
                text = "Active Scheduled Lesson Reminders (${scheduledReminders.size}):",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (scheduledReminders.isEmpty()) {
                Surface(
                    color = MidnightNavy,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No driving lessons currently scheduled.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Reminders are automatically set when you book a driving course slot.",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                scheduledReminders.forEach { reminder ->
                    Surface(
                        color = MidnightNavy,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(reminder.courseTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Date: ${reminder.dateStr} • Slot: ${reminder.timeSlot}",
                                    fontSize = 11.sp,
                                    color = BrightGold
                                )
                                Text(
                                    text = "Student: ${reminder.studentName} • Instructor: ${reminder.instructorName}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    LessonNotificationScheduler.cancelLessonReminder(context, reminder.bookingId)
                                    scheduledReminders = LessonNotificationScheduler.getScheduledReminders(context)
                                    Toast.makeText(context, "Reminder cancelled for ${reminder.courseTitle}", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Cancel Reminder", tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ONE-TAP TEST INSTANT REMINDER BUTTON
            ThreeDButton(
                onClick = {
                    val activeBooking = bookings.firstOrNull()
                    LessonNotificationScheduler.sendImmediateTestReminder(
                        context = context,
                        studentName = activeBooking?.studentName ?: "Anand Sharma",
                        courseTitle = activeBooking?.courseTitle ?: "Executive Manual Driving Mastery",
                        timeSlot = activeBooking?.preferredTimeSlot ?: "08:00 AM - 09:00 AM",
                        instructorName = activeBooking?.instructorName ?: "Rajesh Sharma",
                        pickupAddress = activeBooking?.pickupAddress ?: "D&D Driving Center Academy Track"
                    )
                    Toast.makeText(context, "🔔 Test Lesson Reminder Notification Sent!", Toast.LENGTH_SHORT).show()
                },
                text = "TRIGGER TEST INSTANT REMINDER NOTIFICATION",
                icon = Icons.Default.AddAlert,
                containerColor = ImperialGold,
                shadowColor = WarmBronze,
                contentColor = ObsidianBlack,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_test_instant_reminder"
            )
        }
    }
}
