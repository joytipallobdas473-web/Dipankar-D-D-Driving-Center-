package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CourseEntity
import com.example.ui.components.EmiFeeCalculatorComponent
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
fun CoursesScreen(
    mainViewModel: MainViewModel,
    onNavigateToBookingWithCourse: (CourseEntity) -> Unit
) {
    val courses by mainViewModel.courses.collectAsState()
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "Manual", "Automatic", "Express", "Refresher", "License Prep")

    val filteredCourses = if (selectedCategory == "ALL") {
        courses
    } else {
        courses.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("courses_screen_column")
    ) {
        SectionHeader(
            title = "Driving Courses & Pricing",
            subtitle = "Comprehensive packages with doorstep pickup (Prices in ₹ INR)"
        )

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                ThreeDChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = category,
                    testTag = "filter_chip_$category"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filteredCourses) { course ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (course.isPopular) BrightGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (course.badge.isNotBlank()) {
                                    StatusBadge(text = course.badge)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = course.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${course.category} Transmission • ${course.durationWeeks} Weeks (${course.totalHours} Total Hours)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${course.priceInr.toInt()}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrightGold
                                )
                                Text(
                                    text = "₹${course.originalPriceInr.toInt()}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = course.description,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Highlights Bullet Grid
                        val highlightsList = course.highlights.split(",").map { it.trim() }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            highlightsList.forEach { perk ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BrightGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = perk,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ThreeDButton(
                            onClick = { onNavigateToBookingWithCourse(course) },
                            text = "Book This Course",
                            icon = Icons.Default.Event,
                            containerColor = BrightGold,
                            contentColor = DeepNavy,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_book_course_${course.id}"
                        )
                    }
                }
            }

            item {
                EmiFeeCalculatorComponent()
            }
        }
    }
}
