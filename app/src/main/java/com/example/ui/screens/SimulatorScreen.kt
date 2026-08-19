package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Custom3DCarCanvas
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDChip
import com.example.ui.theme.*
import com.example.viewmodel.AdminViewModel
import com.example.viewmodel.BookingViewModel
import com.example.viewmodel.GearState
import com.example.viewmodel.SimulatorViewModel
import com.example.viewmodel.WeatherMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreen(
    simulatorViewModel: SimulatorViewModel,
    bookingViewModel: BookingViewModel,
    adminViewModel: AdminViewModel,
    onNavigateToBooking: () -> Unit = {},
    onOpenAdminPortal: () -> Unit = {}
) {
    val uiState by simulatorViewModel.uiState.collectAsState()
    val isAdmin by adminViewModel.isAuthenticated.collectAsState()
    val savedBookings by bookingViewModel.savedBookings.collectAsState()
    val bookingUiState by bookingViewModel.uiState.collectAsState()

    val hasAppliedCourse = savedBookings.isNotEmpty() || bookingUiState.confirmedBooking != null
    val isAccessGranted = isAdmin || hasAppliedCourse || uiState.isStudentAccessBypassed

    var selectedTab by remember { mutableIntStateOf(0) } // 0: 3D Driving Cockpit, 1: Sign Quiz, 2: RTO Guide

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("simulator_screen_column"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            SectionHeader(
                title = "3D Executive Cockpit Simulator",
                subtitle = "First-person RHD training & RTO traffic evaluation"
            )

            // Simulator Mode Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ImperialGold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("simulator_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("3D Cockpit", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Sign Quiz", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("RTO Guide", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        if (selectedTab == 0) {
            if (!isAccessGranted) {
                // ACCESS CONTROL GUARD CARD
                item {
                    GlassCard(
                        borderColor = ImperialGold,
                        backgroundColor = DarkSurface,
                        cornerRadius = 20.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(ImperialGold.copy(alpha = 0.15f))
                                    .border(1.dp, ImperialGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Access Restricted",
                                    tint = ImperialGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "RESTRICTED SIMULATOR ACCESS",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.8.sp,
                                color = ImperialGold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "The 3D First-Person Cockpit Simulator is reserved exclusively for Enrolled Driving Students with an active course booking or D&D Admin Personnel.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Action 1: Enrol / Book Course
                            ThreeDButton(
                                onClick = onNavigateToBooking,
                                text = "ENROL IN DRIVING COURSE NOW",
                                icon = Icons.Default.AppRegistration,
                                containerColor = ImperialGold,
                                shadowColor = WarmBronze,
                                contentColor = ObsidianBlack,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "btn_guard_enrol"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action 2: Admin Login
                            OutlinedButton(
                                onClick = onOpenAdminPortal,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold)
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Admin Officer Login", color = ImperialGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action 3: Demo Bypass
                            TextButton(
                                onClick = { simulatorViewModel.bypassStudentAccess() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unlock Student Access Demo Key", color = ChampagneGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // 1. FIRST-PERSON RHD CAR COCKPIT CANVAS (GAME MODE)
                item {
                    Custom3DCarCanvas(
                        speedKmH = uiState.speedKmH,
                        steeringAngleDeg = uiState.steeringAngleDeg,
                        gear = uiState.gear.name,
                        isEngineOn = uiState.isEngineOn,
                        isSeatbeltFastened = uiState.isSeatbeltFastened,
                        isHandbrakeEngaged = uiState.isHandbrakeEngaged,
                        isClutchPressed = uiState.isClutchPressed,
                        isHazardLightOn = uiState.isHazardLightOn,
                        isWiperOn = uiState.isWiperOn,
                        isHornActive = uiState.isHornActive,
                        isLeftSignalOn = uiState.isLeftSignalOn,
                        isRightSignalOn = uiState.isRightSignalOn,
                        isHeadlightsOn = uiState.isHeadlightsOn,
                        trafficLightState = uiState.trafficLight.name,
                        weatherMode = uiState.weather.name,
                        ruleViolationAlert = uiState.ruleViolationAlert,
                        isNitroActive = uiState.isNitroActive,
                        nitroChargePercent = uiState.nitroChargePercent,
                        gameXpScore = uiState.gameXpScore,
                        comboMultiplier = uiState.comboMultiplier,
                        currentMissionText = uiState.currentMissionText,
                        floatXpPopup = uiState.floatXpPopup,
                        isTiltSteeringEnabled = uiState.isTiltSteeringEnabled,
                        parkingProximityDistanceMeters = uiState.parkingProximityDistanceMeters,
                        isParkingSuccess = uiState.isParkingSuccess,
                        onSteerAngleChanged = { simulatorViewModel.updateSteering(it) },
                        onSteerLeft = { simulatorViewModel.updateSteering(uiState.steeringAngleDeg - 20f) },
                        onSteerRight = { simulatorViewModel.updateSteering(uiState.steeringAngleDeg + 20f) },
                        onToggleTiltSteering = { simulatorViewModel.toggleTiltSteering() },
                        onToggleEngine = { simulatorViewModel.toggleEngine() },
                        onToggleSeatbelt = { simulatorViewModel.toggleSeatbelt() },
                        onToggleHandbrake = { simulatorViewModel.toggleHandbrake() },
                        onPressClutch = { simulatorViewModel.setClutchPressed(it) },
                        onToggleHazard = { simulatorViewModel.toggleHazard() },
                        onToggleWiper = { simulatorViewModel.toggleWiper() },
                        onPressHorn = { simulatorViewModel.pressHorn() },
                        onToggleLeftSignal = { simulatorViewModel.toggleLeftSignal() },
                        onToggleRightSignal = { simulatorViewModel.toggleRightSignal() },
                        onToggleHeadlights = { simulatorViewModel.toggleHeadlights() },
                        onAccelerate = { simulatorViewModel.accelerate() },
                        onBrake = { simulatorViewModel.applyBrake() },
                        onTriggerNitro = { simulatorViewModel.triggerNitro() }
                    )
                }

                // 2. TRANSMISSION & ATMOSPHERE SELECTION
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Transmission Gear Selector (RHD Console):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GearState.values().forEach { g ->
                                    val isSelected = uiState.gear == g
                                    ThreeDChip(
                                        selected = isSelected,
                                        onClick = { simulatorViewModel.setGear(g) },
                                        label = g.name.take(1) + " (${g.name})",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Simulator Atmosphere & Weather:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                WeatherMode.values().forEach { mode ->
                                    val isSelected = uiState.weather == mode
                                    ThreeDChip(
                                        selected = isSelected,
                                        onClick = { simulatorViewModel.setWeather(mode) },
                                        label = mode.name,
                                        icon = when (mode) {
                                            WeatherMode.CLEAR -> Icons.Default.WbSunny
                                            WeatherMode.RAIN -> Icons.Default.WaterDrop
                                            WeatherMode.FOG -> Icons.Default.Cloud
                                            WeatherMode.NIGHT -> Icons.Default.DarkMode
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. REVERSE PARKING CHALLENGE INTERACTION CARD
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalParking, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Reverse Parallel Parking Radar Challenge", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Target Obstacle Distance: 0.30m - 0.50m", fontSize = 10.sp, color = ImperialGold)
                                    }
                                }
                                Switch(
                                    checked = uiState.isParkingChallengeActive,
                                    onCheckedChange = { simulatorViewModel.toggleParkingChallenge() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ObsidianBlack,
                                        checkedTrackColor = ImperialGold
                                    )
                                )
                            }

                            if (uiState.isParkingChallengeActive) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = MidnightNavy,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Ultrasonic Distance", fontSize = 10.sp, color = ChampagneGold)
                                            Text(
                                                text = "${String.format("%.2f", uiState.parkingProximityDistanceMeters)} m",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp,
                                                color = if (uiState.parkingProximityDistanceMeters < 0.4f) Color.Red else if (uiState.parkingProximityDistanceMeters < 0.8f) BrightGold else SuccessGreen
                                            )
                                        }
                                        Surface(
                                            color = if (uiState.isParkingSuccess) SuccessGreen.copy(alpha = 0.2f) else ImperialGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (uiState.isParkingSuccess) SuccessGreen else ImperialGold)
                                        ) {
                                            Text(
                                                text = if (uiState.isParkingSuccess) "🏆 PARKING CERTIFIED!" else "REVERSE INTO BOX",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = if (uiState.isParkingSuccess) SuccessGreen else ImperialGold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. STATS & PARKING ACCURACY GAUGE
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Distance Covered", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${String.format("%.1f", uiState.distanceCoveredMeters)} m", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ImperialGold)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Parking Test Score", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${uiState.parkingAccuracyPercent}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }
                    }
                }
            }
        } else if (selectedTab == 1) {
            // TRAFFIC SIGN QUIZ MODE
            item {
                val currentSign = simulatorViewModel.trafficSignsList[uiState.currentSignIndex]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${uiState.currentSignIndex + 1} of ${simulatorViewModel.trafficSignsList.size}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                color = ImperialGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Score: ${uiState.quizScore} pts",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ImperialGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MidnightNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = currentSign.title,
                                    tint = ImperialGold,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentSign.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "What does this mandatory RTO road sign indicate?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        currentSign.options.forEachIndexed { idx, option ->
                            val isSelected = uiState.quizSelectedOption == idx
                            val isCorrect = idx == currentSign.correctAnswerIndex

                            val buttonBg = when {
                                !uiState.quizAnswered -> MaterialTheme.colorScheme.surfaceVariant
                                isCorrect -> EmeraldGreen
                                isSelected -> Color.Red
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            Surface(
                                onClick = { simulatorViewModel.answerQuizOption(idx) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("quiz_option_$idx"),
                                shape = RoundedCornerShape(12.dp),
                                color = buttonBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${('A' + idx)}. $option",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (uiState.quizAnswered && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        if (uiState.quizAnswered) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { simulatorViewModel.nextQuizSign() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("btn_next_quiz_question"),
                                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Next Road Sign Question", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else if (selectedTab == 2) {
            // RTO DRIVING TEST PREPARATION GUIDE
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Official Indian RTO Driving Test Blueprint",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mandatory Indian Motor Vehicles Act evaluation criteria used by Motor Vehicle Inspectors (MVI) during practical driving tests.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        listOf(
                            Triple("Keep Left Traffic Rule", "Indian roads require vehicles to drive on the left lane and give way to traffic from the right at roundabouts.", Icons.Default.TurnLeft),
                            Triple("Parallel Parking Track", "Reverse at 45° angle without hitting outer boundaries or kerbs within 3 mins.", Icons.Default.LocalParking),
                            Triple("8-Shape Track Test", "Navigate the figure-8 loop without stopping or dabbing foot. Proper turn signal usage is mandatory.", Icons.Default.Loop),
                            Triple("Gradient Ramp Stop & Start", "Stop on 15° incline ramp. Move forward without rolling backward by more than 2 inches.", Icons.Default.FilterHdr)
                        ).forEach { (title, desc, icon) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(ImperialGold.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = ImperialGold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
