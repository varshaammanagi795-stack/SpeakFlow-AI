package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PeerPracticeTopic
import com.example.ui.theme.*

@Composable
fun PeerPracticeScreen(
    viewModel: MainViewModel
) {
    var selectedTopic by remember { mutableStateOf(viewModel.repository.peerTopics.first()) }
    var isRoomActive by remember { mutableStateOf(false) }
    var currentIcebreakerIndex by remember { mutableStateOf(0) }
    var currentSpeaker by remember { mutableStateOf("You (Turn 1)") }
    var peerRatingGiven by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "👥 Peer-to-Peer Practice Rooms",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "5-minute structured conversation rooms with ESL peers worldwide, guided icebreakers, and turn timers.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Active Room Card / Matchmaking Sandbox
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("peer_practice_active_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isRoomActive) FluencyEmerald else AccentAmber)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRoomActive) "CONNECTED LIVE (5-MIN SESSION)" else "MATCHED WITH PEER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRoomActive) FluencyEmerald else AccentAmber
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryIndigo.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "⏱️ 04:18 Left",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Peer Participants Avatars Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User avatar
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryIndigo)
                                    .border(2.dp, if (currentSpeaker.startsWith("You")) FluencyEmerald else Color.Transparent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("You (Alex)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("CEFR B2", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // VS / Handshake Icon
                        Text("🤝", fontSize = 24.sp)

                        // Peer Avatar
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryCyan)
                                    .border(2.dp, if (!currentSpeaker.startsWith("You")) FluencyEmerald else Color.Transparent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("E", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Elena (Madrid 🇪🇸)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("CEFR B2", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Structured Icebreaker Card Deck
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🃏 Structured Icebreaker Card", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                                TextButton(
                                    onClick = {
                                        currentIcebreakerIndex = (currentIcebreakerIndex + 1) % selectedTopic.icebreakers.size
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Next Card ↻", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${selectedTopic.icebreakers.getOrElse(currentIcebreakerIndex) { selectedTopic.icebreakers.first() }}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Turn Switcher and Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                currentSpeaker = if (currentSpeaker.startsWith("You")) "Elena (Turn 2)" else "You (Turn 1)"
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Text("Switch Speaking Turn 🔄", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { isRoomActive = !isRoomActive },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRoomActive) Color(0xFFEF4444) else FluencyEmerald
                            )
                        ) {
                            Text(if (isRoomActive) "Leave Room" else "Start 5-Min Timer", fontSize = 12.sp)
                        }
                    }

                    // Peer Feedback Rating
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rate Peer Cadence:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row {
                            (1..5).forEach { star ->
                                Text(
                                    text = if (star <= peerRatingGiven) "⭐" else "☆",
                                    fontSize = 18.sp,
                                    modifier = Modifier.clickable { peerRatingGiven = star }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Topic Catalog List
        item {
            Text(
                text = "🎯 Today's 5-Minute Conversation Topics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(viewModel.repository.peerTopics) { topic ->
            val isSelected = topic.id == selectedTopic.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedTopic = topic },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) PrimaryIndigo.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder() else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = PrimaryIndigo.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = topic.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Key debate points: ${topic.keyDebatePoints.joinToString(" · ")}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
