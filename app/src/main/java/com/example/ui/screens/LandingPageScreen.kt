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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun LandingPageScreen(
    viewModel: MainViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToRoleplay: () -> Unit
) {
    var isTutorPortalMode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Navbar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(listOf(PrimaryIndigo, SecondaryCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SF", fontWeight = FontWeight.Black, color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SpeakFlow AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }

                // Student vs Tutor Portal Toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(2.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isTutorPortalMode) PrimaryIndigo else Color.Transparent,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "Learner",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isTutorPortalMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable { isTutorPortalMode = false }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTutorPortalMode) PrimaryIndigo else Color.Transparent,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "Tutor Portal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTutorPortalMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable { isTutorPortalMode = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        if (!isTutorPortalMode) {
            // HERO SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PrimaryIndigo.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Text(
                                text = "🚀 Next-Gen AI Spoken English Platform",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Speak English With\nFlawless Confidence",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Real-time phonetic telemetry, 2D mouth & tongue articulation, dynamic AI roleplays across 100+ accents, and 1-on-1 human tutoring.",
                            fontSize = 12.sp,
                            color = DarkTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onNavigateToRoleplay,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("landing_try_sandbox_button")
                            ) {
                                Text("Try AI Sandbox 🎙️", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onNavigateToDashboard,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Enter Platform")
                            }
                        }
                    }
                }
            }

            // PAIN VS SOLUTION MATRIX
            item {
                Text("⚔️ Why Traditional Apps Fail vs SpeakFlow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentRose.copy(alpha = 0.08f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("❌ Traditional Apps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentRose)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Static multiple-choice taps", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("• Fake simulated robots", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("• No phonetic mouth feedback", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("• High speaking anxiety", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FluencyEmerald.copy(alpha = 0.08f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("✅ SpeakFlow AI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FluencyEmerald)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• 100% spoken voice practice", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("• 2D Mouth IPA articulation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("• Live filler & WPM telemetry", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("• 1-on-1 WebRTC Live Tutors", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // PRICING TIERS
            item {
                Text("💎 Flexible Pricing & Enterprise LMS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PricingCard(
                        title = "Free Starter",
                        price = "$0 / month",
                        description = "3 AI Roleplay sessions/day, Speech Lab basic phonemes, Placement test.",
                        isPopular = false,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PricingCard(
                        title = "Pro Fluency Tier",
                        price = "$19 / month",
                        description = "Unlimited AI roleplays, 100+ accents, full 2D sagittal visualizer, unlimited peer rooms.",
                        isPopular = true,
                        color = PrimaryIndigo
                    )
                    PricingCard(
                        title = "Enterprise & Corporate LMS",
                        price = "$49 / seat / mo",
                        description = "Team progress dashboard, live tutor credits, custom corporate scenarios & whiteboarding.",
                        isPopular = false,
                        color = SecondaryCyan
                    )
                }
            }

            // FAQ
            item {
                Text("❓ Frequently Asked Questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Q: How does the 2D Mouth Articulation Visualizer work?", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("A: It models dynamic sagittal vocal tract biomechanics, showing precise tongue contact (interdental, alveolar, palatal) and vocal cord vibration.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Q: Are the CEFR placement scores accredited?", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("A: Yes, our evaluation prompts follow Cambridge CEFR benchmarks evaluating vocabulary density, grammar complexity, cadence, and coherence.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            // TUTOR & ENTERPRISE PORTAL VIEW
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("👩‍🏫 Tutor Management & Analytics Portal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("View assigned student rosters, review speech telemetry logs, and launch live whiteboard sessions.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Assigned Students:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                        Spacer(modifier = Modifier.height(6.dp))
                        listOf(
                            "Alex Rivera (B2 → Target C1) · Avg WPM: 138 · 1.8% Fillers",
                            "Elena Rostova (B1 → Target B2) · Avg WPM: 115 · Pronunciation: 84%",
                            "Kenji Takahashi (C1 → Target C2) · Avg WPM: 145 · Advanced Idioms"
                        ).forEach { stu ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            ) {
                                Text("🎓 $stu", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PricingCard(
    title: String,
    price: String,
    description: String,
    isPopular: Boolean,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) PrimaryIndigo.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = if (isPopular) CardDefaults.outlinedCardBorder() else null
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (isPopular) {
                    Surface(shape = RoundedCornerShape(6.dp), color = PrimaryIndigo) {
                        Text("MOST POPULAR", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = price, fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (isPopular) PrimaryIndigo else MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
