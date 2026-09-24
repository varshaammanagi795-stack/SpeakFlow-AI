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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CertificateDialog
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: MainViewModel
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val badges by viewModel.badges.collectAsStateWithLifecycle()
    val sessions by viewModel.practiceSessions.collectAsStateWithLifecycle()

    var showCertDialog by remember { mutableStateOf(false) }

    if (showCertDialog) {
        val cefr = viewModel.repository.scenarios.first().targetCEFR
        CertificateDialog(
            studentName = userProfile?.name ?: "Alex Rivera",
            cefrLevel = cefr,
            onDismiss = { showCertDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(PrimaryIndigo, SecondaryCyan))
                            )
                            .border(3.dp, FluencyEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (userProfile?.name ?: "Alex").take(1),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userProfile?.name ?: "Alex Rivera",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${userProfile?.email ?: "alex.learner@example.com"} · CEFR ${userProfile?.currentCEFR ?: "B2"} Spoken English",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showCertDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        modifier = Modifier.testTag("view_cefr_certificate_button")
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View & Share CEFR Certificate 🎓", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Gamification Stats Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "XP Earned",
                    value = "${userProfile?.xp ?: 1420}",
                    emoji = "⚡",
                    color = FluencyEmerald,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Streak",
                    value = "${userProfile?.streakDays ?: 12} Days",
                    emoji = "🔥",
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Minutes",
                    value = "${userProfile?.totalMinutesPracticed ?: 185}m",
                    emoji = "⏱️",
                    color = SecondaryCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Global Weekly League Leaderboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏆 Diamond League Leaderboard", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Resets in 2d", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val leaderboard = listOf(
                        Triple("🥇 1", "Yuki Tanaka (Tokyo 🇯🇵)", "2,420 XP"),
                        Triple("🥈 2", "Alex Rivera (You 🇺🇸)", "${userProfile?.xp ?: 1850} XP"),
                        Triple("🥉 3", "Mateo Silva (São Paulo 🇧🇷)", "1,780 XP"),
                        Triple("4", "Elena Rostova (Madrid 🇪🇸)", "1,540 XP")
                    )

                    leaderboard.forEach { (rank, name, xp) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(rank, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(name, fontSize = 12.sp, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                            Text(xp, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryIndigo)
                        }
                    }
                }
            }
        }

        // Skill Badges Showcase
        item {
            Text("🏅 Unlocked Badges & Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(badges) { badge ->
                    Card(
                        modifier = Modifier.width(140.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (badge.isUnlocked) PrimaryIndigo.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(badge.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(badge.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(badge.description, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }

        // Practice History
        item {
            Text("📜 Speech Telemetry History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(sessions) { s ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(s.scenarioTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${s.wpm} WPM · ${s.pronunciationScore}% Clarity", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FluencyEmerald)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(s.keyFeedback, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
            Text(title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
