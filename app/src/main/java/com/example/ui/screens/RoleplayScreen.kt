package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.model.*
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.SpeechMetricsCard
import com.example.ui.theme.*

@Composable
fun RoleplayScreen(
    viewModel: MainViewModel,
    onOpenPhonetics: (PhonemeArticulation) -> Unit
) {
    val currentScenario by viewModel.currentScenario.collectAsStateWithLifecycle()
    val messages by viewModel.roleplayMessages.collectAsStateWithLifecycle()
    val isGeneratingAI by viewModel.isGeneratingAIReply.collectAsStateWithLifecycle()

    val isRecording by viewModel.speechManager.isRecording.collectAsStateWithLifecycle()
    val rmsAmplitude by viewModel.speechManager.rmsAmplitude.collectAsStateWithLifecycle()
    val liveTranscript by viewModel.speechManager.liveTranscript.collectAsStateWithLifecycle()
    val liveMetrics by viewModel.speechManager.liveMetrics.collectAsStateWithLifecycle()

    var manualInputText by remember { mutableStateOf("") }
    var showScenarioSelector by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Request Audio Record permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListeningForRoleplay()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header: Active Scenario & Persona
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).clickable { showScenarioSelector = !showScenarioSelector }
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(currentScenario.aiPersona.avatarColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(currentScenario.aiPersona.accentFlag, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentScenario.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "${currentScenario.aiPersona.name} (${currentScenario.aiPersona.accent} Accent) · ${currentScenario.targetCEFR.name.take(2)}",
                            fontSize = 11.sp,
                            color = PrimaryIndigo,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryIndigo.copy(alpha = 0.15f),
                    modifier = Modifier.clickable { showScenarioSelector = !showScenarioSelector }
                ) {
                    Text(
                        text = "Change",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Expandable Scenario Selector
        AnimatedVisibility(visible = showScenarioSelector) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Select Scenario & AI Persona", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(viewModel.repository.scenarios) { sc ->
                            val isSelected = sc.id == currentScenario.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    viewModel.selectScenario(sc)
                                    showScenarioSelector = false
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(sc.aiPersona.accentFlag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = sc.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = sc.category.title,
                                            fontSize = 9.sp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Suggested Keywords / Objectives Banner
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(currentScenario.suggestedKeywords) { kw ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = "+ \"$kw\"",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubbleItem(
                    message = msg,
                    persona = currentScenario.aiPersona,
                    onPlayAudio = {
                        viewModel.ttsManager.speak(
                            msg.text,
                            currentScenario.aiPersona.accent,
                            currentScenario.aiPersona.speechPitch,
                            currentScenario.aiPersona.speechRate
                        )
                    },
                    onPhonemeClick = {
                        val matchingPhoneme = viewModel.repository.phonemes.firstOrNull() ?: return@MessageBubbleItem
                        onOpenPhonetics(matchingPhoneme)
                    }
                )
            }

            if (isGeneratingAI) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimaryIndigo, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentScenario.aiPersona.name} is speaking & analyzing your cadence...",
                            fontSize = 11.sp,
                            color = PrimaryIndigo,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Real-Time Waveform & Live Transcription Bar
        if (isRecording) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                AudioWaveformVisualizer(
                    isRecording = true,
                    amplitude = rmsAmplitude,
                    durationSeconds = liveMetrics.durationSeconds
                )

                if (liveTranscript.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryIndigo.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text(
                            text = "🗣️ \"$liveTranscript\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryIndigo,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Live Speech Analytics preview
        if (liveMetrics.totalWords > 0) {
            SpeechMetricsCard(
                metrics = liveMetrics,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }

        // Bottom Controls: Mic Recording & Manual Simulation Input
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                // Quick suggested response chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val suggestions = when (currentScenario.id) {
                        "sc_tech_interview" -> listOf(
                            "In my previous role, I led the microservices migration to reduce latency.",
                            "We encountered scalability bottlenecks and addressed them with Redis caching."
                        )
                        "sc_salary_negotiation" -> listOf(
                            "Based on market benchmarks for senior leads, I was anticipating $160,000.",
                            "Could we explore performance equity refreshers alongside base compensation?"
                        )
                        else -> listOf(
                            "I understand your point and would like to propose a structured solution.",
                            "Could you elaborate on the key timeline milestones?"
                        )
                    }
                    items(suggestions) { sugg ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                viewModel.submitManualUserText(sugg)
                            }
                        ) {
                            Text(
                                text = "💡 $sugg",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Text Input
                    OutlinedTextField(
                        value = manualInputText,
                        onValueChange = { manualInputText = it },
                        placeholder = { Text("Speak with mic or type response...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("roleplay_text_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (manualInputText.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        viewModel.submitManualUserText(manualInputText)
                                        manualInputText = ""
                                    },
                                    modifier = Modifier.testTag("send_roleplay_text_button")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimaryIndigo)
                                }
                            }
                        }
                    )

                    // Large Animated Microphone Button
                    val micColor = if (isRecording) AccentRose else PrimaryIndigo
                    Button(
                        onClick = {
                            if (isRecording) {
                                viewModel.stopListeningForRoleplay()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("roleplay_mic_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = micColor),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop" else "Speak",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubbleItem(
    message: RoleplayMessage,
    persona: AIPersona,
    onPlayAudio: () -> Unit,
    onPhonemeClick: () -> Unit
) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(persona.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(persona.accentFlag, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(modifier = Modifier.weight(1f, fill = false)) {
                // Speech Bubble
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    ),
                    color = if (isUser) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (!isUser) CardDefaults.outlinedCardBorder() else null
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )

                        if (!isUser) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable(onClick = onPlayAudio)
                                    .testTag("play_ai_audio_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Listen",
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Listen (${persona.accent} Accent)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                            }
                        }
                    }
                }

                // AI Speech Feedback Card (Attached to User Messages)
                if (isUser && message.feedback != null) {
                    val fb = message.feedback
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💡 AI Speech Coach Feedback", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = FluencyEmerald.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "CEFR ${fb.cefrEstimate.name.take(2)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FluencyEmerald,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Grammar Correction
                            if (!fb.grammarCorrection.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚠️ Fix: ${fb.grammarCorrection}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentRose
                                )
                            }

                            // Natural Native Alternative
                            if (!fb.naturalAlternative.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✨ Native phrasing: \"${fb.naturalAlternative}\"",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SecondaryCyan
                                )
                            }

                            // Pronunciation / Intonation advice
                            if (!fb.pronunciationTip.isNullOrBlank() || !fb.intonationCurve.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🎯 ${fb.pronunciationTip ?: fb.intonationCurve}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = onPhonemeClick,
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("IPA Guide 👄", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
