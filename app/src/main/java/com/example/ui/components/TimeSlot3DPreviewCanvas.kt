package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

data class TimeSlot3DData(
    val timeSlot: String,
    val displayLabel: String,
    val badgeStr: String,
    val isMorning: Boolean,
    val trafficCondition: String,
    val slotsRemaining: Int
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun TimeSlot3DPreviewCanvas(
    selectedTimeSlot: String,
    onTimeSlotSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val allSlots = remember {
        listOf(
            TimeSlot3DData("07:00 AM - 08:00 AM", "07:00 AM", "Available", true, "Low Traffic • Calm Road", 5),
            TimeSlot3DData("08:00 AM - 09:00 AM", "08:00 AM", "Popular", true, "Light Morning Traffic", 2),
            TimeSlot3DData("09:00 AM - 10:00 AM", "09:00 AM", "Available", true, "Moderate City Traffic", 4),
            TimeSlot3DData("10:00 AM - 11:00 AM", "10:00 AM", "3 Left", true, "Normal Traffic Practice", 3),
            TimeSlot3DData("04:00 PM - 05:00 PM", "04:00 PM", "Popular", false, "Moderate Evening Flow", 2),
            TimeSlot3DData("05:00 PM - 06:00 PM", "05:00 PM", "Available", false, "Peak Traffic Training", 4),
            TimeSlot3DData("06:00 PM - 07:00 PM", "06:00 PM", "2 Left", false, "Dusk / Night Headlights", 2)
        )
    }

    var selectedIndex by remember(selectedTimeSlot) {
        val idx = allSlots.indexOfFirst { it.timeSlot == selectedTimeSlot }
        mutableIntStateOf(if (idx >= 0) idx else 1)
    }

    var userRotationAngle by remember { mutableFloatStateOf(25f) }
    var isAutoRotate by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "3D Slot Transition")

    // Continuous subtle 3D hovering / rotation animation
    val autoAngle by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "autoAngle"
    )

    val carPulseOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "carPulse"
    )

    val effectiveAngle = if (isAutoRotate) userRotationAngle + autoAngle else userRotationAngle

    val currentData = allSlots[selectedIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("3d_timeslot_preview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // HEADER ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ImperialGold.copy(alpha = 0.2f))
                            .border(1.dp, ImperialGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ViewInAr,
                            contentDescription = "3D Time Slot Visualizer",
                            tint = ImperialGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "3D Interactive Time Slot Stage",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Touch or drag to orbit 3D lesson slots",
                            fontSize = 10.sp,
                            color = ImperialGold
                        )
                    }
                }

                IconButton(
                    onClick = { isAutoRotate = !isAutoRotate },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isAutoRotate) Icons.Default.RotateRight else Icons.Default.Pause,
                        contentDescription = "Toggle 3D Rotation",
                        tint = ImperialGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D CANVAS STAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MidnightNavy,
                                ObsidianBlack,
                                DarkSurface
                            )
                        )
                    )
                    .border(1.dp, ImperialGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            userRotationAngle = (userRotationAngle + dragAmount.x * 0.25f).coerceIn(-60f, 60f)
                        }
                    }
                    .testTag("3d_timeslot_canvas_stage"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // 1. Draw 3D Grid / Platform Base
                    val angleRad = Math.toRadians(effectiveAngle.toDouble()).toFloat()
                    val cosA = cos(angleRad)
                    val sinA = sin(angleRad)

                    val centerX = width / 2f
                    val centerY = height / 2f + 20f

                    // Draw Background Sky Glow (Sun or Night Moon gradient)
                    val isMorning = currentData.isMorning
                    val sunMoonX = centerX + (selectedIndex - 3) * 35f
                    val sunMoonY = 45f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = if (isMorning) {
                                listOf(Color(0xFFFDE047).copy(alpha = 0.6f), Color(0xFFF97316).copy(alpha = 0.2f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF818CF8).copy(alpha = 0.6f), Color(0xFF312E81).copy(alpha = 0.2f), Color.Transparent)
                            },
                            center = Offset(sunMoonX, sunMoonY),
                            radius = 120f
                        ),
                        center = Offset(sunMoonX, sunMoonY),
                        radius = 120f
                    )

                    drawCircle(
                        color = if (isMorning) Color(0xFFFDE047) else Color(0xFFE2E8F0),
                        radius = 18f,
                        center = Offset(sunMoonX, sunMoonY)
                    )

                    // Draw 3D Stage Ellipse Base Grid
                    val baseRadiusX = width * 0.42f
                    val baseRadiusY = 55f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ImperialGold.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(centerX, centerY + 30f),
                            radius = baseRadiusX
                        ),
                        center = Offset(centerX, centerY + 30f),
                        radius = baseRadiusX
                    )

                    // 2. Render 3D Extruded Slot Pedestals in Row
                    val numSlots = allSlots.size
                    val spacingX = (width - 60f) / (numSlots - 1)

                    val blockWidth = 38f
                    val blockDepth = 22f
                    val blockHeightNormal = 28f
                    val blockHeightSelected = 50f

                    for (i in 0 until numSlots) {
                        val slotData = allSlots[i]
                        val isSelected = i == selectedIndex

                        val rawX = (i - (numSlots - 1) / 2f) * spacingX
                        // Rotate x and z around y-axis
                        val rotX = rawX * cosA
                        val rotZ = rawX * sinA

                        val screenX = centerX + rotX
                        val screenY = centerY + rotZ * 0.35f

                        val curHeight = if (isSelected) blockHeightSelected else blockHeightNormal
                        val topY = screenY - curHeight - (if (isSelected) carPulseOffset else 0f)

                        // Colors for 3D faces
                        val topColor = when {
                            isSelected -> ImperialGold
                            slotData.badgeStr == "Popular" -> SapphireBlue
                            else -> DarkSurfaceVariant
                        }
                        val frontColor = when {
                            isSelected -> WarmBronze
                            slotData.badgeStr == "Popular" -> MidnightNavy
                            else -> Color(0xFF1E293B)
                        }
                        val sideColor = when {
                            isSelected -> Color(0xFF78350F)
                            else -> Color(0xFF0F172A)
                        }

                        // 3D Drop Shadow on stage
                        drawOval(
                            color = Color.Black.copy(alpha = if (isSelected) 0.6f else 0.3f),
                            topLeft = Offset(screenX - blockWidth / 1.5f, screenY - blockDepth / 2f + 10f),
                            size = Size(blockWidth * 1.3f, blockDepth * 1.2f)
                        )

                        // 3D Front Face
                        val frontPath = Path().apply {
                            moveTo(screenX - blockWidth / 2f, topY + blockDepth / 2f)
                            lineTo(screenX + blockWidth / 2f, topY + blockDepth / 2f)
                            lineTo(screenX + blockWidth / 2f, screenY + blockDepth / 2f)
                            lineTo(screenX - blockWidth / 2f, screenY + blockDepth / 2f)
                            close()
                        }
                        drawPath(frontPath, color = frontColor)

                        // 3D Side Face (Right depth)
                        val sidePath = Path().apply {
                            moveTo(screenX + blockWidth / 2f, topY + blockDepth / 2f)
                            lineTo(screenX + blockWidth / 2f + 10f, topY - blockDepth / 2f)
                            lineTo(screenX + blockWidth / 2f + 10f, screenY - blockDepth / 2f)
                            lineTo(screenX + blockWidth / 2f, screenY + blockDepth / 2f)
                            close()
                        }
                        drawPath(sidePath, color = sideColor)

                        // 3D Top Polygon Face
                        val topPath = Path().apply {
                            moveTo(screenX - blockWidth / 2f, topY + blockDepth / 2f)
                            lineTo(screenX + blockWidth / 2f, topY + blockDepth / 2f)
                            lineTo(screenX + blockWidth / 2f + 10f, topY - blockDepth / 2f)
                            lineTo(screenX - blockWidth / 2f + 10f, topY - blockDepth / 2f)
                            close()
                        }
                        drawPath(topPath, color = topColor)

                        // Neon Border Outline for selected block
                        if (isSelected) {
                            drawPath(
                                path = topPath,
                                color = Color.White,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                            )

                            // Draw LED Status Light Pillar above top
                            drawCircle(
                                color = EmeraldGreen,
                                radius = 5f,
                                center = Offset(screenX, topY - 8f)
                            )
                        }

                        // Draw 3D Mini Training Car on top of selected pedestal
                        if (isSelected) {
                            val carY = topY - 14f

                            // Car Body Shadow
                            drawOval(
                                color = Color.Black.copy(alpha = 0.5f),
                                topLeft = Offset(screenX - 16f, carY + 2f),
                                size = Size(32f, 12f)
                            )

                            // Car Main Chassis
                            drawRoundRect(
                                color = Color(0xFFDC2626), // Premium Red Training Vehicle
                                topLeft = Offset(screenX - 14f, carY - 8f),
                                size = Size(28f, 12f),
                                cornerRadius = CornerRadius(4f, 4f)
                            )

                            // Car Cabin / Windshield
                            drawRoundRect(
                                color = Color(0xFF38BDF8),
                                topLeft = Offset(screenX - 8f, carY - 14f),
                                size = Size(16f, 8f),
                                cornerRadius = CornerRadius(2f, 2f)
                            )

                            // Car Wheels
                            drawCircle(Color.Black, radius = 3.5f, center = Offset(screenX - 9f, carY + 4f))
                            drawCircle(Color.Black, radius = 3.5f, center = Offset(screenX + 9f, carY + 4f))

                            // Headlight beams casting forward
                            drawPath(
                                path = Path().apply {
                                    moveTo(screenX + 14f, carY - 4f)
                                    lineTo(screenX + 32f, carY - 10f)
                                    lineTo(screenX + 32f, carY + 4f)
                                    lineTo(screenX + 14f, carY + 2f)
                                    close()
                                },
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Yellow.copy(alpha = 0.8f), Color.Transparent)
                                )
                            )
                        }

                        // Time Label Text under each pedestal
                        val timeLayout = textMeasurer.measure(
                            text = AnnotatedString(slotData.displayLabel),
                            style = TextStyle(
                                color = if (isSelected) ImperialGold else Color.White.copy(alpha = 0.7f),
                                fontSize = if (isSelected) 11.sp else 9.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                            )
                        )
                        drawText(
                            textLayoutResult = timeLayout,
                            topLeft = Offset(screenX - timeLayout.size.width / 2f, screenY + 22f)
                        )
                    }
                }

                // Overlay Touch Tap Targets over the 3D pedestals
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    allSlots.forEachIndexed { index, slotData ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    selectedIndex = index
                                    onTimeSlotSelected(slotData.timeSlot)
                                },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (index == selectedIndex) {
                                Surface(
                                    color = ImperialGold,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = "SELECTED",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ObsidianBlack,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SELECTED SLOT 3D DETAIL HUD CARD
            Surface(
                color = MidnightNavy,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentData.isMorning) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                contentDescription = null,
                                tint = ImperialGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentData.timeSlot,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🛣️ ${currentData.trafficCondition}",
                            fontSize = 11.sp,
                            color = BrightGold
                        )
                    }

                    Surface(
                        color = when (currentData.badgeStr) {
                            "Popular" -> SapphireBlue
                            "2 Left", "3 Left" -> Color(0xFFEA580C)
                            else -> SuccessGreen
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${currentData.badgeStr} • ${currentData.slotsRemaining} Open",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // HORIZONTAL CHIP SELECTOR (QUICK ACCESSIBILITY FALLBACK)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                allSlots.take(4).forEachIndexed { idx, slotData ->
                    val isSel = selectedIndex == idx
                    Surface(
                        onClick = {
                            selectedIndex = idx
                            onTimeSlotSelected(slotData.timeSlot)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) ImperialGold else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ImperialGold else Color.Gray.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = slotData.displayLabel,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                            color = if (isSel) ObsidianBlack else Color.White,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
