package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AirflowType
import com.example.data.model.MouthShape
import com.example.data.model.PhonemeArticulation
import com.example.data.model.TonguePosition
import com.example.ui.theme.*

@Composable
fun ArticulationVisualizer(
    phoneme: PhonemeArticulation,
    modifier: Modifier = Modifier,
    onPlayAudio: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "airflow_vibe")
    val airflowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "airflow_anim"
    )

    val vocalCordVibe by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vocal_cord_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("articulation_visualizer_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with IPA badge and sound name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryIndigo, SecondaryCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = phoneme.ipa,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Serif
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = phoneme.soundName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (phoneme.isVoiced) FluencyEmerald.copy(alpha = 0.2f) else AccentAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (phoneme.isVoiced) "Voiced (Vibrate Throat)" else "Voiceless (Airflow Only)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (phoneme.isVoiced) FluencyEmerald else AccentAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PrimaryIndigo.copy(alpha = 0.15f))
                        .testTag("play_phoneme_audio_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Hear native pronunciation",
                        tint = PrimaryIndigo
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2D Sagittal Vocal Tract Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkBackground)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    drawSagittalVocalTract(
                        phoneme = phoneme,
                        airflowPhase = airflowPhase,
                        vocalVibe = if (phoneme.isVoiced) vocalCordVibe else 1.0f
                    )
                }

                // Anatomy labels overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("← Lips & Teeth", fontSize = 10.sp, color = DarkTextSecondary)
                    Text("Hard Palate ⌢", fontSize = 10.sp, color = DarkTextSecondary)
                    Text("Vocal Cords ⚡ →", fontSize = 10.sp, color = if (phoneme.isVoiced) FluencyEmerald else DarkTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Physical placement guide
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🎯 Physical Articulation Mechanics",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = phoneme.tips,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "⚠️ Watch out: ${phoneme.commonMistakes}",
                        fontSize = 12.sp,
                        color = AccentRose,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Example words pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Examples:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                phoneme.exampleWords.forEach { word ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryIndigo.copy(alpha = 0.12f),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Text(
                            text = word,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawSagittalVocalTract(
    phoneme: PhonemeArticulation,
    airflowPhase: Float,
    vocalVibe: Float
) {
    val w = size.width
    val h = size.height

    // 1. Draw Hard & Soft Palate (Upper Roof of Mouth)
    val palatePath = Path().apply {
        moveTo(w * 0.15f, h * 0.28f) // Nose & upper lip base
        cubicTo(
            w * 0.25f, h * 0.18f,
            w * 0.50f, h * 0.15f,
            w * 0.70f, h * 0.25f // Soft palate (velum)
        )
        // Back of pharynx down to larynx
        cubicTo(
            w * 0.82f, h * 0.35f,
            w * 0.85f, h * 0.60f,
            w * 0.85f, h * 0.85f
        )
    }

    drawPath(
        path = palatePath,
        color = PalateColor,
        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
    )

    // 2. Draw Upper & Lower Front Teeth
    // Upper incisor
    drawRect(
        color = TeethWhite,
        topLeft = Offset(w * 0.18f, h * 0.30f),
        size = androidx.compose.ui.geometry.Size(8.dp.toPx(), 14.dp.toPx())
    )
    // Lower incisor
    val lowerJawY = when (phoneme.mouthShape) {
        MouthShape.OPEN_RELAXED -> h * 0.55f
        MouthShape.ROUND_OPEN -> h * 0.58f
        MouthShape.WIDE_SPREAD -> h * 0.44f
        MouthShape.LIP_TEETH_CONTACT -> h * 0.46f
        else -> h * 0.48f
    }
    drawRect(
        color = TeethWhite,
        topLeft = Offset(w * 0.18f, lowerJawY),
        size = androidx.compose.ui.geometry.Size(8.dp.toPx(), 14.dp.toPx())
    )

    // 3. Draw Upper & Lower Lips
    val upperLipX = w * 0.12f
    val lowerLipX = when (phoneme.mouthShape) {
        MouthShape.ROUND_TIGHT, MouthShape.ROUND_OPEN -> w * 0.08f // Protuded
        MouthShape.LIP_TEETH_CONTACT -> w * 0.18f // In contact with upper teeth
        else -> w * 0.12f
    }

    // Upper lip
    drawCircle(
        color = TonguePink.copy(alpha = 0.8f),
        radius = 7.dp.toPx(),
        center = Offset(upperLipX, h * 0.30f)
    )
    // Lower lip
    drawCircle(
        color = TonguePink.copy(alpha = 0.8f),
        radius = 7.dp.toPx(),
        center = Offset(lowerLipX, lowerJawY + 10.dp.toPx())
    )

    // 4. Draw Animated Dynamic Tongue
    val tonguePath = Path().apply {
        // Base of tongue in pharynx
        moveTo(w * 0.72f, h * 0.78f)

        // Tongue body position depends on TonguePosition enum
        when (phoneme.tonguePosition) {
            TonguePosition.INTERDENTAL -> {
                // Tip extends between teeth!
                cubicTo(
                    w * 0.55f, h * 0.50f,
                    w * 0.35f, h * 0.42f,
                    w * 0.13f, h * 0.38f // Between teeth
                )
                // Bottom of tongue curve
                cubicTo(
                    w * 0.28f, h * 0.58f,
                    w * 0.50f, h * 0.75f,
                    w * 0.65f, h * 0.82f
                )
            }
            TonguePosition.ALVEOLAR_RIDGE -> {
                // Tip touches ridge right behind upper teeth
                cubicTo(
                    w * 0.55f, h * 0.45f,
                    w * 0.35f, h * 0.30f,
                    w * 0.22f, h * 0.28f // Ridge touch
                )
                cubicTo(
                    w * 0.32f, h * 0.55f,
                    w * 0.50f, h * 0.75f,
                    w * 0.65f, h * 0.82f
                )
            }
            TonguePosition.RETROFLEX_CURL -> {
                // American R: Tongue curled back
                cubicTo(
                    w * 0.55f, h * 0.52f,
                    w * 0.40f, h * 0.40f,
                    w * 0.32f, h * 0.26f // Tip curled up
                )
                cubicTo(
                    w * 0.38f, h * 0.58f,
                    w * 0.50f, h * 0.75f,
                    w * 0.65f, h * 0.82f
                )
            }
            TonguePosition.PALATAL -> {
                // High arch against hard palate
                cubicTo(
                    w * 0.60f, h * 0.38f,
                    w * 0.42f, h * 0.24f, // Arching near roof
                    w * 0.26f, h * 0.35f
                )
                cubicTo(
                    w * 0.32f, h * 0.55f,
                    w * 0.50f, h * 0.75f,
                    w * 0.65f, h * 0.82f
                )
            }
            TonguePosition.VELAR_BACK -> {
                // Back of tongue elevated to touch soft palate (velum)
                cubicTo(
                    w * 0.65f, h * 0.26f, // Touches soft palate
                    w * 0.45f, h * 0.55f,
                    w * 0.28f, h * 0.50f
                )
                cubicTo(
                    w * 0.32f, h * 0.62f,
                    w * 0.50f, h * 0.75f,
                    w * 0.65f, h * 0.82f
                )
            }
            TonguePosition.LOW_FRONT -> {
                // Low flat tongue for /æ/
                cubicTo(
                    w * 0.55f, h * 0.65f,
                    w * 0.38f, h * 0.62f,
                    w * 0.22f, h * 0.55f
                )
                cubicTo(
                    w * 0.32f, h * 0.68f,
                    w * 0.50f, h * 0.78f,
                    w * 0.65f, h * 0.82f
                )
            }
        }
        close()
    }

    drawPath(
        path = tonguePath,
        brush = Brush.radialGradient(
            colors = listOf(TonguePink, Color(0xFFE11D48)),
            center = Offset(w * 0.45f, h * 0.55f),
            radius = w * 0.4f
        )
    )
    drawPath(
        path = tonguePath,
        color = Color(0xFFBE123C),
        style = Stroke(width = 2.dp.toPx())
    )

    // 5. Draw Vocal Cords Vibration (Larynx)
    val larynxCenter = Offset(w * 0.80f, h * 0.82f)
    if (phoneme.isVoiced) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(FluencyEmerald.copy(alpha = 0.8f), Color.Transparent),
                center = larynxCenter,
                radius = 18.dp.toPx() * vocalVibe
            ),
            radius = 18.dp.toPx() * vocalVibe,
            center = larynxCenter
        )
        drawCircle(
            color = FluencyEmerald,
            radius = 6.dp.toPx(),
            center = larynxCenter
        )
    } else {
        drawCircle(
            color = DarkTextTertiary,
            radius = 4.dp.toPx(),
            center = larynxCenter
        )
    }

    // 6. Draw Animated Airflow Stream
    for (i in 0..4) {
        val t = (airflowPhase + i * 0.2f) % 1f
        val startX = w * 0.75f - (t * w * 0.65f)
        val airY = when (phoneme.airflowType) {
            AirflowType.NASAL_RESONANCE -> h * 0.20f + (kotlin.math.sin(t * 6f) * 8f)
            else -> h * 0.35f + (kotlin.math.sin(t * 4f) * 10f)
        }

        if (startX in (w * 0.05f)..(w * 0.80f)) {
            drawCircle(
                color = AirflowCyan.copy(alpha = (1f - t).coerceIn(0.1f, 0.9f)),
                radius = (3.dp.toPx() * (1f - t * 0.5f)),
                center = Offset(startX, airY)
            )
        }
    }
}
