package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CourseEntity
import com.example.data.InstructorEntity
import com.example.ui.theme.*

@Composable
fun TimeSlotConfirmationModal(
    timeSlot: String,
    dateStr: String,
    instructor: InstructorEntity?,
    course: CourseEntity?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val instructorName = instructor?.name ?: "Senior Certified Driving Mentor"
    val instructorInfo = instructor?.let { "${it.experienceYears} Yrs Exp • ${it.vehicleAssigned}" } ?: "Auto-assigned Senior Trainer"
    val courseTitle = course?.title ?: "Practical Driving Course"
    val priceInr = (course?.priceInr ?: 4500.0).toInt()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("timeslot_confirmation_modal"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TOP GLOWING ICON & BADGE
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(ImperialGold.copy(alpha = 0.35f), MidnightNavy)
                            )
                        )
                        .border(1.5.dp, ImperialGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EventAvailable,
                        contentDescription = "Time Slot Confirmed",
                        tint = ImperialGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = ImperialGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "TIME SLOT RESERVED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ImperialGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Confirm Driving Lesson",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    Text(
                        text = "Review your session details before proceeding",
                        fontSize = 11.sp,
                        color = BrightGold
                    )
                }

                // DETAILS SUMMARY CARD
                Surface(
                    color = MidnightNavy,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. DATE & TIME
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ImperialGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Date & Time Slot", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$dateStr • $timeSlot", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.1f))

                        // 2. CHOSEN INSTRUCTOR
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SapphireBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = SapphireBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Chosen Instructor", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(instructorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text(instructorInfo, fontSize = 10.sp, color = BrightGold)
                            }
                        }

                        Divider(color = Color.White.copy(alpha = 0.1f))

                        // 3. COURSE & PRICE IN INR ₹
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Course Fee (₹ INR)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(courseTitle, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.White)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹$priceInr", fontWeight = FontWeight.Black, fontSize = 18.sp, color = ImperialGold)
                                Text("Doorstep Pickup Incl.", fontSize = 9.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // ACTION BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_close_slot_modal"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                    ) {
                        Text("Change", color = Color.White, fontSize = 12.sp)
                    }

                    ThreeDButton(
                        onClick = onConfirm,
                        text = "Confirm Slot",
                        icon = Icons.Default.CheckCircle,
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack,
                        shadowColor = WarmBronze,
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("btn_confirm_slot_modal"),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
