package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.*
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SapphireBlue
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.MainViewModel

data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryFaqContactScreen(
    mainViewModel: MainViewModel,
    onOpenAdminPortal: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val galleryItems by mainViewModel.galleryItems.collectAsState()
    val testimonials by mainViewModel.testimonials.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: About & Gallery, 1: FAQ, 2: Contact

    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactMsg by remember { mutableStateOf("") }
    var contactSent by remember { mutableStateOf(false) }

    val faqList = listOf(
        FaqItem(
            question = "Are female driving instructors available for lessons?",
            answer = "Yes! We have certified female driving instructors available for both manual and automatic SUV/Hatchback packages. You can select your preferred instructor during booking."
        ),
        FaqItem(
            question = "Is doorstep pickup and drop included in the packages?",
            answer = "Absolutely! Free doorstep pickup and drop is included in all our 2-week and 3-week comprehensive driving packages across the city."
        ),
        FaqItem(
            question = "What are the fees in Indian Rupees (₹ INR)?",
            answer = "Our courses range from ₹3,200 for Refresher courses, ₹5,500 for Hatchback Manual, up to ₹8,900 for 7-Day Fast-Track Express. All prices are transparent with no hidden charges."
        ),
        FaqItem(
            question = "Will D&D Driving Center assist with my RTO Permanent License test?",
            answer = "Yes, we provide dedicated RTO test preparation track simulation (Figure-8 and H-Track), document verification assistance, and provide official dual-control vehicles for your test day."
        ),
        FaqItem(
            question = "Can I practice on the 3D Simulator before real road driving?",
            answer = "Yes! All students get unlimited access to our high-tech 3D Cockpit Simulator lab to build steering, pedal, and signal muscle memory safely before stepping onto city roads."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("gallery_faq_screen_column"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            SectionHeader(
                title = "About, Gallery & Support",
                subtitle = "D&D Driving Center Facilities, FAQs & Admissions Desk"
            )

            ScrollableTabRow(
                selectedTabIndex = activeSubTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrightGold,
                edgePadding = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(selected = activeSubTab == 0, onClick = { activeSubTab = 0 }, text = { Text("About", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = activeSubTab == 1, onClick = { activeSubTab = 1 }, text = { Text("Student Tools", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = activeSubTab == 2, onClick = { activeSubTab = 2 }, text = { Text("FAQ", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(selected = activeSubTab == 3, onClick = { activeSubTab = 3 }, text = { Text("Contact", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                Tab(
                    selected = false,
                    onClick = { onOpenAdminPortal?.invoke() },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = BrightGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrightGold)
                        }
                    }
                )
            }
        }

        if (activeSubTab == 1) {
            item { RtoQuizComponent() }
            item { StudentSkillScorecardComponent() }
            item { StudentLeaderboardComponent() }
            item { LivePickupGpsTrackerComponent() }
            item { DigitalDocumentWalletComponent() }
            item { EmiFeeCalculatorComponent() }
            item { RoadsideBreakdownAssistantComponent() }
        }

        if (activeSubTab == 0) {
            // ADMIN LOGIN PROMPT CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BrightGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BrightGold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("D&D Staff Admin Portal", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Manage bookings & revenue (PIN: 1234)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        ThreeDButton(
                            onClick = { onOpenAdminPortal?.invoke() },
                            text = "Login",
                            containerColor = BrightGold,
                            contentColor = DeepNavy,
                            fontSize = 12.sp,
                            testTag = "btn_open_admin_portal"
                        )
                    }
                }
            }

            // ABOUT US CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusBadge(text = "ESTABLISHED 2011 • ISO 9001 CERTIFIED")
                        Text(
                            text = "About D&D Driving Center",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "D&D Driving Center is a premier motor driving school committed to building safe, responsible, and defensive drivers. Equipped with dual-control modern vehicles, private RTO track simulators, and patient master trainers, we have successfully trained over 12,500+ licensed students with a 99.4% first-attempt pass rate.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // GALLERY ITEMS
            items(galleryItems) { gItem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SapphireBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = BrightGold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(gItem.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(gItem.category, fontSize = 11.sp, color = BrightGold, fontWeight = FontWeight.SemiBold)
                            Text(gItem.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        } else if (activeSubTab == 2) {
            // FAQ ACCORDION LIST
            items(faqList) { faq ->
                var expanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faq.question,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = BrightGold
                            )
                        }

                        AnimatedVisibility(visible = expanded) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = faq.answer,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        } else if (activeSubTab == 3) {
            // CONTACT FORM & HELPLINE DIRECT LAUNCHERS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Contact Admissions Desk", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        if (contactSent) {
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Message Sent! Our admissions counselor will call you within 30 minutes.",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 12.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedTextField(
                            value = contactName,
                            onValueChange = { contactName = it },
                            label = { Text("Your Name") },
                            modifier = Modifier.fillMaxWidth().testTag("input_contact_name")
                        )

                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("Mobile Number") },
                            modifier = Modifier.fillMaxWidth().testTag("input_contact_phone")
                        )

                        OutlinedTextField(
                            value = contactMsg,
                            onValueChange = { contactMsg = it },
                            label = { Text("Query / Message") },
                            modifier = Modifier.fillMaxWidth().height(90.dp).testTag("input_contact_msg")
                        )

                        ThreeDButton(
                            onClick = {
                                if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
                                    contactSent = true
                                }
                            },
                            text = "Send Inquiry",
                            containerColor = BrightGold,
                            contentColor = DeepNavy,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_send_contact_form"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Official Helpline Numbers", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        // Phone 1 Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Primary Helpline", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("+91 8403050225", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrightGold)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+918403050225"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(36.dp).background(SapphireBlue, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Call 8403050225", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918403050225"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(36.dp).background(SuccessGreen, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "WhatsApp 8403050225", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        // Phone 2 Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Secondary Helpline", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("+91 9101303239", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrightGold)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919101303239"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(36.dp).background(SapphireBlue, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Call 9101303239", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919101303239"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.size(36.dp).background(SuccessGreen, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "WhatsApp 9101303239", tint = Color.White, modifier = Modifier.size(18.dp))
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
