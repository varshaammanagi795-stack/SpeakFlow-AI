package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.SavedVocabEntity
import com.example.ui.theme.*

@Composable
fun VocabDeckScreen(
    viewModel: MainViewModel
) {
    val vocabList by viewModel.savedVocab.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    var newWord by remember { mutableStateOf("") }
    var newIpa by remember { mutableStateOf("") }
    var newPos by remember { mutableStateOf("noun") }
    var newDef by remember { mutableStateOf("") }
    var newEx by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Word / Phrase to Deck", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newWord,
                        onValueChange = { newWord = it },
                        label = { Text("Word or Phrase") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newIpa,
                        onValueChange = { newIpa = it },
                        label = { Text("Phonetic IPA (e.g. /ˈnuːɑːns/)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDef,
                        onValueChange = { newDef = it },
                        label = { Text("Definition") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newEx,
                        onValueChange = { newEx = it },
                        label = { Text("Example Sentence") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newWord.isNotBlank()) {
                            viewModel.saveNewWord(newWord, newIpa, newDef, newEx, newPos, "B2")
                            newWord = ""
                            newIpa = ""
                            newDef = ""
                            newEx = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Save to SRS Deck")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
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
                        text = "📚 Spaced Repetition Vocab Deck",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Supercharge active recall with Leitner spaced repetition intervals and native audio.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Stats & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${vocabList.size} Saved Expressions · 3 Due for Review",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo
                )

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Word", fontSize = 12.sp)
                }
            }
        }

        // Flashcards List
        items(vocabList) { vocab ->
            VocabFlashcardItem(
                vocab = vocab,
                onPlayAudio = {
                    viewModel.ttsManager.speak(vocab.wordOrPhrase, "US", 1.0f, 0.9f)
                },
                onRemembered = {
                    viewModel.updateVocabReview(vocab, true)
                },
                onNeedReview = {
                    viewModel.updateVocabReview(vocab, false)
                }
            )
        }
    }
}

@Composable
private fun VocabFlashcardItem(
    vocab: SavedVocabEntity,
    onPlayAudio: () -> Unit,
    onRemembered: () -> Unit,
    onNeedReview: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vocab.wordOrPhrase,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = vocab.ipa,
                            fontSize = 12.sp,
                            color = SecondaryCyan
                        )
                    }
                    Text(
                        text = "${vocab.partOfSpeech} · CEFR ${vocab.cefrLevel}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PrimaryIndigo.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Play", tint = PrimaryIndigo)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Definition
            Text(
                text = vocab.definition,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            // Example Sentence
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "“${vocab.exampleSentence}”",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryIndigo,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SRS Mastery Bar & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text("Mastery: ${vocab.masteryLevel}% · Interval: ${vocab.intervalDays}d", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { vocab.masteryLevel / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = if (vocab.masteryLevel > 70) FluencyEmerald else AccentAmber
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onNeedReview,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Hard", fontSize = 11.sp, color = AccentRose)
                    }

                    Button(
                        onClick = onRemembered,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FluencyEmerald)
                    ) {
                        Text("Easy ✓", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
