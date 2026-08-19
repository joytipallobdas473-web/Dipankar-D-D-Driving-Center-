package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ripple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.NavigationTab

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = ImperialGold.copy(alpha = 0.35f),
    backgroundColor: Color = DarkSurface.copy(alpha = 0.88f),
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        ChampagneGold.copy(alpha = 0.6f),
                        borderColor.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        color = backgroundColor,
        tonalElevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarHeader(
    currentTab: NavigationTab,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenCallSupport: () -> Unit,
    onOpenAdmin: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ImperialGold, ChampagneGold, MetallicGold)
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "D&D",
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "D&D DRIVING CENTER",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "★ EXECUTIVE DRIVING ACADEMY",
                        fontSize = 9.sp,
                        color = ImperialGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onOpenAdmin,
                modifier = Modifier.testTag("btn_open_admin_portal")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Portal",
                    tint = ImperialGold
                )
            }
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.testTag("btn_toggle_theme")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = ImperialGold
                )
            }
            IconButton(
                onClick = onOpenCallSupport,
                modifier = Modifier.testTag("btn_call_support")
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call Helpline",
                    tint = SuccessGreen
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    )
}

@Composable
fun BottomNavBar(
    selectedTab: NavigationTab,
    onSelectTab: (NavigationTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(NavigationTab.HOME, Icons.Default.Home, Icons.Outlined.Home),
            Triple(NavigationTab.COURSES, Icons.Default.DirectionsCar, Icons.Outlined.DirectionsCar),
            Triple(NavigationTab.SIMULATOR, Icons.Default.VideogameAsset, Icons.Outlined.VideogameAsset),
            Triple(NavigationTab.BOOKING, Icons.Default.Event, Icons.Outlined.Event),
            Triple(NavigationTab.INSTRUCTORS, Icons.Default.Badge, Icons.Outlined.Badge),
            Triple(NavigationTab.MORE, Icons.Default.Grid3x3, Icons.Outlined.Grid3x3)
        )

        items.forEach { (tab, filledIcon, outlinedIcon) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = tab.title,
                        tint = if (isSelected) BrightGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BrightGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = BrightGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color = BrightGold.copy(alpha = 0.15f),
    contentColor: Color = BrightGold
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

/**
 * Modern 3D Tactile Push Button with interactive press depth,
 * bevel highlight, and metallic bottom shadow.
 */
@Composable
fun ThreeDButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = BrightGold,
    contentColor: Color = DeepNavy,
    shadowColor: Color = MetallicGold,
    depth: Dp = 5.dp,
    shape: CornerBasedShape = RoundedCornerShape(14.dp),
    icon: ImageVector? = null,
    fontSize: TextUnit = 14.sp,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val topOffsetY by animateDpAsState(
        targetValue = if (isPressed) depth - 1.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )

    Box(
        modifier = modifier
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
            .height(IntrinsicSize.Min)
    ) {
        // Bottom 3D Depth Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = depth)
                .background(
                    color = if (enabled) shadowColor else Color.Gray.copy(alpha = 0.4f),
                    shape = shape
                )
        )
        // Top Face Button
        Box(
            modifier = Modifier
                .offset(y = topOffsetY)
                .background(
                    brush = if (enabled) {
                        Brush.verticalGradient(
                            listOf(
                                containerColor,
                                containerColor.copy(alpha = 0.88f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color.LightGray,
                                Color.Gray
                            )
                        )
                    },
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = shape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    enabled = enabled,
                    onClick = onClick
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) contentColor else Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (enabled) contentColor else Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 3D Tactile Icon Button with press depth animation
 */
@Composable
fun ThreeDIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    containerColor: Color = BrightGold,
    contentColor: Color = DeepNavy,
    shadowColor: Color = MetallicGold,
    size: Dp = 44.dp,
    depth: Dp = 4.dp,
    shape: CornerBasedShape = CircleShape,
    enabled: Boolean = true,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val topOffsetY by animateDpAsState(
        targetValue = if (isPressed) depth - 1.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )

    Box(
        modifier = modifier
            .size(size)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        // Bottom 3D Depth Base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = depth)
                .background(
                    color = if (enabled) shadowColor else Color.Gray.copy(alpha = 0.4f),
                    shape = shape
                )
        )
        // Top Face
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = topOffsetY)
                .padding(bottom = depth - topOffsetY)
                .background(
                    brush = if (enabled) {
                        Brush.verticalGradient(
                            listOf(containerColor, containerColor.copy(alpha = 0.88f))
                        )
                    } else {
                        Brush.verticalGradient(listOf(Color.LightGray, Color.Gray))
                    },
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = shape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) contentColor else Color.DarkGray,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}

/**
 * 3D Tactile Filter/Select Chip with pressed state bevel
 */
@Composable
fun ThreeDChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selectedContainerColor: Color = BrightGold,
    selectedContentColor: Color = DeepNavy,
    selectedShadowColor: Color = MetallicGold,
    unselectedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
    unselectedShadowColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    depth: Dp = 3.dp,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val topOffsetY by animateDpAsState(
        targetValue = if (isPressed) depth - 1.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )

    val currentContainer = if (selected) selectedContainerColor else unselectedContainerColor
    val currentContent = if (selected) selectedContentColor else unselectedContentColor
    val currentShadow = if (selected) selectedShadowColor else unselectedShadowColor

    Box(
        modifier = modifier
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
            .height(IntrinsicSize.Min)
    ) {
        // Bottom 3D Depth
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = depth)
                .background(currentShadow, RoundedCornerShape(10.dp))
        )
        // Top Face
        Box(
            modifier = Modifier
                .offset(y = topOffsetY)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(currentContainer, currentContainer.copy(alpha = 0.9f))
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (selected) Color.White.copy(alpha = 0.5f) else currentContent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick
                )
                .padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = currentContent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = label,
                    color = currentContent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

