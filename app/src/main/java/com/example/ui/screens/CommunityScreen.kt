package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.CommunityDiscussion
import com.example.ui.theme.*

@Composable
fun CommunityScreen(
    viewModel: MainViewModel,
    onNavigateToPeer: () -> Unit
) {
    var upvotedMap by remember { mutableStateOf(mapOf<String, Boolean>()) }
    var showMatchDialog by remember { mutableStateOf(false) }

    if (showMatchDialog) {
        AlertDialog(
            onDismissRequest = { showMatchDialog = false },
            title = { Text("Language Exchange Buddy Matcher", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("We found 3 native & advanced speakers with matching time zones who want to practice with you:")
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "David Miller (London 🇬🇧) · Native English → Learning Spanish",
                        "Camille Dubois (Montreal 🇨🇦) · Native English & French",
                        "Kenji Takahashi (Tokyo 🇯🇵) · CEFR C1 English"
                    ).forEach { buddy ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryIndigo.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                showMatchDialog = false
                                onNavigateToPeer()
                            }
                        ) {
                            Text("👤 $buddy", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showMatchDialog = false }) { Text("Close") } }
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
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🌍 Community & Masterclasses",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Audio discussion forum, daily pronunciation challenges, and language exchange buddies.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Buddy Matcher & Peer Practice Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FluencyEmerald.copy(alpha = 0.2f)
                        ) {
                            Text("ACTIVE MATCHMAKING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FluencyEmerald, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Find a Speaking Buddy", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Match with global learners for 5-minute daily audio exchanges.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = { showMatchDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Match 🤝", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Community Discussions List
        item {
            Text(
                text = "💬 Active Audio Threads & Workshops",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(viewModel.repository.communityDiscussions) { post ->
            val isUpvoted = upvotedMap[post.id] ?: false
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (post.isMasterclass) PrimaryIndigo.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = if (post.isMasterclass) CardDefaults.outlinedCardBorder() else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(post.authorAvatarColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(post.authorName.take(1), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(post.authorName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${post.authorLevel.name.take(2)} · ${post.timeAgo}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (post.isMasterclass) Color(0xFFEF4444).copy(alpha = 0.2f) else PrimaryIndigo.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = post.topicTag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (post.isMasterclass) Color(0xFFEF4444) else PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = post.transcript,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (post.audioDurationSec > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimaryIndigo.copy(alpha = 0.12f),
                                modifier = Modifier.clickable {
                                    viewModel.ttsManager.speak(post.transcript, "US")
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Play Audio (${post.audioDurationSec}s)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                                }
                            }
                        } else {
                            Text("🔴 ${post.masterclassTime ?: "Live"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.clickable {
                                    upvotedMap = upvotedMap + (post.id to !isUpvoted)
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isUpvoted) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                    contentDescription = "Upvote",
                                    tint = if (isUpvoted) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${post.upvotes + (if (isUpvoted) 1 else 0)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Comment, contentDescription = "Replies", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.repliesCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
