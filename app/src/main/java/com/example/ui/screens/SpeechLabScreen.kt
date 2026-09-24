package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ArticulationVisualizer
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.SpeechMetricsCard
import com.example.ui.theme.*

@Composable
fun SpeechLabScreen(
    viewModel: MainViewModel
) {
    val selectedPhoneme by viewModel.selectedPhoneme.collectAsStateWithLifecycle()
    val isRecording by viewModel.speechManager.isRecording.collectAsStateWithLifecycle()
    val rmsAmplitude by viewModel.speechManager.rmsAmplitude.collectAsStateWithLifecycle()
    val liveTranscript by viewModel.speechManager.liveTranscript.collectAsStateWithLifecycle()
    val liveMetrics by viewModel.speechManager.liveMetrics.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.speechManager.startListening { _, _ -> }
        }
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
            Column {
                Text(
                    text = "👄 Speech & Articulation Lab",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Master tricky English phonemes with 2D mouth cross-section and real-time speech telemetry.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Phoneme Selector Horizontal Chips
        item {
            Text(
                text = "Select Target IPA Phoneme",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.repository.phonemes) { phoneme ->
                    val isSelected = phoneme.ipa == selectedPhoneme.ipa
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { viewModel.selectPhoneme(phoneme) }
                            .testTag("phoneme_chip_${phoneme.ipa.filter { it.isLetterOrDigit() }}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = phoneme.ipa,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = phoneme.soundName.take(14),
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2D Sagittal Mouth & Tongue Articulation Visualizer Card
        item {
            ArticulationVisualizer(
                phoneme = selectedPhoneme,
                onPlayAudio = { viewModel.playPhonemeAudio(selectedPhoneme) }
            )
        }

        // Live Speech Telemetry Sandbox
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
                                text = "🎙️ Live Speech Sandbox",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Read aloud the sample words or free speak to test cadence and fillers.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                if (isRecording) {
                                    viewModel.speechManager.stopListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) AccentRose else PrimaryIndigo
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("speech_lab_record_button")
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRecording) "Stop" else "Test Mic")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
                                text = "🗣️ Spoken: \"$liveTranscript\"",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SpeechMetricsCard(metrics = liveMetrics)
                }
            }
        }
    }
}
