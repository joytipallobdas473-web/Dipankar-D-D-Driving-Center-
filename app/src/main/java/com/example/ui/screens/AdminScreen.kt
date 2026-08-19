package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CourseEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDChip
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel,
    onCloseAdminPortal: (() -> Unit)? = null
) {
    val isAuthenticated by adminViewModel.isAuthenticated.collectAsState()
    val pinInput by adminViewModel.adminPinInput.collectAsState()
    val loginError by adminViewModel.loginError.collectAsState()
    val analytics by adminViewModel.analytics.collectAsState()
    val bookings by adminViewModel.bookings.collectAsState()
    val courses by adminViewModel.courses.collectAsState()
    val instructors by adminViewModel.instructors.collectAsState()
    val notificationLog by adminViewModel.notificationLog.collectAsState()

    var activeAdminTab by remember { mutableIntStateOf(0) } 
    // 0: Overview, 1: Student Progress, 2: Bookings Pass, 3: Packages, 4: Instructors, 5: Security

    // Student Progress Tab states
    var studentSearchQuery by remember { mutableStateOf("") }
    var studentFilterStatus by remember { mutableStateOf("ALL") }

    // Form state for Editing or Creating Course/Package
    var isEditingCourse by remember { mutableStateOf(false) }
    var courseToEditId by remember { mutableIntStateOf(0) }
    var courseTitleInput by remember { mutableStateOf("") }
    var courseCategoryInput by remember { mutableStateOf("Manual") }
    var coursePriceInput by remember { mutableStateOf("5500") }
    var courseOriginalPriceInput by remember { mutableStateOf("7000") }
    var courseDurationWeeksInput by remember { mutableStateOf("3") }
    var courseHoursInput by remember { mutableStateOf("15") }
    var courseBadgeInput by remember { mutableStateOf("MOST POPULAR") }
    var courseDescInput by remember { mutableStateOf("") }
    var courseHighlightsInput by remember { mutableStateOf("") }

    // Dialog/Form states for Add Instructor
    var newInstName by remember { mutableStateOf("") }
    var newInstGender by remember { mutableStateOf("Female") }
    var newInstExp by remember { mutableStateOf("8") }
    var newInstVehicle by remember { mutableStateOf("Maruti Swift (Manual)") }
    var newInstSpec by remember { mutableStateOf("City Driving & Parallel Parking") }
    var newInstPhone by remember { mutableStateOf("+91 98888 77777") }
    var newInstBio by remember { mutableStateOf("Certified driving trainer.") }

    // Security PIN Change States
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var pinChangeMsg by remember { mutableStateOf<String?>(null) }
    var isPinChangeSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("admin_screen_column"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionHeader(
                        title = "Admin Portal & Control",
                        subtitle = "Manage Packages, Bookings, Instructors, Revenue & PIN Security"
                    )
                }
                if (onCloseAdminPortal != null) {
                    ThreeDButton(
                        onClick = onCloseAdminPortal,
                        text = "Exit",
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        containerColor = DeepNavy,
                        contentColor = BrightGold,
                        shadowColor = Color(0xFF0F172A),
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (!isAuthenticated) {
            // ADMIN PIN LOGIN CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrightGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = BrightGold, modifier = Modifier.size(28.dp))
                        }

                        Text("Secure Admin Login", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Enter Passcode to access D&D Driving Center backoffice (Default PIN: 1234)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        var isLoginPasswordVisible by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { adminViewModel.updatePinInput(it) },
                            label = { Text("Admin PIN / Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isLoginPasswordVisible = !isLoginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isLoginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (isLoginPasswordVisible) "Hide PIN" else "Show PIN",
                                        tint = BrightGold
                                    )
                                }
                            },
                            visualTransformation = if (isLoginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                autoCorrectEnabled = false,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { adminViewModel.loginWithPin() }
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_admin_pin"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Quick Auto-Fill Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = {
                                    adminViewModel.updatePinInput("1234")
                                },
                                modifier = Modifier.testTag("btn_autofill_admin_pin")
                            ) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Auto-Fill Default PIN (1234)", color = BrightGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (loginError != null) {
                            Text(loginError ?: "", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        ThreeDButton(
                            onClick = { adminViewModel.loginWithPin() },
                            text = "Unlock Admin Portal",
                            containerColor = BrightGold,
                            contentColor = DeepNavy,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_admin_login"
                        )
                    }
                }
            }
        } else {
            // AUTHENTICATED DASHBOARD
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Admin Session Active", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    TextButton(onClick = { adminViewModel.logout() }) {
                        Text("Lock Portal", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // Sub Navigation Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = activeAdminTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = BrightGold,
                    edgePadding = 0.dp,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(selected = activeAdminTab == 0, onClick = { activeAdminTab = 0 }, text = { Text("Overview") })
                    Tab(selected = activeAdminTab == 1, onClick = { activeAdminTab = 1 }, text = { Text("Student Progress (${bookings.size})") })
                    Tab(selected = activeAdminTab == 2, onClick = { activeAdminTab = 2 }, text = { Text("Bookings Pass") })
                    Tab(selected = activeAdminTab == 3, onClick = { activeAdminTab = 3 }, text = { Text("Packages (${courses.size})") })
                    Tab(selected = activeAdminTab == 4, onClick = { activeAdminTab = 4 }, text = { Text("Instructors") })
                    Tab(selected = activeAdminTab == 5, onClick = { activeAdminTab = 5 }, text = { Text("Security (PIN)") })
                }
            }

            if (activeAdminTab == 0) {
                // ANALYTICS OVERVIEW GRID
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = SapphireBlue)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Total Revenue (₹)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                    Text("₹${analytics.totalRevenueInr.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = BrightGold)
                                }
                            }

                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Active Students", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${analytics.activeStudents}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("RTO Pass Rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${analytics.completionRate}%", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SuccessGreen)
                                }
                            }

                            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Total Bookings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${analytics.totalBookings}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                // SYSTEM NOTIFICATION LOG
                item {
                    Text("System Activity & Notification Logs", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            notificationLog.take(6).forEach { log ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(log, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            } else if (activeAdminTab == 1) {
                // STUDENT PROGRESS RECORDS
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Student Training Progress Records", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Track completed vs pending lesson days & update daily attendance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        // Search Bar
                        OutlinedTextField(
                            value = studentSearchQuery,
                            onValueChange = { studentSearchQuery = it },
                            placeholder = { Text("Search student name or phone...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_search_student"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Filter Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filters = listOf("ALL", "In Progress", "Completed", "Pending")
                            filters.forEach { status ->
                                ThreeDChip(
                                    selected = studentFilterStatus == status,
                                    onClick = { studentFilterStatus = status },
                                    label = status,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                val filteredStudents = bookings.filter { booking ->
                    val matchesSearch = booking.studentName.contains(studentSearchQuery, ignoreCase = true) ||
                            booking.mobileNumber.contains(studentSearchQuery, ignoreCase = true)
                    val matchesFilter = when (studentFilterStatus) {
                        "In Progress" -> booking.completedDays > 0 && booking.completedDays < booking.totalDays
                        "Completed" -> booking.completedDays >= booking.totalDays || booking.status == "Completed"
                        "Pending" -> booking.completedDays == 0
                        else -> true
                    }
                    matchesSearch && matchesFilter
                }

                if (filteredStudents.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No student records match search criteria.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                items(filteredStudents) { student ->
                    val completed = student.completedDays
                    val total = if (student.totalDays > 0) student.totalDays else 15
                    val pending = (total - completed).coerceAtLeast(0)
                    val progressFraction = (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                    var isSyllabusExpanded by remember { mutableStateOf(false) }

                    val syllabusTopics = listOf(
                        "Day 1: Cockpit Drill, Seat Adjust & Mirror Setup",
                        "Day 2: Pedal Control (Clutch, Accelerator, Brake)",
                        "Day 3: Half-Clutch Hold & Smooth Biting Point",
                        "Day 4: Steering Geometry & 90° Corner Turns",
                        "Day 5: Gear Shifting Practice (1st to 4th Gear)",
                        "Day 6: Traffic Signals, Road Rules & Signs",
                        "Day 7: Slope & Hill Start without Roll-Back",
                        "Day 8: U-Turns, 3-Point Turns & Lane Changes",
                        "Day 9: Parallel Parking & Reverse Bay Parking",
                        "Day 10: Narrow Street Maneuvers & Reverse S-Turn",
                        "Day 11: City Bumper-to-Bumper Traffic Practice",
                        "Day 12: Flyovers & Elevated Highway Driving",
                        "Day 13: Night Driving & Headlight Beam Control",
                        "Day 14: Emergency Braking & Skid Control",
                        "Day 15: Official RTO Ground Mock Test & License Prep"
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("student_record_card_${student.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("${student.mobileNumber} • ${student.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                StatusBadge(
                                    text = if (completed >= total) "COMPLETED" else if (completed > 0) "IN PROGRESS" else "NEW / PENDING",
                                    containerColor = if (completed >= total) SuccessGreen.copy(alpha = 0.2f) else BrightGold.copy(alpha = 0.2f),
                                    contentColor = if (completed >= total) SuccessGreen else BrightGold
                                )
                            }

                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                            // Course & Trainer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Course: ${student.courseTitle}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SapphireBlue)
                                Text("Trainer: ${student.instructorName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            // Attendance Metric Box
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Daily Training Tracker", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${(progressFraction * 100).toInt()}% Completed", fontWeight = FontWeight.Black, fontSize = 12.sp, color = BrightGold)
                                    }

                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = BrightGold,
                                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Completed: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("$completed Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = BrightGold, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Pending: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("$pending Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrightGold)
                                        }

                                        Text("Total: $total Days", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }

                            // Remarks
                            if (student.notes.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notes, contentDescription = null, tint = BrightGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Notes: ${student.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Syllabus Toggle Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isSyllabusExpanded = !isSyllabusExpanded }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = SapphireBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View 15-Day Driving Syllabus Breakdown", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SapphireBlue)
                                }
                                Icon(
                                    if (isSyllabusExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = SapphireBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(visible = isSyllabusExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    syllabusTopics.take(total).forEachIndexed { index, topic ->
                                        val dayNum = index + 1
                                        val isDone = dayNum <= completed
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isDone) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = topic,
                                                fontSize = 11.sp,
                                                fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
                                                color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val newCompleted = (completed + 1).coerceAtMost(total)
                                        adminViewModel.updateStudentProgress(
                                            bookingId = student.id,
                                            completedDays = newCompleted,
                                            totalDays = total,
                                            notes = student.notes,
                                            studentName = student.studentName
                                        )
                                    },
                                    modifier = Modifier.weight(1f).testTag("btn_add_day_${student.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = DeepNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("+1 Day Done", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val newCompleted = (completed - 1).coerceAtLeast(0)
                                        adminViewModel.updateStudentProgress(
                                            bookingId = student.id,
                                            completedDays = newCompleted,
                                            totalDays = total,
                                            notes = student.notes,
                                            studentName = student.studentName
                                        )
                                    },
                                    modifier = Modifier.weight(1f).testTag("btn_sub_day_${student.id}"),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("-1 Day", fontSize = 10.sp)
                                }

                                Button(
                                    onClick = {
                                        adminViewModel.updateStudentProgress(
                                            bookingId = student.id,
                                            completedDays = total,
                                            totalDays = total,
                                            notes = "Course completed successfully!",
                                            studentName = student.studentName
                                        )
                                    },
                                    modifier = Modifier.weight(1.2f).testTag("btn_complete_course_${student.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Complete", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else if (activeAdminTab == 2) {
                // BOOKINGS PASS MANAGEMENT
                items(bookings) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pass #DD-${booking.id + 1000}", fontWeight = FontWeight.Bold, color = BrightGold, fontSize = 14.sp)
                                StatusBadge(
                                    text = booking.status,
                                    containerColor = if (booking.status == "Confirmed") SuccessGreen.copy(alpha = 0.2f) else Color.Yellow.copy(alpha = 0.2f),
                                    contentColor = if (booking.status == "Confirmed") SuccessGreen else BrightGold
                                )
                            }

                            Text("Student: ${booking.studentName} (${booking.mobileNumber})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Course: ${booking.courseTitle}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Slot: ${booking.preferredDate} (${booking.preferredTimeSlot})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Address: ${booking.pickupAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Revenue: ₹${booking.priceInr.toInt()} (${booking.paymentMode})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrightGold)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { adminViewModel.updateBookingStatus(booking.id, "Completed", booking.studentName) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Mark Completed", fontSize = 10.sp)
                                }

                                OutlinedButton(
                                    onClick = { adminViewModel.deleteBooking(booking.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Delete", fontSize = 10.sp, color = Color.Red)
                                }
                            }
                        }
                    }
                }
            } else if (activeAdminTab == 3) {
                // PACKAGE EDIT / MANAGEMENT
                item {
                    if (isEditingCourse) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold)
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = if (courseToEditId > 0) "Edit Package #${courseToEditId}" else "Create New Driving Package",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BrightGold
                                )

                                OutlinedTextField(
                                    value = courseTitleInput,
                                    onValueChange = { courseTitleInput = it },
                                    label = { Text("Package Title") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = courseCategoryInput,
                                        onValueChange = { courseCategoryInput = it },
                                        label = { Text("Category") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = courseBadgeInput,
                                        onValueChange = { courseBadgeInput = it },
                                        label = { Text("Badge Label") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = coursePriceInput,
                                        onValueChange = { coursePriceInput = it },
                                        label = { Text("Price (₹ INR)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = courseOriginalPriceInput,
                                        onValueChange = { courseOriginalPriceInput = it },
                                        label = { Text("Original Price (₹)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = courseDurationWeeksInput,
                                        onValueChange = { courseDurationWeeksInput = it },
                                        label = { Text("Duration (Weeks)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = courseHoursInput,
                                        onValueChange = { courseHoursInput = it },
                                        label = { Text("Total Hours") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                OutlinedTextField(
                                    value = courseDescInput,
                                    onValueChange = { courseDescInput = it },
                                    label = { Text("Description") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = courseHighlightsInput,
                                    onValueChange = { courseHighlightsInput = it },
                                    label = { Text("Highlights (Comma-separated)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ThreeDButton(
                                        onClick = { isEditingCourse = false },
                                        text = "Cancel",
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        shadowColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        modifier = Modifier.weight(1f)
                                    )

                                    ThreeDButton(
                                        onClick = {
                                            val price = coursePriceInput.toDoubleOrNull() ?: 5500.0
                                            val origPrice = courseOriginalPriceInput.toDoubleOrNull() ?: (price * 1.2)
                                            val weeks = courseDurationWeeksInput.toIntOrNull() ?: 2
                                            val hours = courseHoursInput.toIntOrNull() ?: 12

                                            if (courseToEditId > 0) {
                                                val updated = CourseEntity(
                                                    id = courseToEditId,
                                                    title = courseTitleInput.ifBlank { "Driving Course" },
                                                    category = courseCategoryInput.ifBlank { "General" },
                                                    durationWeeks = weeks,
                                                    totalHours = hours,
                                                    priceInr = price,
                                                    originalPriceInr = origPrice,
                                                    badge = courseBadgeInput.ifBlank { "POPULAR" },
                                                    description = courseDescInput,
                                                    highlights = courseHighlightsInput,
                                                    isPopular = true
                                                )
                                                adminViewModel.updateCourse(updated)
                                            } else {
                                                adminViewModel.addCourse(
                                                    courseTitleInput, courseCategoryInput, weeks, hours,
                                                    price, courseDescInput, courseHighlightsInput
                                                )
                                            }
                                            isEditingCourse = false
                                        },
                                        text = "Save Package",
                                        containerColor = BrightGold,
                                        contentColor = DeepNavy,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    } else {
                        ThreeDButton(
                            onClick = {
                                courseToEditId = 0
                                courseTitleInput = ""
                                courseCategoryInput = "Manual"
                                coursePriceInput = "6000"
                                courseOriginalPriceInput = "7500"
                                courseDurationWeeksInput = "2"
                                courseHoursInput = "12"
                                courseBadgeInput = "FEATURED"
                                courseDescInput = "Comprehensive driving package with flexible scheduling."
                                courseHighlightsInput = "Doorstep Pickup, RTO Assistance, Flexible Slots"
                                isEditingCourse = true
                            },
                            text = "+ Create New Package",
                            containerColor = BrightGold,
                            contentColor = DeepNavy,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (!isEditingCourse) {
                    items(courses) { course ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(course.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    StatusBadge(
                                        text = course.badge,
                                        containerColor = BrightGold.copy(alpha = 0.2f),
                                        contentColor = BrightGold
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Category: ${course.category} | ${course.totalHours} Hrs (${course.durationWeeks} Wks)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("₹${course.priceInr.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BrightGold)
                                }

                                Text(course.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ThreeDButton(
                                        onClick = {
                                            courseToEditId = course.id
                                            courseTitleInput = course.title
                                            courseCategoryInput = course.category
                                            coursePriceInput = course.priceInr.toInt().toString()
                                            courseOriginalPriceInput = course.originalPriceInr.toInt().toString()
                                            courseDurationWeeksInput = course.durationWeeks.toString()
                                            courseHoursInput = course.totalHours.toString()
                                            courseBadgeInput = course.badge
                                            courseDescInput = course.description
                                            courseHighlightsInput = course.highlights
                                            isEditingCourse = true
                                        },
                                        text = "Edit Package Details",
                                        icon = Icons.Default.Edit,
                                        containerColor = SapphireBlue,
                                        contentColor = Color.White,
                                        shadowColor = Color(0xFF1E3A8A),
                                        modifier = Modifier.weight(1f),
                                        fontSize = 11.sp
                                    )

                                    ThreeDButton(
                                        onClick = { adminViewModel.deleteCourse(course.id) },
                                        text = "Delete",
                                        icon = Icons.Default.Delete,
                                        containerColor = Color(0xFFDC2626),
                                        contentColor = Color.White,
                                        shadowColor = Color(0xFF991B1B),
                                        modifier = Modifier.width(100.dp),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (activeAdminTab == 4) {
                // INSTRUCTORS & ADD INSTRUCTOR FORM
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Register New Driving Instructor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = BrightGold)
                            }

                            OutlinedTextField(
                                value = newInstName,
                                onValueChange = { newInstName = it },
                                label = { Text("Instructor Full Name") },
                                modifier = Modifier.fillMaxWidth().testTag("input_new_inst_name"),
                                singleLine = true
                            )

                            Text("Select Gender / Trainer Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ThreeDChip(
                                    selected = newInstGender == "Female",
                                    onClick = { newInstGender = "Female" },
                                    label = "Female Trainer",
                                    modifier = Modifier.weight(1f)
                                )
                                ThreeDChip(
                                    selected = newInstGender == "Male",
                                    onClick = { newInstGender = "Male" },
                                    label = "Male Trainer",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Text("Quick Specialization / Trainer Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(
                                        selected = newInstSpec == "Female Student Mentor & City Traffic",
                                        onClick = { newInstSpec = "Female Student Mentor & City Traffic" },
                                        label = { Text("Female Mentor", fontSize = 10.sp) }
                                    )
                                    FilterChip(
                                        selected = newInstSpec == "Clutch Precision & Hill Start",
                                        onClick = { newInstSpec = "Clutch Precision & Hill Start" },
                                        label = { Text("Clutch & Hill Master", fontSize = 10.sp) }
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(
                                        selected = newInstSpec == "Automatic SUV & Highway Expert",
                                        onClick = { newInstSpec = "Automatic SUV & Highway Expert" },
                                        label = { Text("Automatic SUV Expert", fontSize = 10.sp) }
                                    )
                                    FilterChip(
                                        selected = newInstSpec == "Defensive Driving & Parking Mastery",
                                        onClick = { newInstSpec = "Defensive Driving & Parking Mastery" },
                                        label = { Text("Parallel Park Specialist", fontSize = 10.sp) }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = newInstSpec,
                                onValueChange = { newInstSpec = it },
                                label = { Text("Specialization / Instructor Type (Custom)") },
                                modifier = Modifier.fillMaxWidth().testTag("input_new_inst_spec"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newInstVehicle,
                                onValueChange = { newInstVehicle = it },
                                label = { Text("Vehicle Assigned (e.g. Creta Automatic, Swift Manual)") },
                                modifier = Modifier.fillMaxWidth().testTag("input_new_inst_vehicle"),
                                singleLine = true
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = newInstExp,
                                    onValueChange = { newInstExp = it },
                                    label = { Text("Exp (Years)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = newInstPhone,
                                    onValueChange = { newInstPhone = it },
                                    label = { Text("Phone Number") },
                                    modifier = Modifier.weight(1.5f),
                                    singleLine = true
                                )
                            }

                            ThreeDButton(
                                onClick = {
                                    if (newInstName.isNotBlank()) {
                                        adminViewModel.addInstructor(
                                            newInstName, newInstGender, newInstExp.toIntOrNull() ?: 5,
                                            newInstVehicle, newInstSpec, newInstPhone, newInstBio
                                        )
                                        newInstName = ""
                                        newInstSpec = "City Driving & Parallel Parking"
                                    }
                                },
                                text = "Save Instructor Record",
                                containerColor = BrightGold,
                                contentColor = DeepNavy,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_save_new_instructor"
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Registered Driving Instructors (${instructors.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BrightGold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(instructors) { inst ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("instructor_card_${inst.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${inst.name} (${inst.gender})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Type: ${inst.specialization}",
                                        fontSize = 12.sp,
                                        color = BrightGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                StatusBadge(
                                    text = if (inst.isAvailable) "Active" else "Off Duty",
                                    containerColor = if (inst.isAvailable) SuccessGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                                    contentColor = if (inst.isAvailable) SuccessGreen else Color.Red
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🚘 Vehicle: ${inst.vehicleAssigned}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("📞 ${inst.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🎓 ${inst.experienceYears} Years Exp", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("⭐ Rating: ${inst.rating} (${inst.reviewCount} reviews)", fontSize = 11.sp, color = BrightGold, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ThreeDChip(
                                    selected = inst.isAvailable,
                                    onClick = { adminViewModel.toggleInstructorAvailability(inst.id, inst.isAvailable) },
                                    label = if (inst.isAvailable) "Set Off Duty" else "Set Active",
                                    modifier = Modifier.weight(1f)
                                )

                                ThreeDButton(
                                    onClick = { adminViewModel.deleteInstructor(inst.id, inst.name) },
                                    text = "Delete",
                                    icon = Icons.Default.Delete,
                                    containerColor = Color(0xFFDC2626),
                                    contentColor = Color.White,
                                    shadowColor = Color(0xFF991B1B),
                                    modifier = Modifier.width(110.dp),
                                    fontSize = 11.sp,
                                    testTag = "btn_delete_instructor_${inst.id}"
                                )
                            }
                        }
                    }
                }
            } else if (activeAdminTab == 5) {
                // CHANGE ADMIN PIN / SECURITY
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = BrightGold, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Change Admin Password / PIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Text(
                                "Update your admin passcode for accessing control portal. The default PIN is 1234.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            var isOldPinVisible by remember { mutableStateOf(false) }
                            var isNewPinVisible by remember { mutableStateOf(false) }
                            var isConfirmPinVisible by remember { mutableStateOf(false) }

                            OutlinedTextField(
                                value = oldPinInput,
                                onValueChange = { oldPinInput = it; pinChangeMsg = null },
                                label = { Text("Current PIN / Password") },
                                trailingIcon = {
                                    IconButton(onClick = { isOldPinVisible = !isOldPinVisible }) {
                                        Icon(
                                            imageVector = if (isOldPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = BrightGold
                                        )
                                    }
                                },
                                visualTransformation = if (isOldPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    autoCorrectEnabled = false,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newPinInput,
                                onValueChange = { newPinInput = it; pinChangeMsg = null },
                                label = { Text("New Admin PIN / Password (Min 4 chars)") },
                                trailingIcon = {
                                    IconButton(onClick = { isNewPinVisible = !isNewPinVisible }) {
                                        Icon(
                                            imageVector = if (isNewPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = BrightGold
                                        )
                                    }
                                },
                                visualTransformation = if (isNewPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    autoCorrectEnabled = false,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = confirmPinInput,
                                onValueChange = { confirmPinInput = it; pinChangeMsg = null },
                                label = { Text("Confirm New Admin PIN / Password") },
                                trailingIcon = {
                                    IconButton(onClick = { isConfirmPinVisible = !isConfirmPinVisible }) {
                                        Icon(
                                            imageVector = if (isConfirmPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = BrightGold
                                        )
                                    }
                                },
                                visualTransformation = if (isConfirmPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    autoCorrectEnabled = false,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (pinChangeMsg != null) {
                                Text(
                                    text = pinChangeMsg ?: "",
                                    color = if (isPinChangeSuccess) SuccessGreen else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            ThreeDButton(
                                onClick = {
                                    if (newPinInput != confirmPinInput) {
                                        pinChangeMsg = "New PIN and Confirmation PIN do not match."
                                        isPinChangeSuccess = false
                                    } else {
                                        val err = adminViewModel.changeAdminPin(oldPinInput, newPinInput)
                                        if (err != null) {
                                            pinChangeMsg = err
                                            isPinChangeSuccess = false
                                        } else {
                                            pinChangeMsg = "Success! Admin PIN updated successfully."
                                            isPinChangeSuccess = true
                                            oldPinInput = ""
                                            newPinInput = ""
                                            confirmPinInput = ""
                                        }
                                    }
                                },
                                text = "Update Admin Security PIN",
                                containerColor = BrightGold,
                                contentColor = DeepNavy,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
