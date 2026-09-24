package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.FluencyEmerald
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import kotlin.math.sin

@Composable
fun AudioWaveformVisualizer(
    isRecording: Boolean,
    amplitude: Float,
    durationSeconds: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            val barCount = 28
            val totalWidth = size.width
            val barWidth = totalWidth / (barCount * 1.6f)
            val spacing = (totalWidth - (barWidth * barCount)) / (barCount - 1)
            val centerY = size.height / 2f

            for (i in 0 until barCount) {
                val x = i * (barWidth + spacing)
                val normalizedDistFromCenter = 1f - kotlin.math.abs((i - barCount / 2f) / (barCount / 2f))

                val barHeight = if (isRecording) {
                    val baseAmp = amplitude.coerceIn(0.15f, 1.0f)
                    val sinFactor = (sin(waveOffset + i * 0.4f) + 1.2f) / 2.2f
                    (size.height * 0.85f * baseAmp * sinFactor * (0.4f + normalizedDistFromCenter * 0.6f))
                        .coerceAtLeast(6.dp.toPx())
                } else {
                    (8.dp.toPx() + (sin(waveOffset * 0.5f + i * 0.3f) * 3.dp.toPx()))
                }

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = if (isRecording) {
                            listOf(SecondaryCyan, PrimaryIndigo, FluencyEmerald)
                        } else {
                            listOf(Color(0xFF475569), Color(0xFF334155))
                        },
                        startY = centerY - barHeight / 2f,
                        endY = centerY + barHeight / 2f
                    ),
                    topLeft = Offset(x, centerY - barHeight / 2f),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }

        // Timer badge overlay
        if (isRecording) {
            val mins = (durationSeconds / 60).toInt()
            val secs = (durationSeconds % 60).toInt()
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun Surface(shape: RoundedCornerShape, color: Color, modifier: Modifier, content: @Composable () -> Unit) {
    androidx.compose.material3.Surface(
        shape = shape,
        color = color,
        modifier = modifier,
        content = content
    )
}
