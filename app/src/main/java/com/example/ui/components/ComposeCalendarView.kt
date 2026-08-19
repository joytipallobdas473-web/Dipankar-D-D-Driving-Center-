package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeCalendarView(
    selectedDateStr: String,
    onDateSelected: (String) -> Unit,
    selectedTimeSlot: String,
    onTimeSlotSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displaySdf = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()) }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    var currentCalendar by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                try {
                    if (selectedDateStr.isNotBlank()) {
                        time = sdf.parse(selectedDateStr) ?: Date()
                    }
                } catch (e: Exception) {
                    time = Date()
                }
            }
        )
    }

    val todayCal = remember { Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) } }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    val morningSlots = listOf(
        "07:00 AM - 08:00 AM" to "Available",
        "08:00 AM - 09:00 AM" to "Popular",
        "09:00 AM - 10:00 AM" to "Available",
        "10:00 AM - 11:00 AM" to "3 Slots Left"
    )

    val eveningSlots = listOf(
        "04:00 PM - 05:00 PM" to "Popular",
        "05:00 PM - 06:00 PM" to "Available",
        "06:00 PM - 07:00 PM" to "2 Slots Left"
    )

    // Material 3 Date Picker Dialog Modal
    if (showDatePickerDialog) {
        val initialTimeMillis = remember(selectedDateStr) {
            try {
                if (selectedDateStr.isNotBlank()) sdf.parse(selectedDateStr)?.time ?: System.currentTimeMillis()
                else System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialTimeMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            val formattedDate = String.format(
                                Locale.getDefault(),
                                "%04d-%02d-%02d",
                                utcCal.get(Calendar.YEAR),
                                utcCal.get(Calendar.MONTH) + 1,
                                utcCal.get(Calendar.DAY_OF_MONTH)
                            )
                            onDateSelected(formattedDate)
                            try {
                                currentCalendar = Calendar.getInstance().apply {
                                    time = sdf.parse(formattedDate) ?: Date()
                                }
                            } catch (e: Exception) { }
                        }
                        showDatePickerDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_m3_datepicker")
                ) {
                    Text("Confirm Date", fontWeight = FontWeight.Bold, color = BrightGold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePickerDialog = false },
                    modifier = Modifier.testTag("btn_cancel_m3_datepicker")
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Select Lesson Date",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("compose_calendar_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Calendar Header with Month Navigation & M3 DatePicker Launcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = monthYearFormat.format(currentCalendar.time),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Material3 Date Picker Launch Button
                    IconButton(
                        onClick = { showDatePickerDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_open_m3_datepicker")
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Material3 Date Picker",
                            tint = BrightGold
                        )
                    }

                    IconButton(
                        onClick = {
                            val newCal = (currentCalendar.clone() as Calendar).apply {
                                add(Calendar.MONTH, -1)
                            }
                            // Only allow going back if month >= today's month
                            if (newCal.get(Calendar.YEAR) > todayCal.get(Calendar.YEAR) ||
                                (newCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                                 newCal.get(Calendar.MONTH) >= todayCal.get(Calendar.MONTH))) {
                                currentCalendar = newCal
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = {
                            currentCalendar = (currentCalendar.clone() as Calendar).apply {
                                add(Calendar.MONTH, 1)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Days of week header (Sun Mon Tue Wed Thu Fri Sat)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (dayName == "Sun") Color.Red.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Days Grid
            val monthCal = (currentCalendar.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed (0=Sun, 1=Mon...)
            val maxDaysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            val totalCells = firstDayOfWeek + maxDaysInMonth
            val totalRows = (totalCells + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until totalRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (col in 0 until 7) {
                            val dayIndex = row * 7 + col
                            val dayNumber = dayIndex - firstDayOfWeek + 1

                            if (dayNumber in 1..maxDaysInMonth) {
                                val cellCal = (monthCal.clone() as Calendar).apply {
                                    set(Calendar.DAY_OF_MONTH, dayNumber)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }

                                val cellDateStr = sdf.format(cellCal.time)
                                val isPast = cellCal.before(todayCal)
                                val isSelected = cellDateStr == selectedDateStr
                                val isToday = cellCal.timeInMillis == todayCal.timeInMillis

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when {
                                                isSelected -> BrightGold
                                                isToday -> BrightGold.copy(alpha = 0.2f)
                                                isPast -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                        .border(
                                            width = if (isToday) 1.5.dp else 0.dp,
                                            color = BrightGold,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable(enabled = !isPast) {
                                            onDateSelected(cellDateStr)
                                        }
                                        .testTag("cal_day_$dayNumber"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dayNumber",
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = when {
                                            isSelected -> DeepNavy
                                            isPast -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(38.dp))
                            }
                        }
                    }
                }
            }

            // Formatted Selected Date Label
            if (selectedDateStr.isNotBlank()) {
                val dateText = try {
                    val date = sdf.parse(selectedDateStr)
                    if (date != null) displaySdf.format(date) else selectedDateStr
                } catch (e: Exception) {
                    selectedDateStr
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SapphireBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selected Date: $dateText",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // 3D TIME SLOT PREVIEW COMPONENT
            TimeSlot3DPreviewCanvas(
                selectedTimeSlot = selectedTimeSlot,
                onTimeSlotSelected = onTimeSlotSelected,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // TIME SLOTS SECTION
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = BrightGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Standard Batch Selector", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Morning Batches
            Text("Morning Batches (Doorstep Pickup)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                morningSlots.chunked(2).forEach { rowSlots ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSlots.forEach { (slot, badge) ->
                            val isSelected = selectedTimeSlot == slot
                            SlotChipItem(
                                slot = slot,
                                badge = badge,
                                isSelected = isSelected,
                                onClick = { onTimeSlotSelected(slot) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Evening Batches
            Spacer(modifier = Modifier.height(2.dp))
            Text("Evening Batches (Traffic Practice)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                eveningSlots.chunked(2).forEach { rowSlots ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSlots.forEach { (slot, badge) ->
                            val isSelected = selectedTimeSlot == slot
                            SlotChipItem(
                                slot = slot,
                                badge = badge,
                                isSelected = isSelected,
                                onClick = { onTimeSlotSelected(slot) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotChipItem(
    slot: String,
    badge: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BrightGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (isSelected) BrightGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = slot,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) BrightGold else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = badge,
                fontSize = 9.sp,
                color = if (isSelected) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
