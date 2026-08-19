package com.example.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Custom3DCarCanvas(
    modifier: Modifier = Modifier,
    speedKmH: Float = 0f,
    steeringAngleDeg: Float = 0f,
    gear: String = "PARK",
    isEngineOn: Boolean = false,
    isSeatbeltFastened: Boolean = false,
    isHandbrakeEngaged: Boolean = true,
    isClutchPressed: Boolean = false,
    isHazardLightOn: Boolean = false,
    isWiperOn: Boolean = false,
    isHornActive: Boolean = false,
    isLeftSignalOn: Boolean = false,
    isRightSignalOn: Boolean = false,
    isHeadlightsOn: Boolean = true,
    trafficLightState: String = "GREEN",
    weatherMode: String = "CLEAR",
    ruleViolationAlert: String? = null,
    isNitroActive: Boolean = false,
    nitroChargePercent: Float = 100f,
    gameXpScore: Int = 1250,
    comboMultiplier: Int = 1,
    currentMissionText: String = "🏆 MISSION: Drive 100m smoothly on Indian Left Lane",
    floatXpPopup: String? = null,
    isTiltSteeringEnabled: Boolean = false,
    parkingProximityDistanceMeters: Float = 1.8f,
    isParkingSuccess: Boolean = false,
    onSteerAngleChanged: (Float) -> Unit = {},
    onSteerLeft: () -> Unit = {},
    onSteerRight: () -> Unit = {},
    onToggleTiltSteering: () -> Unit = {},
    onToggleEngine: () -> Unit = {},
    onToggleSeatbelt: () -> Unit = {},
    onToggleHandbrake: () -> Unit = {},
    onPressClutch: (Boolean) -> Unit = {},
    onToggleHazard: () -> Unit = {},
    onToggleWiper: () -> Unit = {},
    onPressHorn: () -> Unit = {},
    onToggleLeftSignal: () -> Unit = {},
    onToggleRightSignal: () -> Unit = {},
    onToggleHeadlights: () -> Unit = {},
    onAccelerate: () -> Unit = {},
    onBrake: () -> Unit = {},
    onTriggerNitro: () -> Unit = {}
) {
    val context = LocalContext.current

    // SENSOR LISTENER FOR GYROSCOPE / ACCELEROMETER TILT STEERING
    DisposableEffect(isTiltSteeringEnabled) {
        if (!isTiltSteeringEnabled) {
            onDispose { }
        } else {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        val tiltX = event.values[0] // Tilt left/right
                        val targetAngle = (-tiltX * 18f).coerceIn(-120f, 120f)
                        onSteerAngleChanged(targetAngle)
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            if (sensorManager != null && accelerometer != null) {
                sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            }

            onDispose {
                sensorManager?.unregisterListener(listener)
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "First Person Road Animation")

    // Animate road stripes moving down screen when speed > 0
    val animatedRoadOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (100000f / speedKmH.coerceAtLeast(10f)).toInt().coerceIn(150, 2500),
                easing = LinearEasing
            )
        ),
        label = "roadStripeOffset"
    )

    val currentRoadOffset = if (isEngineOn && !isHandbrakeEngaged && speedKmH > 0) animatedRoadOffset else 0f

    // Signal Flasher animation
    val flasherAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flasherAlpha"
    )

    // Wiper blade sweep animation
    val wiperSweepDeg by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wiperSweep"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cockpit_canvas_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianBlack),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            // 1. FIRST-PERSON DRIVER COCKPIT VIEWPORT (Canvas)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val horizonY = height * 0.40f

                    val isNightMode = weatherMode == "NIGHT" || weatherMode == "RAIN"

                    // A) SKY & CITY ENVIRONMENT VIEW
                    val skyBrush = Brush.verticalGradient(
                        colors = if (isNightMode) listOf(
                            Color(0xFF030712),
                            Color(0xFF0B1329),
                            Color(0xFF162544)
                        ) else if (weatherMode == "FOG") listOf(
                            Color(0xFF64748B),
                            Color(0xFF94A3B8),
                            Color(0xFFCBD5E1)
                        ) else listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF3B82F6),
                            Color(0xFF93C5FD)
                        ),
                        startY = 0f,
                        endY = horizonY
                    )
                    drawRect(brush = skyBrush, size = Size(width, horizonY))

                    // Stars at Night
                    if (isNightMode) {
                        for (s in 0 until 20) {
                            val sx = (s * 37f) % width
                            val sy = (s * 19f) % (horizonY * 0.7f)
                            drawCircle(Color.White.copy(alpha = 0.8f), radius = 1.2f, center = Offset(sx, sy))
                        }
                    }

                    // City skyline & street scenery
                    drawCitySkyline(horizonY, width, isNightMode)

                    // B) 3D PERSPECTIVE INDIAN ROAD (KEEP LEFT TRAFFIC FLOW)
                    val vanishingX = width * 0.5f + (steeringAngleDeg * 0.4f)
                    val roadBottomLeft = Offset(-width * 0.1f, height * 0.82f)
                    val roadBottomRight = Offset(width * 1.1f, height * 0.82f)
                    val roadTopLeft = Offset(vanishingX - 35f, horizonY)
                    val roadTopRight = Offset(vanishingX + 35f, horizonY)

                    val roadPath = Path().apply {
                        moveTo(roadTopLeft.x, roadTopLeft.y)
                        lineTo(roadTopRight.x, roadTopRight.y)
                        lineTo(roadBottomRight.x, roadBottomRight.y)
                        lineTo(roadBottomLeft.x, roadBottomLeft.y)
                        close()
                    }

                    drawPath(
                        path = roadPath,
                        brush = Brush.verticalGradient(
                            colors = if (isNightMode) listOf(Color(0xFF0F172A), Color(0xFF020617)) else listOf(Color(0xFF1B263B), Color(0xFF0E1626)),
                            startY = horizonY,
                            endY = height * 0.82f
                        )
                    )

                    // Yellow/Gold road shoulders & cat eyes
                    drawLine(BrightGold, roadTopLeft, roadBottomLeft, strokeWidth = 5f)
                    drawLine(BrightGold, roadTopRight, roadBottomRight, strokeWidth = 5f)

                    // Headlight Projection Cone on asphalt
                    if (isHeadlightsOn) {
                        val beamPath = Path().apply {
                            moveTo(width * 0.35f, height * 0.75f)
                            lineTo(vanishingX - 25f, horizonY + 20f)
                            lineTo(vanishingX + 25f, horizonY + 20f)
                            lineTo(width * 0.95f, height * 0.75f)
                            close()
                        }
                        drawPath(
                            path = beamPath,
                            brush = Brush.radialGradient(
                                colors = listOf(BrightGold.copy(alpha = 0.28f), Color.Transparent),
                                center = Offset(vanishingX, horizonY + 60f),
                                radius = width * 0.6f
                            )
                        )
                    }

                    // Center Lane Divider Stripes (Moving)
                    val totalStripes = 6
                    for (i in 0 until totalStripes) {
                        val progress = ((i * (100f / totalStripes) + currentRoadOffset) % 100f) / 100f
                        val py = horizonY + (height * 0.82f - horizonY) * progress
                        val px = vanishingX

                        val stripeW = 4f + progress * 12f
                        val stripeH = 5f + progress * 22f

                        drawRect(
                            color = Color.White.copy(alpha = progress.coerceIn(0.2f, 0.95f)),
                            topLeft = Offset(px - stripeW / 2f, py),
                            size = Size(stripeW, stripeH)
                        )

                        // Cat eyes on road
                        if (isNightMode) {
                            drawCircle(
                                color = ImperialGold,
                                radius = 2f + progress * 3f,
                                center = Offset(px - stripeW, py + stripeH / 2f)
                            )
                        }
                    }

                    // REVERSING PARKING GUIDELINE OVERLAY (When gear == REVERSE)
                    if (gear == "REVERSE") {
                        val steerBend = (steeringAngleDeg * 0.5f)
                        val guideLeftStart = Offset(width * 0.2f, height * 0.75f)
                        val guideLeftEnd = Offset(width * 0.35f + steerBend, horizonY + 40f)
                        val guideRightStart = Offset(width * 0.8f, height * 0.75f)
                        val guideRightEnd = Offset(width * 0.65f + steerBend, horizonY + 40f)

                        // Red stop line (closest)
                        drawLine(Color.Red, Offset(width * 0.28f + steerBend * 0.2f, height * 0.72f), Offset(width * 0.72f + steerBend * 0.2f, height * 0.72f), strokeWidth = 5f)
                        // Yellow caution line
                        drawLine(BrightGold, Offset(width * 0.32f + steerBend * 0.5f, height * 0.62f), Offset(width * 0.68f + steerBend * 0.5f, height * 0.62f), strokeWidth = 4f)
                        // Green entry line
                        drawLine(SuccessGreen, Offset(width * 0.35f + steerBend * 0.8f, height * 0.52f), Offset(width * 0.65f + steerBend * 0.8f, height * 0.52f), strokeWidth = 3f)

                        // Side trajectory tracks
                        drawLine(SuccessGreen, guideLeftStart, guideLeftEnd, strokeWidth = 3f)
                        drawLine(SuccessGreen, guideRightStart, guideRightEnd, strokeWidth = 3f)
                    }

                    // ONCOMING 3D PERSPECTIVE TRAFFIC CAR (RIGHT LANE / OPPOSITE TRAFFIC)
                    val carProg = ((currentRoadOffset * 1.5f) % 100f) / 100f
                    val carY = horizonY + (height * 0.82f - horizonY) * carProg
                    val carX = vanishingX + (width * 0.22f * carProg)
                    val carW = 12f + carProg * 38f
                    val carH = 8f + carProg * 26f

                    if (carProg in 0.1f..0.95f) {
                        drawRoundRect(
                            color = Color(0xFFDC2626),
                            topLeft = Offset(carX - carW / 2f, carY),
                            size = Size(carW, carH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                        drawCircle(BrightGold, radius = 2f + carProg * 4f, center = Offset(carX - carW * 0.3f, carY + carH * 0.3f))
                        drawCircle(BrightGold, radius = 2f + carProg * 4f, center = Offset(carX + carW * 0.3f, carY + carH * 0.3f))
                    }

                    // NITRO SPEED WARP LINES & MOTION BLUR FX
                    if (isNitroActive || speedKmH > 60f) {
                        for (n in 0 until 18) {
                            val nx = (n * 57f + currentRoadOffset * 12f) % width
                            val ny = (n * 37f + currentRoadOffset * 18f) % height
                            drawLine(
                                color = if (isNitroActive) Color(0xFF06B6D4).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.4f),
                                start = Offset(nx, ny),
                                end = Offset(nx - (if (isNitroActive) 25f else 10f), ny + (if (isNitroActive) 45f else 20f)),
                                strokeWidth = if (isNitroActive) 3.5f else 1.5f
                            )
                        }
                    }

                    // Weather Rain/Fog
                    if (weatherMode == "RAIN") {
                        for (r in 0 until 35) {
                            val rx = (r * 43f + currentRoadOffset * 6f) % width
                            val ry = (r * 31f + currentRoadOffset * 10f) % (height * 0.8f)
                            drawLine(
                                color = Color(0xFF90E0EF).copy(alpha = 0.7f),
                                start = Offset(rx, ry),
                                end = Offset(rx - 8f, ry + 20f),
                                strokeWidth = 2f
                            )
                        }
                    }

                    // Rearview Mirror (Center Top)
                    val mirrorWidth = 110f
                    val mirrorHeight = 36f
                    val mirrorLeft = width / 2f - mirrorWidth / 2f
                    drawLine(Color.Gray, Offset(width / 2f, 0f), Offset(width / 2f, 15f), strokeWidth = 6f)
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(mirrorLeft - 4f, 13f),
                        size = Size(mirrorWidth + 8f, mirrorHeight + 8f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF132A53), Color(0xFF091322))
                        ),
                        topLeft = Offset(mirrorLeft, 17f),
                        size = Size(mirrorWidth, mirrorHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Side Wing Mirrors
                    drawRoundRect(Color(0xFF1F2937), Offset(4f, height * 0.35f), Size(42f, 65f), CornerRadius(8f, 8f))
                    drawRoundRect(Color(0xFF0F172A), Offset(8f, height * 0.35f + 4f), Size(34f, 57f), CornerRadius(6f, 6f))

                    drawRoundRect(Color(0xFF1F2937), Offset(width - 46f, height * 0.35f), Size(42f, 65f), CornerRadius(8f, 8f))
                    drawRoundRect(Color(0xFF0F172A), Offset(width - 42f, height * 0.35f + 4f), Size(34f, 57f), CornerRadius(6f, 6f))

                    // DASHBOARD & RIGHT-HAND DRIVE (RHD) STEERING WHEEL
                    val dashY = height * 0.58f
                    val dashPath = Path().apply {
                        moveTo(0f, height)
                        lineTo(0f, dashY + 30f)
                        lineTo(width * 0.45f, dashY)
                        lineTo(width * 0.92f, dashY - 10f)
                        lineTo(width, dashY + 20f)
                        lineTo(width, height)
                        close()
                    }

                    drawPath(
                        path = dashPath,
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0B1120)),
                            startY = dashY - 10f,
                            endY = height
                        )
                    )

                    // Dashboard stitched seams
                    drawLine(ImperialGold.copy(alpha = 0.5f), Offset(0f, dashY + 32f), Offset(width * 0.45f, dashY + 2f), strokeWidth = 2f)
                    drawLine(ImperialGold.copy(alpha = 0.5f), Offset(width * 0.45f, dashY + 2f), Offset(width * 0.92f, dashY - 8f), strokeWidth = 2f)

                    // Wiper blades
                    if (isWiperOn) {
                        rotate(degrees = wiperSweepDeg, pivot = Offset(width * 0.3f, height * 0.65f)) {
                            drawLine(Color.Black, Offset(width * 0.3f, height * 0.65f), Offset(width * 0.2f, height * 0.25f), strokeWidth = 5f)
                        }
                        rotate(degrees = wiperSweepDeg, pivot = Offset(width * 0.7f, height * 0.65f)) {
                            drawLine(Color.Black, Offset(width * 0.7f, height * 0.65f), Offset(width * 0.6f, height * 0.25f), strokeWidth = 5f)
                        }
                    }

                    // RHD STEERING WHEEL (Positioned at right side for Indian traffic)
                    val steerCenterX = width * 0.76f
                    val steerCenterY = height * 0.78f
                    val steerRadius = 60f

                    rotate(degrees = steeringAngleDeg, pivot = Offset(steerCenterX, steerCenterY)) {
                        // Outer rim shadow & ring
                        drawCircle(Color.Black.copy(alpha = 0.6f), radius = steerRadius + 6f, center = Offset(steerCenterX, steerCenterY + 4f))
                        drawCircle(
                            brush = Brush.radialGradient(listOf(Color(0xFF334155), Color(0xFF0F172A))),
                            radius = steerRadius,
                            center = Offset(steerCenterX, steerCenterY)
                        )
                        drawCircle(
                            color = ImperialGold.copy(alpha = 0.6f),
                            radius = steerRadius,
                            center = Offset(steerCenterX, steerCenterY),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                        )
                        drawCircle(Color(0xFF0F172A), radius = steerRadius - 16f, center = Offset(steerCenterX, steerCenterY))

                        // Steering Spokes (3-Spoke RHD Design)
                        drawLine(ImperialGold, Offset(steerCenterX, steerCenterY), Offset(steerCenterX - steerRadius + 14f, steerCenterY), strokeWidth = 8f)
                        drawLine(ImperialGold, Offset(steerCenterX, steerCenterY), Offset(steerCenterX + steerRadius - 14f, steerCenterY), strokeWidth = 8f)
                        drawLine(ImperialGold, Offset(steerCenterX, steerCenterY), Offset(steerCenterX, steerCenterY + steerRadius - 14f), strokeWidth = 8f)

                        // Center Horn Button / D&D Logo
                        drawCircle(ImperialGold, radius = 18f, center = Offset(steerCenterX, steerCenterY))
                        drawCircle(ObsidianBlack, radius = 15f, center = Offset(steerCenterX, steerCenterY))
                    }

                    // Instrument Cluster Speedometer Dial (Left of RHD Steering)
                    val clusterX = width * 0.35f
                    val clusterY = height * 0.72f
                    drawRoundRect(
                        color = Color(0xFF030712),
                        topLeft = Offset(clusterX - 55f, clusterY - 32f),
                        size = Size(110f, 64f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    drawRoundRect(
                        color = ImperialGold.copy(alpha = 0.4f),
                        topLeft = Offset(clusterX - 55f, clusterY - 32f),
                        size = Size(110f, 64f),
                        cornerRadius = CornerRadius(10f, 10f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                    )
                }

                // HUD OVERLAY: SPEEDOMETER, GEAR & TRAFFIC LIGHT
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Surface(
                        color = ObsidianBlack.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${speedKmH.toInt()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = ImperialGold
                                )
                                Text("KM / H", fontSize = 8.sp, color = BrightGold, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = gear,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (gear == "PARK") Color.Red else SuccessGreen
                                )
                                Text("GEAR", fontSize = 8.sp, color = Color.LightGray)
                            }
                        }
                    }

                    // PARKING RADAR SONAR GAUGE (When in Reverse)
                    if (gear == "REVERSE") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = ObsidianBlack.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (parkingProximityDistanceMeters < 0.4f) Color.Red else if (parkingProximityDistanceMeters < 0.8f) BrightGold else SuccessGreen
                            )
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Sensors,
                                        contentDescription = null,
                                        tint = if (parkingProximityDistanceMeters < 0.4f) Color.Red else SuccessGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "REAR SONAR: ${String.format("%.2f", parkingProximityDistanceMeters)} m",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                if (isParkingSuccess) {
                                    Text("🎯 PERFECT REVERSE PARK!", color = SuccessGreen, fontWeight = FontWeight.Black, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }

                // TOP RIGHT: XP SCORE & NITRO BAR
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Surface(
                        color = ObsidianBlack.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐ $gameXpScore XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrightGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Nitro Boost Charge Indicator
                    LinearProgressIndicator(
                        progress = { nitroChargePercent / 100f },
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF06B6D4),
                        trackColor = Color.DarkGray
                    )
                }

                // VIOLATION & MISSION BANNER
                if (ruleViolationAlert != null) {
                    Surface(
                        color = ObsidianBlack.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = ruleViolationAlert,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. COCKPIT CONSOLE CONTROLS & PEDALS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Engine Ignition Button
                Button(
                    onClick = onToggleEngine,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEngineOn) SuccessGreen else Color.Red
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_engine_toggle")
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isEngineOn) "ENGINE ON" else "START STOP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Seatbelt Toggle Button
                Button(
                    onClick = onToggleSeatbelt,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSeatbeltFastened) SuccessGreen else DarkSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_seatbelt_toggle")
                ) {
                    Icon(Icons.Default.AirlineSeatReclineExtra, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSeatbeltFastened) "BELT ON" else "FASTEN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Handbrake Button
                Button(
                    onClick = onToggleHandbrake,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHandbrakeEngaged) Color.Red else DarkSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_handbrake_toggle")
                ) {
                    Icon(Icons.Default.PanTool, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHandbrakeEngaged) "BRAKE (P)" else "RELEASE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. PEDALS & NITRO ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Clutch Pedal
                PedalButton(
                    label = "CLUTCH",
                    color = BrightGold,
                    modifier = Modifier.weight(1f),
                    onPressState = onPressClutch,
                    testTag = "pedal_clutch"
                )

                // Brake Pedal
                PedalButton(
                    label = "BRAKE",
                    color = Color.Red,
                    modifier = Modifier.weight(1f),
                    onClick = onBrake,
                    testTag = "pedal_brake"
                )

                // Accelerator Pedal
                PedalButton(
                    label = "ACCEL",
                    color = SuccessGreen,
                    modifier = Modifier.weight(1.2f),
                    onClick = onAccelerate,
                    testTag = "pedal_accelerate"
                )

                // Nitro Trigger
                Button(
                    onClick = onTriggerNitro,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("NITRO", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. SECONDARY ACCESSORIES & SIGNALS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Tilt Sensor Toggle
                    Button(
                        onClick = onToggleTiltSteering,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTiltSteeringEnabled) SuccessGreen else DarkSurfaceVariant,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.ScreenRotation, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTiltSteeringEnabled) "📱 Tilt ON" else "📱 Tilt OFF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = onToggleHeadlights,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isHeadlightsOn) ImperialGold else DarkSurfaceVariant)
                    ) {
                        Icon(Icons.Default.Highlight, contentDescription = "Lights", tint = if (isHeadlightsOn) ObsidianBlack else Color.White, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onToggleWiper,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isWiperOn) ImperialGold else DarkSurfaceVariant)
                    ) {
                        Icon(Icons.Default.InvertColors, contentDescription = "Wiper", tint = if (isWiperOn) ObsidianBlack else Color.White, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onPressHorn,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isHornActive) ImperialGold else DarkSurfaceVariant)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Horn", tint = if (isHornActive) ObsidianBlack else Color.White, modifier = Modifier.size(14.dp))
                    }
                }

                // Left/Hazard/Right Signals
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onToggleLeftSignal,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isLeftSignalOn) ImperialGold else DarkSurfaceVariant)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left", tint = if (isLeftSignalOn) ObsidianBlack else Color.White, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onToggleHazard,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isHazardLightOn) Color.Red else DarkSurfaceVariant)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Hazard", tint = Color.White, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onToggleRightSignal,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isRightSignalOn) ImperialGold else DarkSurfaceVariant)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right", tint = if (isRightSignalOn) ObsidianBlack else Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. TOUCH STEERING BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onSteerLeft,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Steer Left", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onSteerRight,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Steer Right", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }

                Surface(
                    color = ImperialGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "🇮🇳 RHD • Keep Left Active",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImperialGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PedalButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onPressState: ((Boolean) -> Unit)? = null,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        onPressState?.invoke(isPressed)
    }

    Surface(
        onClick = { onClick?.invoke() },
        interactionSource = interactionSource,
        modifier = modifier
            .height(48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = if (isPressed) color.copy(alpha = 0.8f) else DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isPressed) Color.White else color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = if (isPressed) Color.White else color
            )
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(3.dp)
                            .background(color.copy(alpha = 0.5f), RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCitySkyline(horizonY: Float, width: Float, isNight: Boolean) {
    val buildings = listOf(
        0f to 28f, 35f to 45f, 75f to 30f, 115f to 55f,
        160f to 40f, 210f to 65f, 260f to 38f, 310f to 58f,
        360f to 45f, 420f to 28f, 480f to 52f, 550f to 40f
    )

    buildings.forEach { (xOffset, bHeight) ->
        val bWidth = 32f
        val bTop = horizonY - bHeight
        drawRect(
            color = if (isNight) Color(0xFF0B1324) else Color(0xFF1E293B),
            topLeft = Offset(xOffset, bTop),
            size = Size(bWidth, bHeight)
        )
        if (isNight) {
            for (wy in (bTop.toInt() + 5)..(horizonY.toInt() - 5) step 10) {
                drawCircle(
                    color = BrightGold.copy(alpha = 0.5f),
                    radius = 1.5f,
                    center = Offset(xOffset + 10f, wy.toFloat())
                )
                drawCircle(
                    color = BrightGold.copy(alpha = 0.4f),
                    radius = 1.5f,
                    center = Offset(xOffset + 22f, wy.toFloat())
                )
            }
        }
    }
}
