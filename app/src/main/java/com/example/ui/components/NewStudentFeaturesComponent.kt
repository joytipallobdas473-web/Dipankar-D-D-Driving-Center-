package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// ==========================================
// 1. WEEKLY STUDENT LEADERBOARD & BADGES
// ==========================================

data class LeaderboardStudent(
    val rank: Int,
    val name: String,
    val city: String,
    val xpScore: Int,
    val quizAccuracy: Int,
    val badgeTitle: String,
    val badgeIcon: String,
    val avatarColor: Color
)

data class StudentBadge(
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progress: Float
)

@Composable
fun StudentLeaderboardComponent(
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Leaderboard, 1: My Badges

    val topStudents = listOf(
        LeaderboardStudent(1, "Aarav Sharma", "Pune", 2850, 98, "RTO Champion 🏆", "👑", Color(0xFFEAB308)),
        LeaderboardStudent(2, "Priya Patel", "Kothrud", 2620, 95, "Clutch Master ⚡", "🥇", Color(0xFF3B82F6)),
        LeaderboardStudent(3, "Rohan Kulkarni", "Baner", 2410, 92, "Parking Pro 🅿️", "🥈", Color(0xFF10B981)),
        LeaderboardStudent(4, "Sneha Joshi", "Viman Nagar", 2150, 90, "Night Navigator 🌙", "🥉", Color(0xFFA855F7)),
        LeaderboardStudent(5, "Rahul Deshmukh", "Hadapsar", 1980, 88, "Safety Driver 🛡️", "🚗", Color(0xFFF97316))
    )

    val myBadges = listOf(
        StudentBadge("Clutch King ⚡", "Mastered clutch biting point 10 times in 3D simulator", "⚡", true, 1.0f),
        StudentBadge("RTO Quiz Pro 🛑", "Scored 100% on official RTO mock driving exam", "🛑", true, 1.0f),
        StudentBadge("Parallel Parking Master 🅿️", "Parked in under 20 seconds without cone hits", "🅿️", true, 1.0f),
        StudentBadge("Night Navigator 🌙", "Completed 3 evening driving sessions with instructor", "🌙", false, 0.66f),
        StudentBadge("Speed Limit Guardian 🛡️", "Drove 10km without exceeding 40km/h speed limit", "🛡️", false, 0.40f)
    )

    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆 ", fontSize = 22.sp)
                    Column {
                        Text(
                            "Weekly Driving Leaderboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Rankings reset every Sunday at midnight",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(text = "LIVE RANK #4", containerColor = BrightGold.copy(alpha = 0.2f), contentColor = BrightGold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-Tab Switcher
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrightGold,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Top Learners", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("My Badges (3/5)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (activeTab == 0) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    topStudents.forEach { student ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (student.rank == 4) SapphireBlue.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            border = if (student.rank == 4) androidx.compose.foundation.BorderStroke(1.dp, BrightGold) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = student.avatarColor.copy(alpha = 0.2f),
                                        shape = CircleShape,
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, student.avatarColor)
                                    ) {
                                        Text(
                                            text = "#${student.rank}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = student.avatarColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(student.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            if (student.rank == 4) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("(YOU)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = BrightGold)
                                            }
                                        }
                                        Text("${student.city} • ${student.badgeTitle}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${student.xpScore} XP", fontWeight = FontWeight.Black, fontSize = 13.sp, color = BrightGold)
                                    Text("${student.quizAccuracy}% Quiz", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    myBadges.forEach { badge ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (badge.isUnlocked) EmeraldGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (badge.isUnlocked) EmeraldGreen.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(badge.icon, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(badge.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            if (badge.isUnlocked) "UNLOCKED 🏆" else "${(badge.progress * 100).toInt()}%",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (badge.isUnlocked) EmeraldGreen else Color.Gray
                                        )
                                    }
                                    Text(badge.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (!badge.isUnlocked) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { badge.progress },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                            color = BrightGold,
                                            trackColor = Color.Gray.copy(alpha = 0.3f)
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
}

// ==========================================
// 2. EMI & INSTALLMENT FEE CALCULATOR
// ==========================================

@Composable
fun EmiFeeCalculatorComponent(
    modifier: Modifier = Modifier
) {
    var selectedCourseIndex by remember { mutableStateOf(0) }
    var selectedMonths by remember { mutableStateOf(6) }
    var showPaymentSuccessModal by remember { mutableStateOf(false) }

    val courses = listOf(
        Pair("Car Manual (4-Wheeler)", 7500),
        Pair("Car Automatic SUV", 9500),
        Pair("Bike + Car Combo (2+4)", 11000),
        Pair("RTO License VIP Special", 13500)
    )

    val coursePrice = courses[selectedCourseIndex].second
    val monthlyInstallment = (coursePrice / selectedMonths)
    val downPayment = (coursePrice * 0.2f).toInt()
    val remainingBalance = coursePrice - downPayment
    val emiPerMonth = (remainingBalance / selectedMonths)

    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💳 ", fontSize = 22.sp)
                Column {
                    Text(
                        "0% Interest EMI & Fee Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Pay in flexible monthly installments via UPI / Credit Card",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Select Course
            Text("Select Training Package:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrightGold)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(courses.size) { idx ->
                    val (title, price) = courses[idx]
                    val isSelected = selectedCourseIndex == idx
                    Surface(
                        modifier = Modifier.clickable { selectedCourseIndex = idx },
                        color = if (isSelected) SapphireBlue else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrightGold) else null
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                            Text("₹$price", fontWeight = FontWeight.Black, fontSize = 12.sp, color = BrightGold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Select Tenure (3, 6, 9, 12 Months)
            Text("Choose EMI Duration (0% Processing Fee):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrightGold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(3, 6, 9, 12).forEach { months ->
                    val isSelected = selectedMonths == months
                    Surface(
                        modifier = Modifier.weight(1f).clickable { selectedMonths = months },
                        color = if (isSelected) BrightGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BrightGold else Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$months Mo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSelected) BrightGold else MaterialTheme.colorScheme.onSurface)
                            Text("0% Interest", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown Card
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Package Fee:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹$coursePrice", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Today's Down Payment (20%):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹$downPayment", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldGreen)
                    }
                    Divider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 2.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Monthly Installment ($selectedMonths Months):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrightGold)
                        Text("₹$emiPerMonth / mo", fontWeight = FontWeight.Black, fontSize = 14.sp, color = BrightGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ThreeDButton(
                text = "Pay Down Payment ₹$downPayment via UPI",
                icon = Icons.Default.AccountBalanceWallet,
                containerColor = EmeraldGreen,
                onClick = { showPaymentSuccessModal = true },
                modifier = Modifier.fillMaxWidth().testTag("btn_pay_down_payment")
            )
        }
    }

    if (showPaymentSuccessModal) {
        AlertDialog(
            onDismissRequest = { showPaymentSuccessModal = false },
            containerColor = DeepNavy,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment Receipt Generated!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Transaction ID: #DD-UPI-${(100000..999999).random()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrightGold)
                    Text("Package: ${courses[selectedCourseIndex].first}", fontSize = 12.sp, color = Color.White)
                    Text("Amount Paid: ₹$downPayment (Down Payment)", fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    Text("Balance: ₹$remainingBalance split into $selectedMonths EMIs of ₹$emiPerMonth/month.", fontSize = 11.sp, color = Color.LightGray)
                    Text("✅ Official Digital Fee Receipt emailed to student.", fontSize = 11.sp, color = BrightGold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showPaymentSuccessModal = false }) {
                    Text("Done", color = BrightGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ==========================================
// 3. EMERGENCY ROADSIDE BREAKDOWN ASSISTANT
// ==========================================

data class BreakdownGuide(
    val title: String,
    val icon: String,
    val summary: String,
    val steps: List<String>
)

@Composable
fun RoadsideBreakdownAssistantComponent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedGuideIndex by remember { mutableStateOf<Int?>(null) }

    val guides = listOf(
        BreakdownGuide(
            "Flat Tire Replacement 🛞",
            "🛞",
            "How to safely replace a puncture with the spare tire step-by-step",
            listOf(
                "1. Turn on Hazard Lights and pull onto a flat, solid shoulder.",
                "2. Apply Handbrake firmly and engage 1st Gear (or Park in Auto).",
                "3. Loosen wheel lug nuts slightly with lug wrench BEFORE jacking up.",
                "4. Place Jack under car frame notch and raise vehicle until tire clears ground.",
                "5. Swap flat tire with spare tire, tighten nuts, lower jack and torque nuts firmly."
            )
        ),
        BreakdownGuide(
            "Car Battery Jump-Start 🔋",
            "🔋",
            "Connecting jumper cables safely without sparking",
            listOf(
                "1. Position donor car close without touching, turn off both ignitions.",
                "2. Connect RED cable to POSITIVE (+) terminal of dead battery.",
                "3. Connect other RED clamp to POSITIVE (+) of live donor battery.",
                "4. Connect BLACK cable to NEGATIVE (-) of donor battery.",
                "5. Connect other BLACK clamp to unpainted metal bolt on dead engine block.",
                "6. Start donor car, wait 2 mins, then start dead car."
            )
        ),
        BreakdownGuide(
            "Engine Overheating 🌡️",
            "🌡️",
            "What to do when temperature gauge turns red or steam appears",
            listOf(
                "1. Immediately turn OFF AC and turn ON Cabin Heater full blast to vent heat.",
                "2. Pull over safely, shut off engine, pop hood latch but DO NOT OPEN RADIATOR CAP.",
                "3. Wait at least 25 minutes for engine to cool down completely.",
                "4. Check coolant reservoir level once cool and top up with water/coolant."
            )
        ),
        BreakdownGuide(
            "Brake Failure Protocol 🛑",
            "🛑",
            "Emergency stopping procedure if foot brake pedal loses pressure",
            listOf(
                "1. Pump the brake pedal rapidly multiple times to build hydraulic pressure.",
                "2. Downshift rapidly into lower gears (3rd -> 2nd -> 1st) to use engine braking.",
                "3. Gradually pull Handbrake button/lever up while holding release button.",
                "4. Steer toward soft dirt shoulder or uphill incline if available."
            )
        )
    )

    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🆘 ", fontSize = 22.sp)
                    Column {
                        Text(
                            "Roadside Breakdown Safety Assistant",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "24x7 Emergency Helplines & Troubleshooting Guides",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(text = "24x7 SOS", containerColor = Color.Red.copy(alpha = 0.2f), contentColor = Color.Red)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1-Tap Emergency Hotline Call Cards
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                        context.startActivity(intent)
                    },
                    color = Color.Red.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhoneInTalk, contentDescription = "SOS", tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("D&D SOS Patrol", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Red)
                            Text("+91 98765 43210", fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f).clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                        context.startActivity(intent)
                    },
                    color = BrightGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalPolice, contentDescription = "Police", tint = BrightGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("National Helpline", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrightGold)
                            Text("112 Emergency", fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Breakdown Self-Help Guides:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrightGold)
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                guides.forEachIndexed { idx, guide ->
                    val isExpanded = selectedGuideIndex == idx
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedGuideIndex = if (isExpanded) null else idx
                        },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(guide.icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(guide.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(guide.summary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand",
                                    tint = BrightGold
                                )
                            }

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = Color.Gray.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))
                                guide.steps.forEach { step ->
                                    Text(
                                        text = step,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.padding(vertical = 3.dp)
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
