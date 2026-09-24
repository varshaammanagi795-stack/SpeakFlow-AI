package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.FluencyEmerald
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan

data class DrawingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

data class TextAnnotation(
    val text: String,
    val position: Offset,
    val color: Color
)

@Composable
fun WhiteboardCanvas(
    modifier: Modifier = Modifier,
    initialAnnotations: List<TextAnnotation> = listOf(
        TextAnnotation("Linking /r/: 'clear‿understanding'", Offset(40f, 60f), SecondaryCyan),
        TextAnnotation("Intonation: Rising ↗ on 'Really?'", Offset(40f, 110f), FluencyEmerald)
    )
) {
    var strokes by remember { mutableStateOf(listOf<DrawingStroke>()) }
    var currentPoints by remember { mutableStateOf(listOf<Offset>()) }
    var selectedColor by remember { mutableStateOf(PrimaryIndigo) }
    var isEraserMode by remember { mutableStateOf(false) }
    var strokeWidth by remember { mutableStateOf(6f) }
    var annotations by remember { mutableStateOf(initialAnnotations) }

    val colorPalette = listOf(
        PrimaryIndigo,
        SecondaryCyan,
        FluencyEmerald,
        Color(0xFFF59E0B), // Amber
        Color(0xFFEF4444), // Red
        Color(0xFFFFFFFF)  // White
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("interactive_whiteboard"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkBackground
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Whiteboard Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🎨 Live Tutor Whiteboard",
                        fontSize = 11.sp,
                        color = Color.White
                    )

                    // Color picks
                    colorPalette.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    if (selectedColor == color && !isEraserMode) 2.dp else 0.dp,
                                    Color.White,
                                    CircleShape
                                )
                                .pointerInput(Unit) {
                                    selectedColor = color
                                    isEraserMode = false
                                }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { isEraserMode = !isEraserMode },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Eraser",
                            tint = if (isEraserMode) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            strokes = emptyList()
                            annotations = emptyList()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear board",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Canvas drawing area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(isEraserMode, selectedColor, strokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPoints = currentPoints + change.position
                            },
                            onDragEnd = {
                                if (currentPoints.isNotEmpty()) {
                                    strokes = strokes + DrawingStroke(
                                        points = currentPoints,
                                        color = if (isEraserMode) DarkBackground else selectedColor,
                                        strokeWidth = if (isEraserMode) 24f else strokeWidth,
                                        isEraser = isEraserMode
                                    )
                                    currentPoints = emptyList()
                                }
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw saved strokes
                    for (stroke in strokes) {
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                moveTo(stroke.points.first().x, stroke.points.first().y)
                                for (i in 1 until stroke.points.size) {
                                    lineTo(stroke.points[i].x, stroke.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(
                                    width = stroke.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // Draw current drawing stroke
                    if (currentPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(currentPoints.first().x, currentPoints.first().y)
                            for (i in 1 until currentPoints.size) {
                                lineTo(currentPoints[i].x, currentPoints[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = if (isEraserMode) DarkBackground else selectedColor,
                            style = Stroke(
                                width = if (isEraserMode) 24f else strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    // Draw tutor annotations
                    for (ann in annotations) {
                        val paint = android.graphics.Paint().apply {
                            color = ann.color.hashCode()
                            textSize = 32f
                            isAntiAlias = true
                            isFakeBoldText = true
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            ann.text,
                            ann.position.x,
                            ann.position.y,
                            paint
                        )
                    }
                }
            }
        }
    }
}
