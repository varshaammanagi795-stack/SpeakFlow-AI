package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.FluencyEmerald
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import kotlin.math.cos
import kotlin.math.sin

data class RadarAxis(
    val label: String,
    val score: Int // 0-100
)

@Composable
fun CEFRRadarChart(
    axes: List<RadarAxis>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRadar(axes)
            }
        }
    }
}

private fun DrawScope.drawRadar(axes: List<RadarAxis>) {
    if (axes.size < 3) return

    val center = Offset(size.width / 2f, size.height / 2f)
    val maxRadius = (minOf(size.width, size.height) / 2f) * 0.72f
    val n = axes.size
    val angleStep = (2 * Math.PI / n).toFloat()

    // 1. Draw Concentric Grid Polygons (25%, 50%, 75%, 100%)
    val rings = listOf(0.25f, 0.5f, 0.75f, 1.0f)
    for (ring in rings) {
        val gridPath = Path()
        for (i in 0 until n) {
            val angle = -Math.PI / 2 + (i * angleStep)
            val x = center.x + (maxRadius * ring * cos(angle)).toFloat()
            val y = center.y + (maxRadius * ring * sin(angle)).toFloat()
            if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
        }
        gridPath.close()
        drawPath(
            path = gridPath,
            color = Color(0xFF475569).copy(alpha = 0.4f),
            style = Stroke(width = 1.dp.toPx())
        )
    }

    // 2. Draw Spokes and Labels
    for (i in 0 until n) {
        val angle = -Math.PI / 2 + (i * angleStep)
        val endX = center.x + (maxRadius * cos(angle)).toFloat()
        val endY = center.y + (maxRadius * sin(angle)).toFloat()

        drawLine(
            color = Color(0xFF475569).copy(alpha = 0.4f),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 1.dp.toPx()
        )

        // Axis Label text
        val labelRadius = maxRadius * 1.22f
        val labelX = center.x + (labelRadius * cos(angle)).toFloat()
        val labelY = center.y + (labelRadius * sin(angle)).toFloat()

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            "${axes[i].label} (${axes[i].score}%)",
            labelX,
            labelY + 8f,
            paint
        )
    }

    // 3. Draw Student Data Polygon
    val dataPath = Path()
    val points = mutableListOf<Offset>()

    for (i in 0 until n) {
        val angle = -Math.PI / 2 + (i * angleStep)
        val ratio = (axes[i].score / 100f).coerceIn(0.1f, 1.0f)
        val x = center.x + (maxRadius * ratio * cos(angle)).toFloat()
        val y = center.y + (maxRadius * ratio * sin(angle)).toFloat()
        val pt = Offset(x, y)
        points.add(pt)
        if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
    }
    dataPath.close()

    // Fill with gradient
    drawPath(
        path = dataPath,
        brush = Brush.radialGradient(
            colors = listOf(PrimaryIndigo.copy(alpha = 0.65f), SecondaryCyan.copy(alpha = 0.25f)),
            center = center,
            radius = maxRadius
        )
    )

    // Outline
    drawPath(
        path = dataPath,
        color = SecondaryCyan,
        style = Stroke(width = 2.5.dp.toPx())
    )

    // Draw vertex dots
    for (pt in points) {
        drawCircle(
            color = Color.White,
            radius = 4.dp.toPx(),
            center = pt
        )
        drawCircle(
            color = PrimaryIndigo,
            radius = 2.5.dp.toPx(),
            center = pt
        )
    }
}
