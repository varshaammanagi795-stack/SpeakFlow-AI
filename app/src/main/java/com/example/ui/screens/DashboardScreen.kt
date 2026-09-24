package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.CertificateDialog
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToRoleplay: () -> Unit,
    onNavigateToSpeechLab: () -> Unit,
    onNavigateToPlacement: () -> Unit,
    onNavigateToMicroLessons: () -> Unit,
    onNavigateToLiveTutor: () -> Unit,
    onNavigateToVocab: () -> Unit,
    onNavigateToLanding: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val savedVocab by viewModel.savedVocab.collectAsStateWithLifecycle()
    val sessions by viewModel.practiceSessions.collectAsStateWithLifecycle()
    val currentScenario by viewModel.currentScenario.collectAsStateWithLifecycle()

    var showCertificate by remember { mutableStateOf(false) }

    if (showCertificate) {
        val cefr = viewModel.repository.scenarios.first().targetCEFR
        CertificateDialog(
            studentName = userProfile?.name ?: "Alex Rivera",
            cefrLevel = cefr,
            onDismiss = { showCertificate = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar / User Profile Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo)
                            .border(2.dp, SecondaryCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (userProfile?.name ?: "Alex").take(1),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Welcome back, ${userProfile?.name ?: "Alex"}! 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimaryIndigo.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "CEFR ${userProfile?.currentCEFR ?: "B2"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎯 Goal: ${userProfile?.targetCEFR ?: "C1"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Streak & XP Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onNavigateToLanding() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile?.streakDays ?: 12}d",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FluencyEmerald.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile?.xp ?: 1420} XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FluencyEmerald
                            )
                        }
                    }
                }
            }
        }

        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_speech_banner_1790265413391),
                        contentDescription = "SpeakFlow Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A).copy(alpha = 0.95f),
                                        Color(0xFF0F172A).copy(alpha = 0.70f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SecondaryCyan.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "AI VOICE & SPEECH ENGINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SecondaryCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Master Spoken English\nwith Native AI Avatars",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onNavigateToRoleplay,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("hero_start_roleplay_button")
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Start Roleplay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onNavigateToSpeechLab,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Speech Lab 👄", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Daily Fluency Index & Habit Tracker
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Fluency Index",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Composite AI score across cadence, clarity & grammar",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Fluency Ring Dial
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(SecondaryCyan, PrimaryIndigo))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${userProfile?.fluencyIndex ?: 82}%",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text("INDEX", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Micro Habit Bars
                    HabitProgressBar(
                        label = "Daily Speaking Practice",
                        current = "${userProfile?.totalMinutesPracticed ?: 15}m",
                        target = "20m target",
                        progress = 0.75f,
                        color = PrimaryIndigo
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HabitProgressBar(
                        label = "Filler Word Reduction (< 3%)",
                        current = "1.8% fillers",
                        target = "Goal: < 2%",
                        progress = 0.90f,
                        color = FluencyEmerald
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HabitProgressBar(
                        label = "Vocabulary Retained (SRS)",
                        current = "${savedVocab.size} words active",
                        target = "5 due today",
                        progress = 0.65f,
                        color = SecondaryCyan
                    )
                }
            }
        }

        // Quick Launch Feature Grid
        item {
            Text(
                text = "⚡ Accelerated Practice Modes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "AI Roleplay",
                    subtitle = "100+ Scenarios & Accents",
                    iconEmoji = "🎙️",
                    badgeText = "US / UK / AU",
                    color = PrimaryIndigo,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToRoleplay
                )
                QuickActionCard(
                    title = "Speech Lab",
                    subtitle = "2D Mouth IPA Visualizer",
                    iconEmoji = "👄",
                    badgeText = "Phonetics",
                    color = SecondaryCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSpeechLab
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Placement Test",
                    subtitle = "Automated 3-Min CEFR",
                    iconEmoji = "🎯",
                    badgeText = "A1 → C2",
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPlacement
                )
                QuickActionCard(
                    title = "1-on-1 Tutoring",
                    subtitle = "Live Call & Whiteboard",
                    iconEmoji = "👩‍🏫",
                    badgeText = "Live WebRTC",
                    color = FluencyEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLiveTutor
                )
            }
        }

        // Recommended Micro-Lesson Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMicroLessons() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "5-MIN MICRO-LESSON",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5CF6),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "High-Impact Business Phrasal Verbs",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Master 'walk through', 'push back' & 'bring up'",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Saved Vocab SRS Quick Action & Certificate Launcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToVocab() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📚 Spaced Repetition", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${savedVocab.size} Words in Deck", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Review Flashcards →", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showCertificate = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🎓 CEFR Milestone", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("View Certificate", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Download & Share →", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitProgressBar(
    label: String,
    current: String,
    target: String,
    progress: Float,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text("$current · $target", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    badgeText: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("quick_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(iconEmoji, fontSize = 24.sp)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
