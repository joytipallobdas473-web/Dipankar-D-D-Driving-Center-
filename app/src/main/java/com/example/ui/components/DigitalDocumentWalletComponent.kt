package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

data class DigitalDocument(
    val id: Int,
    val title: String,
    val docNumber: String,
    val status: String, // "Verified", "Uploaded", "Pending"
    val expiryDate: String,
    val icon: String
)

@Composable
fun DigitalDocumentWalletComponent(
    modifier: Modifier = Modifier
) {
    val documents = remember {
        mutableStateListOf(
            DigitalDocument(1, "Learner's Driving License (LLR)", "LLR-MH12-2026-90412", "Verified", "Expires Nov 2026", "📜"),
            DigitalDocument(2, "Government Aadhaar / ID Proof", "XXXX-XXXX-8821", "Verified", "Permanent Document", "🪪"),
            DigitalDocument(3, "RTO Medical Fitness Certificate", "MED-RTO-2026-081", "Uploaded", "Valid 1 Year", "🩺"),
            DigitalDocument(4, "D&D Course Completion Certificate", "CERT-DD-2026-8802", "Issued", "Verified Gold Seal", "🎓")
        )
    }

    var selectedDocForPreview by remember { mutableStateOf<DigitalDocument?>(null) }
    var uploadMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("digital_document_wallet_card"),
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
                        Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Digital Document Wallet",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "RTO Verified Documents & Certificates",
                            fontSize = 10.sp,
                            color = ImperialGold
                        )
                    }
                }

                IconButton(onClick = {
                    uploadMessage = "New Document Upload Scanner Opened!"
                }) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Upload Document", tint = ImperialGold)
                }
            }

            if (uploadMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MidnightNavy,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold)
                ) {
                    Text(
                        text = uploadMessage!!,
                        fontSize = 11.sp,
                        color = BrightGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // DOCUMENTS LIST
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                documents.forEach { doc ->
                    Surface(
                        onClick = { selectedDocForPreview = doc },
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImperialGold.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_item_${doc.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(doc.icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                    Text(doc.docNumber, fontSize = 10.sp, color = BrightGold)
                                    Text(doc.expiryDate, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            StatusBadge(
                                text = doc.status,
                                containerColor = when (doc.status) {
                                    "Verified", "Issued" -> SuccessGreen.copy(alpha = 0.2f)
                                    else -> SapphireBlue.copy(alpha = 0.2f)
                                },
                                contentColor = when (doc.status) {
                                    "Verified", "Issued" -> SuccessGreen
                                    else -> SapphireBlue
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // PREVIEW DIALOG
    if (selectedDocForPreview != null) {
        val doc = selectedDocForPreview!!
        Dialog(onDismissRequest = { selectedDocForPreview = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ImperialGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(doc.icon, fontSize = 42.sp)
                    Text(doc.title, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)

                    Surface(
                        color = MidnightNavy,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Document ID: ${doc.docNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ImperialGold)
                            Text("Status: ${doc.status}", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                            Text("Validity: ${doc.expiryDate}", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { selectedDocForPreview = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close", color = Color.White)
                        }
                        Button(
                            onClick = { selectedDocForPreview = null },
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Download", color = ObsidianBlack)
                        }
                    }
                }
            }
        }
    }
}
