package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LivePickupGpsTrackerComponent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isSimulatingDrive by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "GPS Car Transition")

    val carProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "carProgress"
    )

    val currentEtaMinutes = (8 - (carProgress * 7).toInt()).coerceAtLeast(1)
    val distanceKm = String.format("%.1f", (2.4 - (carProgress * 2.1)).coerceAtLeast(0.3))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_pickup_gps_tracker_card"),
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
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Live Doorstep Pickup GPS Tracker",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Simulated Real-Time Instructor Vehicle Tracking",
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
                        text = "ETA $currentEtaMinutes MINS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SuccessGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SIMULATED GPS ROUTE CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MidnightNavy,
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(1.dp, ImperialGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .testTag("gps_map_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw Map Grid / Block outline
                    for (i in 0..4) {
                        val x = w * (i / 4f)
                        drawLine(Color.White.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                    }
                    for (j in 0..3) {
                        val y = h * (j / 3f)
                        drawLine(Color.White.copy(alpha = 0.08f), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                    }

                    // S-Curve Route Path
                    val path = Path().apply {
                        moveTo(w * 0.1f, h * 0.8f)
                        cubicTo(
                            w * 0.35f, h * 0.8f,
                            w * 0.45f, h * 0.2f,
                            w * 0.85f, h * 0.25f
                        )
                    }

                    // Draw Road Path
                    drawPath(
                        path = path,
                        color = Color.Gray.copy(alpha = 0.4f),
                        style = Stroke(width = 18f)
                    )
                    drawPath(
                        path = path,
                        color = ImperialGold,
                        style = Stroke(width = 4f)
                    )

                    // Pickup Destination Marker
                    val destX = w * 0.85f
                    val destY = h * 0.25f
                    drawCircle(ImperialGold, radius = 14f, center = Offset(destX, destY))
                    drawCircle(ObsidianBlack, radius = 6f, center = Offset(destX, destY))

                    // Compute animated car position along bezier
                    val t = if (isSimulatingDrive) carProgress else 0.5f
                    val u = 1 - t
                    val p0 = Offset(w * 0.1f, h * 0.8f)
                    val p1 = Offset(w * 0.35f, h * 0.8f)
                    val p2 = Offset(w * 0.45f, h * 0.2f)
                    val p3 = Offset(w * 0.85f, h * 0.25f)

                    val carX = u * u * u * p0.x + 3 * u * u * t * p1.x + 3 * u * t * t * p2.x + t * t * t * p3.x
                    val carY = u * u * u * p0.y + 3 * u * u * t * p1.y + 3 * u * t * t * p2.y + t * t * t * p3.y

                    // Draw Instructor Vehicle Pulse & Icon
                    drawCircle(Color.Red.copy(alpha = 0.35f), radius = 22f, center = Offset(carX, carY))
                    drawCircle(Color.Red, radius = 12f, center = Offset(carX, carY))
                    drawCircle(Color.White, radius = 5f, center = Offset(carX, carY))
                }

                // OVERLAY HUD TEXT
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .background(DarkSurface.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Vehicle $distanceKm km away",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // INSTRUCTOR & VEHICLE INFO CARD
            Surface(
                color = MidnightNavy,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rajesh Kumar", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("⭐ 4.9", fontSize = 10.sp, color = ImperialGold, fontWeight = FontWeight.Bold)
                        }
                        Text("Creta Auto • Reg: MH-12-DD-4022", fontSize = 11.sp, color = BrightGold)
                        Text("Doorstep Pickup Point: Home Residence", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+918403050225"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ImperialGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", color = ObsidianBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
