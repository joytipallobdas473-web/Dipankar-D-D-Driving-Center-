package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InstructorEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDChip
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SapphireBlue
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.NavigationTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorsScreen(
    mainViewModel: MainViewModel,
    onSelectInstructorToBook: (InstructorEntity) -> Unit
) {
    val context = LocalContext.current
    val instructors by mainViewModel.instructors.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filters = listOf("ALL", "Female Instructors", "Automatic Specialists", "Senior Trainers")

    val filteredInstructors = when (selectedFilter) {
        "Female Instructors" -> instructors.filter { it.gender.equals("Female", ignoreCase = true) }
        "Automatic Specialists" -> instructors.filter { it.vehicleAssigned.contains("Automatic", ignoreCase = true) }
        "Senior Trainers" -> instructors.filter { it.experienceYears >= 12 }
        else -> instructors
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("instructors_screen_column")
    ) {
        SectionHeader(
            title = "Certified Instructors",
            subtitle = "RTO Master trainers & dedicated Female Instructors"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(filters) { f ->
                val isSelected = selectedFilter == f
                ThreeDChip(
                    selected = isSelected,
                    onClick = { selectedFilter = f },
                    label = f,
                    testTag = "filter_instructor_$f"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filteredInstructors) { instructor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (instructor.gender == "Female") Color(0xFFEC4899) else SapphireBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = instructor.name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = instructor.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (instructor.gender == "Female") {
                                        StatusBadge(text = "FEMALE SPECIALIST", containerColor = Color(0xFFFCE7F3), contentColor = Color(0xFFBE185D))
                                    }
                                }

                                Text(
                                    text = "${instructor.experienceYears} Years Experience • ${instructor.specialization}",
                                    fontSize = 12.sp,
                                    color = BrightGold,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${instructor.rating} (${instructor.reviewCount} student ratings)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = instructor.bio,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SapphireBlue, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Assigned Vehicle: ${instructor.vehicleAssigned}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ThreeDButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${instructor.phone}"))
                                    context.startActivity(intent)
                                },
                                text = "Call",
                                icon = Icons.Default.Call,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                shadowColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp
                            )

                            ThreeDButton(
                                onClick = { onSelectInstructorToBook(instructor) },
                                text = "Book Slot",
                                icon = Icons.Default.Event,
                                containerColor = BrightGold,
                                contentColor = DeepNavy,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp,
                                testTag = "btn_book_instructor_${instructor.id}"
                            )
                        }
                    }
                }
            }
        }
    }
}
