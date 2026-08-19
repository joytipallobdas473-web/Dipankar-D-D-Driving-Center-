package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CourseEntity
import com.example.data.InstructorEntity
import com.example.data.TestimonialEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.NavigationTab

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onNavigateTo: (NavigationTab) -> Unit,
    onSelectCourseToBook: (CourseEntity) -> Unit
) {
    val context = LocalContext.current
    val courses by mainViewModel.courses.collectAsState()
    val instructors by mainViewModel.instructors.collectAsState()
    val testimonials by mainViewModel.testimonials.collectAsState()

    var activeCarSpeed by remember { mutableFloatStateOf(45f) }
    var activeSteeringAngle by remember { mutableFloatStateOf(0f) }
    var studentToolTab by remember { mutableIntStateOf(0) } // 0: RTO Quiz, 1: Scorecard, 2: GPS Tracker, 3: Wallet

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("home_screen_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // 1. HERO SECTION WITH REALISTIC 3D ANIMATED CAR CANVAS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Headline Badge
                Surface(
                    color = ImperialGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "ISO Certified",
                            tint = ImperialGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ROYAL EXECUTIVE DRIVING ACADEMY • ISO 9001",
                            color = ImperialGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp
                        )
                    }
                }

                Text(
                    text = "Welcome to D&D Driving Center",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 32.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Master the road with supreme confidence. Experience concierge driving instruction with our dual-control luxury SUV fleet and RTO Master Trainers.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                // 3D Car Visual Canvas Component
                Custom3DCarCanvas(
                    speedKmH = activeCarSpeed,
                    steeringAngleDeg = activeSteeringAngle,
                    onSteerLeft = { activeSteeringAngle = (activeSteeringAngle - 25f).coerceAtLeast(-90f) },
                    onSteerRight = { activeSteeringAngle = (activeSteeringAngle + 25f).coerceAtMost(90f) },
                    onAccelerate = { activeCarSpeed = (activeCarSpeed + 15f).coerceAtMost(120f) }
                )

                // Hero Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThreeDButton(
                        onClick = { onNavigateTo(NavigationTab.BOOKING) },
                        text = "Book a Lesson",
                        icon = Icons.Default.Event,
                        containerColor = BrightGold,
                        contentColor = DeepNavy,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_hero_book_lesson"
                    )

                    ThreeDButton(
                        onClick = { onNavigateTo(NavigationTab.SIMULATOR) },
                        text = "3D Simulator",
                        icon = Icons.Default.VideogameAsset,
                        containerColor = DeepNavy,
                        contentColor = BrightGold,
                        shadowColor = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_hero_simulator"
                    )
                }
            }
        }

        // 2. TRUST STATS BAR
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(number = "12.5k+", label = "Licensed Students")
                    VerticalDivider(modifier = Modifier.height(36.dp), color = Color.Gray.copy(alpha = 0.3f))
                    StatItem(number = "99.4%", label = "Pass Rate")
                    VerticalDivider(modifier = Modifier.height(36.dp), color = Color.Gray.copy(alpha = 0.3f))
                    StatItem(number = "15+ Yrs", label = "Excellence")
                }
            }
        }

        // 3. STUDENT TOOLS & SAFETY SUITE
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    title = "Student Success & Driving Tools",
                    subtitle = "RTO Quiz, Skill Scorecard, GPS Pickup Tracker & Document Wallet"
                )

                ScrollableTabRow(
                    selectedTabIndex = studentToolTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = BrightGold,
                    edgePadding = 0.dp,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = studentToolTab == 0,
                        onClick = { studentToolTab = 0 },
                        text = { Text("🛑 RTO Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = studentToolTab == 1,
                        onClick = { studentToolTab = 1 },
                        text = { Text("📊 Scorecard", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = studentToolTab == 2,
                        onClick = { studentToolTab = 2 },
                        text = { Text("🏆 Leaderboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = studentToolTab == 3,
                        onClick = { studentToolTab = 3 },
                        text = { Text("📍 GPS Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = studentToolTab == 4,
                        onClick = { studentToolTab = 4 },
                        text = { Text("📁 Doc Wallet", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = studentToolTab == 5,
                        onClick = { studentToolTab = 5 },
                        text = { Text("💳 EMI Calc", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = studentToolTab == 6,
                        onClick = { studentToolTab = 6 },
                        text = { Text("🆘 SOS Patrol", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                when (studentToolTab) {
                    0 -> RtoQuizComponent()
                    1 -> StudentSkillScorecardComponent()
                    2 -> StudentLeaderboardComponent()
                    3 -> LivePickupGpsTrackerComponent()
                    4 -> DigitalDocumentWalletComponent()
                    5 -> EmiFeeCalculatorComponent()
                    6 -> RoadsideBreakdownAssistantComponent()
                }
            }
        }

        // 4. KEY FEATURES GRID
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    title = "Why Choose D&D Driving Center",
                    subtitle = "Premium facilities designed for your safety & comfort"
                )

                val features = listOf(
                    Triple("Certified Driving Instructors", "RTO certified trainers with 10+ yrs experience.", Icons.Default.WorkspacePremium),
                    Triple("Manual & Automatic Lessons", "Train on dual-control Hatchbacks & SUVs.", Icons.Default.DirectionsCar),
                    Triple("Doorstep Pickup & Drop", "Free pickup from your residence or office.", Icons.Default.HomeWork),
                    Triple("Female Instructors Available", "Dedicated female mentors for comfortable learning.", Icons.Default.Female),
                    Triple("Flexible Schedules", "Early morning, evening & weekend slots.", Icons.Default.AccessTime),
                    Triple("Road Test Guarantee", "Specialized Figure-8 & H-Track preparation.", Icons.Default.AssignmentTurnedIn)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (chunk in features.chunked(2)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (item in chunk) {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(115.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(BrightGold.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(item.third, contentDescription = null, tint = BrightGold, modifier = Modifier.size(18.dp))
                                        }
                                        Text(item.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(item.second, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. FEATURED COURSES SECTION (₹ INR PRICING)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    title = "Featured Driving Packages",
                    subtitle = "All prices in Indian Rupees (₹ INR)",
                    actionText = "View All Courses",
                    onActionClick = { onNavigateTo(NavigationTab.COURSES) }
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(courses) { course ->
                        Card(
                            modifier = Modifier
                                .width(260.dp)
                                .clickable {
                                    onSelectCourseToBook(course)
                                    onNavigateTo(NavigationTab.BOOKING)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (course.isPopular) BrightGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (course.badge.isNotBlank()) {
                                    StatusBadge(text = course.badge)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(
                                    text = course.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${course.category} • ${course.durationWeeks} Weeks (${course.totalHours} Hours)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "₹${course.priceInr.toInt()}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrightGold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "₹${course.originalPriceInr.toInt()}",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                ThreeDButton(
                                    onClick = {
                                        onSelectCourseToBook(course)
                                        onNavigateTo(NavigationTab.BOOKING)
                                    },
                                    text = "Enroll Now",
                                    containerColor = SapphireBlue,
                                    shadowColor = Color(0xFF1E3A8A),
                                    contentColor = Color.White,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. CERTIFIED INSTRUCTORS HIGHLIGHT
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    title = "Certified Instructors",
                    subtitle = "Including dedicated Female Trainers",
                    actionText = "View Team",
                    onActionClick = { onNavigateTo(NavigationTab.INSTRUCTORS) }
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(instructors) { instructor ->
                        Surface(
                            modifier = Modifier
                                .width(220.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(if (instructor.gender == "Female") Color(0xFFEC4899) else SapphireBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = instructor.name.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = instructor.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${instructor.experienceYears} Yrs Exp • ${instructor.gender}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${instructor.rating} (${instructor.reviewCount} Reviews)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = instructor.specialization,
                                    fontSize = 11.sp,
                                    color = BrightGold,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. STUDENT TESTIMONIALS & RTO PASS GUARANTEE BANNER
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SapphireBlue),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = BrightGold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Student Success Stories", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (testimonials.isNotEmpty()) {
                        val t = testimonials.first()
                        Text(
                            text = "\"${t.reviewText}\"",
                            fontSize = 13.sp,
                            color = TextLight,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "— ${t.studentName} (${t.courseTaken})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightGold
                        )
                    }
                }
            }
        }

        // 7. QUICK CONTACT & WHATSAPP ACTION BANNER
        item {
            Surface(
                color = DeepNavy,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Need Assistance?", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        Text("Call or Chat on +91 8403050225 / 9101303239", fontSize = 11.sp, color = BrightGold)
                    }
                    ThreeDButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918403050225?text=Hi%20D%26D%20Driving%20Center,%20I%20want%20to%20inquire%20about%20driving%20lessons."))
                            context.startActivity(intent)
                        },
                        text = "WhatsApp",
                        containerColor = SuccessGreen,
                        shadowColor = Color(0xFF15803D),
                        contentColor = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, fontSize = 18.sp, fontWeight = FontWeight.Black, color = BrightGold)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
