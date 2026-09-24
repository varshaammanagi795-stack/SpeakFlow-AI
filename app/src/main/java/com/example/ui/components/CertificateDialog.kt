package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CEFRLevel
import com.example.ui.theme.*

@Composable
fun CertificateDialog(
    studentName: String,
    cefrLevel: CEFRLevel,
    issueDate: String = "September 24, 2026",
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("cefr_certificate_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFE2E8F0), Color(0xFFF59E0B))),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPEAKFLOW ACADEMY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color(0xFFF59E0B)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CERTIFICATE OF PROFICIENCY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "This certifies that",
                    fontSize = 12.sp,
                    color = DarkTextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = studentName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryCyan,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "has successfully demonstrated spoken fluency and communicative competence aligned with the Common European Framework of Reference (CEFR):",
                    fontSize = 11.sp,
                    color = DarkTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CEFR Level Badge Box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryIndigo, Color(0xFF9333EA))
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cefrLevel.label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Date of Issue", fontSize = 9.sp, color = DarkTextSecondary)
                        Text(issueDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }

                    // Gold Seal
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                            .border(2.dp, Color(0xFFF59E0B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("★ CEFR ★", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Credential ID", fontSize = 9.sp, color = DarkTextSecondary)
                        Text("SF-9482-B2", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("share_certificate_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share & Save Credential", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
