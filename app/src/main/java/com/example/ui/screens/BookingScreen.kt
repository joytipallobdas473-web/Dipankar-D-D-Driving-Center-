package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEntity
import com.example.data.CourseEntity
import com.example.data.InstructorEntity
import com.example.notification.LessonNotificationScheduler
import com.example.ui.components.ComposeCalendarView
import com.example.ui.components.LessonNotificationSchedulerComponent
import com.example.ui.components.SectionHeader
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDChip
import com.example.ui.components.TimeSlotConfirmationModal
import com.example.ui.theme.*
import com.example.viewmodel.BookingViewModel
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    bookingViewModel: BookingViewModel,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val uiState by bookingViewModel.uiState.collectAsState()
    val savedBookings by bookingViewModel.savedBookings.collectAsState()
    val courses by mainViewModel.courses.collectAsState()
    val instructors by mainViewModel.instructors.collectAsState()

    var couponInput by remember { mutableStateOf("") }
    var couponSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showReceiptDialog by remember { mutableStateOf(false) }
    var showSlotConfirmationModal by remember { mutableStateOf(false) }

    val timeSlots = listOf(
        "07:00 AM - 08:00 AM",
        "08:00 AM - 09:00 AM",
        "09:00 AM - 10:00 AM",
        "10:00 AM - 11:00 AM",
        "04:00 PM - 05:00 PM",
        "05:00 PM - 06:00 PM",
        "06:00 PM - 07:00 PM"
    )

    val paymentModes = listOf(
        "UPI (Google Pay / PhonePe / Paytm)",
        "Credit / Debit Card",
        "Net Banking",
        "Pay at Doorstep (Cash/UPI)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("booking_screen_column"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            SectionHeader(
                title = "Book Your Driving Lesson",
                subtitle = "Step-by-step easy registration with doorstep pickup"
            )

            // Step Indicator Indicator Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val stepTitles = listOf("Details", "Course", "Slot", "Address", "Summary")
                stepTitles.forEachIndexed { idx, title ->
                    val stepNum = idx + 1
                    val isDone = uiState.currentStep > stepNum
                    val isCurrent = uiState.currentStep == stepNum

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> BrightGold
                                        isDone -> SuccessGreen
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text(
                                    text = "$stepNum",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isCurrent) DeepNavy else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) BrightGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Live Total Price Banner in INR (₹)
        if (uiState.selectedCourse != null) {
            item {
                val selectedCourse = uiState.selectedCourse!!
                val basePrice = selectedCourse.priceInr
                val discount = uiState.discountAmountInr
                val finalPrice = (basePrice - discount).coerceAtLeast(0.0)

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("live_price_banner_inr"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Selected Course Package", fontSize = 11.sp, color = TextMuted)
                            Text(selectedCourse.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Fee (₹ INR)", fontSize = 10.sp, color = BrightGold)
                            Text("₹${finalPrice.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = BrightGold)
                        }
                    }
                }
            }
        }

        // Error message card if present
        if (uiState.errorMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.errorMessage ?: "", fontSize = 12.sp, color = Color(0xFF991B1B), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // STEP 1: STUDENT DETAILS
        if (uiState.currentStep == 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Step 1: Student Information", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)

                        OutlinedTextField(
                            value = uiState.studentName,
                            onValueChange = { bookingViewModel.setStudentName(it) },
                            label = { Text("Full Name *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_student_name"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.mobileNumber,
                            onValueChange = { bookingViewModel.setMobileNumber(it) },
                            label = { Text("Mobile Number (WhatsApp) *") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("input_mobile_number"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { bookingViewModel.setEmail(it) },
                            label = { Text("Email Address *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth().testTag("input_email"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // STEP 2: COURSE SELECTION
        if (uiState.currentStep == 2) {
            item {
                Text("Step 2: Select Driving Package", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            items(courses) { course ->
                val isSelected = uiState.selectedCourse?.id == course.id
                Surface(
                    onClick = { bookingViewModel.setSelectedCourse(course) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("course_item_${course.id}"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) BrightGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) BrightGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(course.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("${course.category} • ${course.durationWeeks} Weeks (${course.totalHours} hrs)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("₹${course.priceInr.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = BrightGold)
                    }
                }
            }
        }

        // STEP 3: INSTRUCTOR & TIME SLOT SELECTION
        if (uiState.currentStep == 3) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Step 3: Select Instructor & Time Slot", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)

                    Text("Preferred Instructor:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    LazyColumn(
                        modifier = Modifier.height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(instructors) { inst ->
                            val isSelected = uiState.selectedInstructor?.id == inst.id
                            Surface(
                                onClick = { bookingViewModel.setSelectedInstructor(inst) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("instructor_select_${inst.id}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) BrightGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BrightGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = BrightGold, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("${inst.name} (${inst.gender})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${inst.experienceYears} Yrs Exp • ${inst.vehicleAssigned}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    ComposeCalendarView(
                        selectedDateStr = uiState.preferredDate,
                        onDateSelected = { date -> bookingViewModel.setPreferredDate(date) },
                        selectedTimeSlot = uiState.preferredTimeSlot,
                        onTimeSlotSelected = { slot ->
                            bookingViewModel.setPreferredTimeSlot(slot)
                            showSlotConfirmationModal = true
                        }
                    )
                }
            }
        }

        // STEP 4: DOORSTEP PICKUP ADDRESS
        if (uiState.currentStep == 4) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Step 4: Doorstep Pickup Location", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)

                        OutlinedTextField(
                            value = uiState.pickupAddress,
                            onValueChange = { bookingViewModel.setPickupAddress(it) },
                            label = { Text("Complete Address (House No, Street, Area, City) *") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("input_pickup_address"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = uiState.notes,
                            onValueChange = { bookingViewModel.setNotes(it) },
                            label = { Text("Special Instructions (Landmark, Gate info)") },
                            leadingIcon = { Icon(Icons.Default.Note, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_notes"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // STEP 5: ORDER SUMMARY & PAYMENT (₹ INR)
        if (uiState.currentStep == 5) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Step 5: Order Summary & Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)

                        val course = uiState.selectedCourse
                        val instructor = uiState.selectedInstructor

                        SummaryRow("Student Name:", uiState.studentName)
                        SummaryRow("Mobile:", uiState.mobileNumber)
                        SummaryRow("Course:", course?.title ?: "")
                        SummaryRow("Instructor:", instructor?.name ?: "")
                        SummaryRow("Time Slot:", "${uiState.preferredDate} (${uiState.preferredTimeSlot})")
                        SummaryRow("Pickup Address:", uiState.pickupAddress)

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        // Coupon input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it },
                                label = { Text("Coupon Code (Try DRIVE10)") },
                                modifier = Modifier.weight(1f).testTag("input_coupon_code"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            ThreeDButton(
                                onClick = {
                                    val success = bookingViewModel.applyCoupon(couponInput)
                                    if (success) {
                                        couponSuccessMessage = "Coupon Applied! 10% Discount"
                                    } else {
                                        couponSuccessMessage = "Invalid code. Use DRIVE10"
                                    }
                                },
                                text = "Apply",
                                containerColor = BrightGold,
                                contentColor = DeepNavy,
                                fontSize = 12.sp
                            )
                        }

                        if (couponSuccessMessage != null) {
                            Text(couponSuccessMessage ?: "", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        val basePrice = course?.priceInr ?: 0.0
                        val finalPrice = (basePrice - uiState.discountAmountInr).coerceAtLeast(0.0)

                        SummaryRow("Base Course Fee:", "₹${basePrice.toInt()}")
                        if (uiState.discountAmountInr > 0) {
                            SummaryRow("Discount Applied:", "-₹${uiState.discountAmountInr.toInt()}", valueColor = SuccessGreen)
                        }
                        SummaryRow("Doorstep Pickup Fee:", "FREE", valueColor = SuccessGreen)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable (₹ INR):", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("₹${finalPrice.toInt()}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = BrightGold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Select Payment Mode:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        paymentModes.forEach { mode ->
                            val isSelected = uiState.paymentMode == mode
                            Surface(
                                onClick = { bookingViewModel.setPaymentMode(mode) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) BrightGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = isSelected, onClick = { bookingViewModel.setPaymentMode(mode) })
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(mode, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP NAVIGATION BUTTONS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.currentStep > 1) {
                    ThreeDButton(
                        onClick = { bookingViewModel.previousStep() },
                        text = "Back",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shadowColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_booking_previous"
                    )
                }

                ThreeDButton(
                    onClick = {
                        if (uiState.currentStep < 5) {
                            bookingViewModel.nextStep()
                        } else {
                            bookingViewModel.submitBooking { confirmed ->
                                LessonNotificationScheduler.scheduleLessonReminder(context, confirmed, 60)
                                showReceiptDialog = true
                            }
                        }
                    },
                    text = if (uiState.currentStep < 5) "Continue" else "Confirm & Pay ₹",
                    containerColor = BrightGold,
                    contentColor = DeepNavy,
                    modifier = Modifier.weight(1f),
                    testTag = "btn_booking_next_submit"
                )
            }
        }

        // LESSON NOTIFICATION SCHEDULER COMPONENT
        item {
            Spacer(modifier = Modifier.height(8.dp))
            LessonNotificationSchedulerComponent(bookings = savedBookings)
        }

        // SAVED LOCAL DATABASE RESERVATIONS SECTION
        if (savedBookings.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("saved_database_reservations_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = BrightGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saved Local DB Reservations", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = SuccessGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${savedBookings.size} Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        savedBookings.take(3).forEach { reservation ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = reservation.studentName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "₹${reservation.priceInr.toInt()}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = BrightGold
                                        )
                                    }
                                    Text(
                                        text = "${reservation.courseTitle} • ${reservation.instructorName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Event, contentDescription = null, tint = SapphireBlue, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "${reservation.preferredDate} (${reservation.preferredTimeSlot})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SapphireBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // CONFIRMATION RECEIPT MODAL DIALOG
    if (showReceiptDialog && uiState.confirmedBooking != null) {
        val booking = uiState.confirmedBooking!!
        AlertDialog(
            onDismissRequest = {
                showReceiptDialog = false
                bookingViewModel.resetForm()
            },
            containerColor = DeepNavy,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Booking Confirmed!", color = Color.White, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Booking Pass ID: #DD-${booking.id + 1000}", fontWeight = FontWeight.Bold, color = BrightGold, fontSize = 16.sp)
                    Text("Student: ${booking.studentName}", color = TextLight, fontSize = 13.sp)
                    Text("Course: ${booking.courseTitle}", color = TextLight, fontSize = 13.sp)
                    Text("Instructor: ${booking.instructorName}", color = TextLight, fontSize = 13.sp)
                    Text("Time Slot: ${booking.preferredDate} (${booking.preferredTimeSlot})", color = TextLight, fontSize = 13.sp)
                    Text("Pickup: ${booking.pickupAddress}", color = TextLight, fontSize = 13.sp)
                    Text("Amount Paid: ₹${booking.priceInr.toInt()} (${booking.paymentMode})", color = BrightGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val whatsappUrl = "https://wa.me/918403050225?text=Hi%20D%26D%20Driving%20Center,%20my%20booking%20ID%20is%20%23DD-${booking.id + 1000}%20for%20${booking.studentName}."
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                        context.startActivity(intent)
                        showReceiptDialog = false
                        bookingViewModel.resetForm()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier.testTag("btn_send_whatsapp_receipt")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp Pass", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showReceiptDialog = false
                        bookingViewModel.resetForm()
                    }
                ) {
                    Text("Close Pass", color = TextMuted)
                }
            }
        )
    }

    // SLEEK TIME SLOT CONFIRMATION MODAL
    if (showSlotConfirmationModal) {
        TimeSlotConfirmationModal(
            timeSlot = uiState.preferredTimeSlot,
            dateStr = uiState.preferredDate,
            instructor = uiState.selectedInstructor ?: instructors.firstOrNull(),
            course = uiState.selectedCourse ?: courses.firstOrNull(),
            onConfirm = {
                showSlotConfirmationModal = false
                if (uiState.currentStep == 3) {
                    bookingViewModel.nextStep()
                }
            },
            onDismiss = {
                showSlotConfirmationModal = false
            }
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
