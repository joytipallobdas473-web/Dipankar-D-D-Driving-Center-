package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

data class DrivingSkillItem(
    val id: Int,
    val skillName: String,
    val category: String,
    val ratingStars: Float,
    val isMastered: Boolean,
    val feedback: String,
    val icon: String
)

@Composable
fun StudentSkillScorecardComponent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCertificateDialog by remember { mutableStateOf(false) }
    var studentNameInput by remember { mutableStateOf("Rahul Sharma") }

    val skillList = remember {
        mutableStateListOf(
            DrivingSkillItem(1, "Clutch Precision & Biting Point", "Pedal Mastery", 4.8f, true, "Smooth gear shifts without engine stalls", "⚙️"),
            DrivingSkillItem(2, "Parallel & Reverse Box Parking", "Parking Control", 4.2f, false, "Good alignment; practice tight curb angles", "🅿️"),
            DrivingSkillItem(3, "Hill Assist & Incline Hold", "Specialized Maneuver", 4.6f, true, "Zero rollback on steep incline starts", "⛰️"),
            DrivingSkillItem(4, "City Traffic & Lane Merging", "Road Safety", 4.9f, true, "Excellent mirror check & blind spot awareness", "🏙️"),
            DrivingSkillItem(5, "Night Driving & High-Beam Control", "Visibility Skills", 4.0f, false, "Requires 1 more night session practice", "🌙"),
            DrivingSkillItem(6, "Defensive Braking & Hazard Response", "Safety Protocols", 4.7f, true, "Maintains safe following gap reliably", "🛡️")
        )
    }

    val totalMastered = skillList.count { it.isMastered }
    val avgScore = (skillList.map { it.ratingStars }.average() * 20).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_skill_scorecard_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // HEADER
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
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Student Driving Skill Scorecard",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Instructor Verified Competency Audit",
                            fontSize = 10.sp,
                            color = ImperialGold
                        )
                    }
                }

                Surface(
                    color = SuccessGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                ) {
                    Text(
                        text = "$avgScore% Ready for RTO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SuccessGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // OVERALL PROGRESS METER
            Surface(
                color = MidnightNavy,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Modules Mastered", fontSize = 10.sp, color = BrightGold)
                        Text("$totalMastered of ${skillList.size} Core Skills Completed", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { totalMastered.toFloat() / skillList.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ImperialGold,
                            trackColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SKILLS LIST
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skillList.forEachIndexed { index, skill ->
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (skill.isMastered) SuccessGreen.copy(alpha = 0.5f) else BrightGold.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
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
                                Text(skill.icon, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(skill.skillName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("⭐ ${skill.ratingStars}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ImperialGold)
                                    }
                                    Text(skill.feedback, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            FilterChip(
                                selected = skill.isMastered,
                                onClick = {
                                    skillList[index] = skill.copy(isMastered = !skill.isMastered)
                                },
                                label = {
                                    Text(
                                        text = if (skill.isMastered) "Mastered" else "In Progress",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SuccessGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CLAIM CERTIFICATE BUTTON
            ThreeDButton(
                onClick = { showCertificateDialog = true },
                text = "🎓 View Official Course Certificate",
                icon = Icons.Default.WorkspacePremium,
                containerColor = ImperialGold,
                contentColor = ObsidianBlack,
                shadowColor = WarmBronze,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_claim_certificate"
            )
        }
    }

    // OFFICIAL CERTIFICATE DIALOG
    if (showCertificateDialog) {
        Dialog(onDismissRequest = { showCertificateDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F1522),
                border = androidx.compose.foundation.BorderStroke(2.dp, ImperialGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Certificate Border Graphic Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📜 ISO 9001:2015", fontSize = 10.sp, color = ImperialGold, fontWeight = FontWeight.Bold)
                        Text("CERT-DD-${(1000..9999).random()}", fontSize = 10.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "D&D DRIVING CENTER",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp,
                        color = ImperialGold
                    )
                    Text(
                        text = "EXECUTIVE MOTORING ACADEMY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "CERTIFICATE OF DRIVING COMPETENCY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = BrightGold
                    )

                    Text(
                        text = "This is to officially certify that",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    // Student Name Box
                    OutlinedTextField(
                        value = studentNameInput,
                        onValueChange = { studentNameInput = it },
                        label = { Text("Candidate Name", fontSize = 10.sp) },
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ImperialGold
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImperialGold,
                            unfocusedBorderColor = Color.Gray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Text(
                        text = "has successfully mastered the executive manual & automatic dual-control curriculum with a competency evaluation score of $avgScore%.",
                        fontSize = 11.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Signatures and QR Code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MidnightNavy, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("✍️ Deepak Roy", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ImperialGold)
                            Text("Chief Master Instructor", fontSize = 9.sp, color = Color.Gray)
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏁", fontSize = 20.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("🛡️ Verified", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SuccessGreen)
                            Text("Govt. RTO Compliant", fontSize = 9.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCertificateDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Close", color = Color.White, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "D&D Driving Center Certificate")
                                    putExtra(Intent.EXTRA_TEXT, "🎓 Proud to announce that $studentNameInput has completed the professional driving curriculum at D&D Driving Center with a score of $avgScore%!")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Certificate"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share 📤", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
