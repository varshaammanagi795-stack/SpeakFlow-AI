package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.MicroLesson
import com.example.ui.theme.*

@Composable
fun MicroLessonsScreen(
    viewModel: MainViewModel
) {
    var selectedLesson by remember { mutableStateOf(viewModel.repository.microLessons.first()) }
    var selectedQuizOption by remember { mutableStateOf<Int?>(null) }
    var quizSubmitted by remember { mutableStateOf(false) }

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
                    text = "⚡ 5-Minute Daily Micro-Lessons",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Bite-sized daily drills across idioms, phrasal verbs, accent reduction, and rhythm shadowing.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Lesson selector row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.repository.microLessons) { lesson ->
                    val isSelected = lesson.id == selectedLesson.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable {
                            selectedLesson = lesson
                            selectedQuizOption = null
                            quizSubmitted = false
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = lesson.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${lesson.category} · ${lesson.targetCEFR.name.take(2)}",
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Active Lesson Overview & Formula Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_micro_lesson_card"),
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
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryIndigo.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = selectedLesson.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text("⏱️ 5 mins · +25 XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FluencyEmerald)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = selectedLesson.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedLesson.overview,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Formula Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedLesson.formulaOrRule,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SecondaryCyan
                            )
                        }
                    }
                }
            }
        }

        // Real-World Dialogues
        item {
            Text(
                text = "💬 Native Example Dialogues",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(selectedLesson.exampleDialogues) { dialogue ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "${dialogue.speakerA}: \"${dialogue.textA}\"",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${dialogue.speakerB}: \"${dialogue.textB}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 ${dialogue.explanation}",
                        fontSize = 11.sp,
                        color = PrimaryIndigo,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Shadowing Audio Drills
        item {
            Text(
                text = "🎙️ Shadowing & Cadence Repetition",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(selectedLesson.shadowDrills) { drill ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "\"${drill.sentence}\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "IPA: ${drill.phoneticGuide}",
                            fontSize = 11.sp,
                            color = SecondaryCyan
                        )
                        Text(
                            text = "Stress: ${drill.stressGuide}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.ttsManager.speak(drill.sentence, "US", 1.0f, 0.9f)
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PrimaryIndigo.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Play", tint = PrimaryIndigo)
                    }
                }
            }
        }

        // Quick Quiz Section
        if (selectedLesson.quickQuiz.isNotEmpty()) {
            val quiz = selectedLesson.quickQuiz.first()
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🧠 60-Second Comprehension Check", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = quiz.prompt, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        quiz.options.forEachIndexed { idx, opt ->
                            val isChosen = selectedQuizOption == idx
                            val isCorrect = quiz.correctIndex == idx
                            val bgColor = when {
                                quizSubmitted && isCorrect -> FluencyEmerald.copy(alpha = 0.2f)
                                quizSubmitted && isChosen && !isCorrect -> AccentRose.copy(alpha = 0.2f)
                                isChosen -> PrimaryIndigo.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = bgColor,
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        if (!quizSubmitted) selectedQuizOption = idx
                                    }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        if (!quizSubmitted && selectedQuizOption != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { quizSubmitted = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                            ) {
                                Text("Check Answer")
                            }
                        }

                        if (quizSubmitted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "💡 ${quiz.explanation}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedQuizOption == quiz.correctIndex) FluencyEmerald else AccentRose
                            )
                        }
                    }
                }
            }
        }
    }
}
