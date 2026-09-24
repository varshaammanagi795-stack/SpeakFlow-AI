package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TutorProfile
import com.example.ui.components.WhiteboardCanvas
import com.example.ui.theme.*

@Composable
fun LiveTutoringScreen(
    viewModel: MainViewModel
) {
    var isInCall by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOn by remember { mutableStateOf(true) }
    var showWhiteboard by remember { mutableStateOf(true) }
    var selectedTutor by remember { mutableStateOf(viewModel.repository.tutors.first()) }
    var bookingDialogTutor by remember { mutableStateOf<TutorProfile?>(null) }

    val bookings by viewModel.tutorBookings.collectAsStateWithLifecycle()

    if (bookingDialogTutor != null) {
        val tutor = bookingDialogTutor!!
        AlertDialog(
            onDismissRequest = { bookingDialogTutor = null },
            title = { Text("Book 1-on-1 Session with ${tutor.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select time slot for ${tutor.specialty}:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf("Tomorrow at 2:00 PM (GMT-4)", "Thursday at 5:30 PM (GMT-4)", "Saturday at 10:00 AM (GMT-4)").forEach { slot ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryIndigo.copy(alpha = 0.12f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.bookTutorSession(tutor, slot)
                                    bookingDialogTutor = null
                                }
                        ) {
                            Text(
                                text = "📅 $slot",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { bookingDialogTutor = null }) {
                    Text("Cancel")
                }
            }
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
                        text = "👩‍🏫 1-on-1 Live Tutoring",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real-time video coaching, interactive whiteboarding & custom notes.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Video Room Simulator Container
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_tutoring_video_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBackground),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Video Feeds Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isInCall) {
                            // Tutor Video Feed (Simulation)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(PrimaryIndigo, SecondaryCyan)
                                            )
                                        )
                                        .border(2.dp, FluencyEmerald, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedTutor.name.take(1),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(FluencyEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${selectedTutor.name} (Live Audio/Video)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Student PiP Video Box
                            Box(
                                modifier = Modifier
                                    .size(width = 90.dp, height = 65.dp)
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, PrimaryIndigo, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("You (Student)", fontSize = 10.sp, color = Color.White)
                            }

                            // Live Session Timer Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("REC · 14:32", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        } else {
                            // Call Inactive State
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoCall,
                                    contentDescription = null,
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ready to start live session with ${selectedTutor.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = selectedTutor.specialty,
                                    fontSize = 11.sp,
                                    color = DarkTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Transcription Stream
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💬", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isInCall) "Sarah: \"Great sentence structure. Make sure you don't drop the final /t/ in 'project'. Let's write the phonetic rule on the board.\"" else "Live transcription will stream here when call connects.",
                                fontSize = 11.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Call Action Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isMuted) Color(0xFFEF4444) else Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { isVideoOn = !isVideoOn },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isVideoOn) Color(0xFF334155) else Color(0xFFEF4444))
                        ) {
                            Icon(
                                imageVector = if (isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Video",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { showWhiteboard = !showWhiteboard },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (showWhiteboard) PrimaryIndigo else Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Draw,
                                contentDescription = "Whiteboard",
                                tint = Color.White
                            )
                        }

                        Button(
                            onClick = { isInCall = !isInCall },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInCall) Color(0xFFEF4444) else FluencyEmerald
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("toggle_live_call_button")
                        ) {
                            Icon(
                                imageVector = if (isInCall) Icons.Default.CallEnd else Icons.Default.Call,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isInCall) "End Call" else "Join Live Session")
                        }
                    }
                }
            }
        }

        // Shared Interactive Whiteboard
        if (showWhiteboard) {
            item {
                Text(
                    text = "🎨 Interactive Tutor Whiteboard & Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                WhiteboardCanvas()
            }
        }

        // Tutor Notes & Homework Builder
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📝 Lesson Notes & Homework Builder",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Homework: Record 3 audio samples of 'circumlocution' and 'walk through' in the Speech Lab.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "• Target Focus: Linking /r/ sound between vowel boundaries.",
                        fontSize = 12.sp,
                        color = PrimaryIndigo,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Tutor Catalog & Schedule Bookings
        item {
            Text(
                text = "📅 Certified Native Tutors & Schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(viewModel.repository.tutors) { tutor ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(tutor.avatarBgColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(tutor.name.take(1), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tutor.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("⭐ ${tutor.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                            }
                            Text(
                                text = tutor.title,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${tutor.accent} · ${tutor.hourlyRate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryIndigo
                            )
                        }
                    }

                    Button(
                        onClick = {
                            selectedTutor = tutor
                            bookingDialogTutor = tutor
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
