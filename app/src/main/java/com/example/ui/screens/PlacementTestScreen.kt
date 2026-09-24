package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.CEFRRadarChart
import com.example.ui.components.RadarAxis
import com.example.ui.theme.*

@Composable
fun PlacementTestScreen(
    viewModel: MainViewModel,
    onFinishAndGoDashboard: () -> Unit
) {
    val step by viewModel.placementStep.collectAsStateWithLifecycle()
    val result by viewModel.placementResult.collectAsStateWithLifecycle()
    val isEvaluating by viewModel.isEvaluatingPlacement.collectAsStateWithLifecycle()

    val isRecording by viewModel.speechManager.isRecording.collectAsStateWithLifecycle()
    val rmsAmplitude by viewModel.speechManager.rmsAmplitude.collectAsStateWithLifecycle()
    val liveTranscript by viewModel.speechManager.liveTranscript.collectAsStateWithLifecycle()
    val liveMetrics by viewModel.speechManager.liveMetrics.collectAsStateWithLifecycle()

    var manualSpokenText by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.speechManager.startListening { _, _ -> }
        }
    }

    val questions = viewModel.repository.placementQuestions
    val currentQuestion = questions.getOrNull(step)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "🎯 Automated CEFR Speech Placement",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "3-minute multi-dimensional speech evaluation assessing grammar, vocabulary, cadence, and phonetics.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (result == null && !isEvaluating && currentQuestion != null) {
            // Active Question Step
            item {
                // Step Progress Indicator
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Question ${step + 1} of ${questions.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                        Text(currentQuestion.focusArea, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (step + 1) / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryIndigo,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Question Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryIndigo.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "TASK ${step + 1}: ${currentQuestion.title.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentQuestion.prompt,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "💡 Examiner Focus: ${currentQuestion.subPrompt}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Audio Waveform
                        AudioWaveformVisualizer(
                            isRecording = isRecording,
                            amplitude = rmsAmplitude,
                            durationSeconds = liveMetrics.durationSeconds
                        )

                        if (liveTranscript.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimaryIndigo.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🗣️ \"$liveTranscript\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryIndigo,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Mic / Simulation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (isRecording) {
                                        viewModel.speechManager.stopListening()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("placement_record_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecording) AccentRose else PrimaryIndigo
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isRecording) "Stop Speaking" else "Record Response")
                            }

                            Button(
                                onClick = {
                                    val textToSubmit = if (liveTranscript.isNotBlank()) liveTranscript else {
                                        when (step) {
                                            0 -> "My name is Alex. I am a software engineer focused on distributed systems, and I want to master high-stakes executive communication in English."
                                            1 -> "Recently, our team faced a severe database bottleneck before black friday. I organized an emergency incident room and migrated the high-frequency reads to Redis."
                                            2 -> "In my opinion, remote work provides undeniable productivity benefits; nevertheless, hybrid touchpoints remain crucial for serendipitous innovation."
                                            3 -> "If I had one million dollars, I would establish an open-source decentralized education hub delivering localized AI tutors to underprivileged students."
                                            else -> "Although the innovative architectural methodology seemed thoroughly overwhelming, their enthusiastic collaboration produced phenomenal breakthroughs."
                                        }
                                    }
                                    viewModel.submitPlacementAnswer(currentQuestion.title, textToSubmit)
                                },
                                modifier = Modifier.testTag("placement_submit_next_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FluencyEmerald)
                            ) {
                                Text("Next Task →", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else if (isEvaluating) {
            // Evaluating Loader
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryIndigo, strokeWidth = 4.dp, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AI CEFR Examiner is Analyzing Your Speech...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Evaluating lexical richness, syntax variety, phonetic stress, and spontaneous coherence.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (result != null) {
            // Comprehensive Result View
            val res = result!!
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("placement_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OFFICIAL PLACEMENT RESULT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color(0xFFF59E0B)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(PrimaryIndigo, Color(0xFF9333EA))
                                    )
                                )
                                .padding(horizontal = 24.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res.overallCEFR.label,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Composite Speech Score: ${res.overallScore} / 100",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryCyan
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = res.summaryFeedback,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Radar Breakdown Chart
            item {
                Text(
                    text = "📊 CEFR Multi-Dimensional Radar Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                CEFRRadarChart(
                    axes = listOf(
                        RadarAxis("Vocabulary", res.vocabularyScore),
                        RadarAxis("Grammar", res.grammarScore),
                        RadarAxis("Fluency", res.fluencyScore),
                        RadarAxis("Pronunciation", res.pronunciationScore),
                        RadarAxis("Coherence", res.coherenceScore)
                    )
                )
            }

            // Key Strengths & Growth Areas
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("💪 Key Strengths", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FluencyEmerald)
                            Spacer(modifier = Modifier.height(6.dp))
                            res.keyStrengths.forEach { s ->
                                Text("• $s", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🎯 Growth Areas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                            Spacer(modifier = Modifier.height(6.dp))
                            res.growthAreas.forEach { g ->
                                Text("• $g", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }

            // Personalized Learning Roadmap
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🗺️ Personalized Accelerated Roadmap", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                        Spacer(modifier = Modifier.height(8.dp))
                        res.personalizedRoadmap.forEachIndexed { i, stepDesc ->
                            Text(
                                text = "${i + 1}. $stepDesc",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.restartPlacement() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Retake Test")
                            }

                            Button(
                                onClick = onFinishAndGoDashboard,
                                modifier = Modifier.weight(1f).testTag("save_placement_plan_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                            ) {
                                Text("Start Study Plan")
                            }
                        }
                    }
                }
            }
        }
    }
}
