package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpeechMetrics
import com.example.ui.theme.*

@Composable
fun SpeechMetricsCard(
    metrics: SpeechMetrics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("speech_metrics_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Real-Time Speech Analytics",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (metrics.wpm in 110..160) FluencyEmerald.copy(alpha = 0.2f) else AccentAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (metrics.wpm in 110..160) "Optimal Cadence" else if (metrics.wpm < 110) "Too Slow" else "Fast Paced",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (metrics.wpm in 110..160) FluencyEmerald else AccentAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Grid metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricItem(
                    label = "Cadence",
                    value = "${metrics.wpm} WPM",
                    subtext = "Target: 120-150",
                    color = if (metrics.wpm in 110..160) FluencyEmerald else AccentAmber,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = "Pronunciation",
                    value = "${metrics.pronunciationAccuracy}%",
                    subtext = "Phoneme clarity",
                    color = PrimaryIndigo,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = "Fluency",
                    value = "${metrics.fluencyScore}%",
                    subtext = "Flow & rhythm",
                    color = SecondaryCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = "Fillers",
                    value = "${metrics.fillerWordCount}",
                    subtext = if (metrics.fillerWordCount == 0) "Zero fillers! 🎉" else "Detected",
                    color = if (metrics.fillerWordCount == 0) FluencyEmerald else AccentRose,
                    modifier = Modifier.weight(1f)
                )
            }

            // Highlighted Detected Fillers
            if (metrics.detectedFillers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Fillers detected",
                        tint = AccentAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Fillers caught:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    metrics.detectedFillers.distinct().forEach { filler ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AccentRose.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "\"$filler\"",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentRose,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}
