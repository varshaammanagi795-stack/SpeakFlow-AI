package com.example.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.audio.SpeechManager
import com.example.data.audio.TTSManager
import com.example.data.db.*
import com.example.data.model.*
import com.example.data.repository.SpeakFlowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    val repository = SpeakFlowRepository(db.speakFlowDao())

    val speechManager = SpeechManager(application)
    val ttsManager = TTSManager(application)

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Saved Vocab
    val savedVocab: StateFlow<List<SavedVocabEntity>> = repository.savedVocab
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sessions & Badges
    val practiceSessions: StateFlow<List<PracticeSessionEntity>> = repository.practiceSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tutorBookings: StateFlow<List<TutorBookingEntity>> = repository.tutorBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<UserBadgeEntity>> = repository.badges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Roleplay state
    private val _currentScenario = MutableStateFlow<RoleplayScenario>(repository.scenarios.first())
    val currentScenario: StateFlow<RoleplayScenario> = _currentScenario.asStateFlow()

    private val _roleplayMessages = MutableStateFlow<List<RoleplayMessage>>(emptyList())
    val roleplayMessages: StateFlow<List<RoleplayMessage>> = _roleplayMessages.asStateFlow()

    private val _isGeneratingAIReply = MutableStateFlow(false)
    val isGeneratingAIReply: StateFlow<Boolean> = _isGeneratingAIReply.asStateFlow()

    // Placement Test state
    private val _placementStep = MutableStateFlow(0) // 0..4, 5 = results
    val placementStep: StateFlow<Int> = _placementStep.asStateFlow()

    private val _placementAnswers = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val placementAnswers: StateFlow<List<Pair<String, String>>> = _placementAnswers.asStateFlow()

    private val _placementResult = MutableStateFlow<PlacementAssessmentResult?>(null)
    val placementResult: StateFlow<PlacementAssessmentResult?> = _placementResult.asStateFlow()

    private val _isEvaluatingPlacement = MutableStateFlow(false)
    val isEvaluatingPlacement: StateFlow<Boolean> = _isEvaluatingPlacement.asStateFlow()

    // Selected Phoneme for Articulation Visualizer
    private val _selectedPhoneme = MutableStateFlow<PhonemeArticulation>(repository.phonemes.first())
    val selectedPhoneme: StateFlow<PhonemeArticulation> = _selectedPhoneme.asStateFlow()

    // Active Peer Practice Room state
    private val _peerTimeRemaining = MutableStateFlow(300)
    val peerTimeRemaining: StateFlow<Int> = _peerTimeRemaining.asStateFlow()

    private val _peerActiveSpeaker = MutableStateFlow("You")
    val peerActiveSpeaker: StateFlow<String> = _peerActiveSpeaker.asStateFlow()

    // Live Tutoring Simulator state
    private val _tutorTranscription = MutableStateFlow("Tutor: Let's focus on connecting 'walk' and 'through'. Remember to project from your diaphragm.")
    val tutorTranscription: StateFlow<String> = _tutorTranscription.asStateFlow()

    init {
        // Initialize first roleplay
        selectScenario(repository.scenarios.first())
    }

    fun selectScenario(scenario: RoleplayScenario) {
        _currentScenario.value = scenario
        _roleplayMessages.value = listOf(
            RoleplayMessage(
                id = "init_msg",
                sender = MessageSender.AI,
                text = scenario.initialMessage,
                feedback = MessageFeedback(
                    intonationCurve = "Friendly opening cadence",
                    clarityScore = 100
                )
            )
        )
    }

    fun selectPhoneme(phoneme: PhonemeArticulation) {
        _selectedPhoneme.value = phoneme
    }

    fun playPhonemeAudio(phoneme: PhonemeArticulation) {
        val sampleWord = phoneme.exampleWords.firstOrNull() ?: phoneme.soundName
        ttsManager.speak(sampleWord, "US", 1.0f, 0.85f)
    }

    fun startListeningForRoleplay() {
        val persona = _currentScenario.value.aiPersona
        val locale = when (persona.accent) {
            "UK" -> Locale.UK
            "AU" -> Locale("en", "AU")
            "CA" -> Locale.CANADA
            else -> Locale.US
        }

        speechManager.startListening(locale) { spokenText, metrics ->
            if (spokenText.isNotBlank()) {
                handleUserSpokenTurn(spokenText, metrics)
            }
        }
    }

    fun stopListeningForRoleplay() {
        speechManager.stopListening()
    }

    fun submitManualUserText(text: String) {
        if (text.isBlank()) return
        speechManager.handleSimulatedSpokenText(text)
    }

    private fun handleUserSpokenTurn(userText: String, metrics: SpeechMetrics) {
        viewModelScope.launch(Dispatchers.IO) {
            val scenario = _currentScenario.value
            val persona = scenario.aiPersona

            // Add user message immediately
            val userMsg = RoleplayMessage(
                id = "usr_${System.currentTimeMillis()}",
                sender = MessageSender.USER,
                text = userText,
                speechMetrics = metrics
            )

            val updatedList = _roleplayMessages.value + userMsg
            _roleplayMessages.value = updatedList
            _isGeneratingAIReply.value = true

            // Build history summary
            val history = updatedList.takeLast(6).joinToString("\n") {
                "${it.sender.name}: ${it.text}"
            }

            // Call Gemini
            val (aiReply, feedback) = GeminiClient.getRoleplayResponseWithFeedback(
                scenarioTitle = scenario.title,
                personaName = persona.name,
                personaAccent = persona.accent,
                personaRole = persona.role,
                conversationHistory = history,
                userInput = userText
            )

            // Update user message with feedback
            val finalUserMsg = userMsg.copy(feedback = feedback)
            val finalAiMsg = RoleplayMessage(
                id = "ai_${System.currentTimeMillis()}",
                sender = MessageSender.AI,
                text = aiReply
            )

            _roleplayMessages.value = updatedList.dropLast(1) + finalUserMsg + finalAiMsg
            _isGeneratingAIReply.value = false

            // Auto-speak AI reply with persona accent
            ttsManager.speak(aiReply, persona.accent, persona.speechPitch, persona.speechRate)

            // Persist session metrics & award XP
            repository.addXPAndStats(
                xp = 35 + metrics.totalWords,
                fluency = ((userProfile.value?.fluencyIndex ?: 78) * 0.9f + metrics.fluencyScore * 0.1f).toInt(),
                minutes = 1
            )
            repository.saveSession(
                PracticeSessionEntity(
                    scenarioTitle = scenario.title,
                    category = scenario.category.title,
                    durationSeconds = metrics.durationSeconds.toInt(),
                    wpm = metrics.wpm,
                    pronunciationScore = metrics.pronunciationAccuracy,
                    fluencyScore = metrics.fluencyScore,
                    fillerWordCount = metrics.fillerWordCount,
                    keyFeedback = feedback.naturalAlternative ?: "Good cadence.",
                    estimatedCEFR = feedback.cefrEstimate.name
                )
            )
        }
    }

    // Placement test actions
    fun submitPlacementAnswer(questionTitle: String, spokenAnswer: String) {
        val currentAnswers = _placementAnswers.value + Pair(questionTitle, spokenAnswer)
        _placementAnswers.value = currentAnswers

        val nextStep = _placementStep.value + 1
        _placementStep.value = nextStep

        if (nextStep >= repository.placementQuestions.size) {
            // All questions answered, evaluate with Gemini
            evaluatePlacementTest(currentAnswers)
        }
    }

    private fun evaluatePlacementTest(answers: List<Pair<String, String>>) {
        viewModelScope.launch(Dispatchers.IO) {
            _isEvaluatingPlacement.value = true
            val result = GeminiClient.evaluateSpeechPlacement(answers)
            _placementResult.value = result
            _isEvaluatingPlacement.value = false

            // Update user profile
            val current = userProfile.value
            if (current != null) {
                repository.updateProfile(
                    current.copy(
                        currentCEFR = result.overallCEFR.name.take(2),
                        fluencyIndex = result.overallScore,
                        placementTestCompleted = true
                    )
                )
            }
        }
    }

    fun restartPlacement() {
        _placementStep.value = 0
        _placementAnswers.value = emptyList()
        _placementResult.value = null
    }

    fun saveNewWord(word: String, ipa: String, definition: String, example: String, partOfSpeech: String, cefr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveVocab(
                SavedVocabEntity(
                    wordOrPhrase = word,
                    ipa = ipa,
                    partOfSpeech = partOfSpeech,
                    definition = definition,
                    exampleSentence = example,
                    cefrLevel = cefr,
                    masteryLevel = 50
                )
            )
        }
    }

    fun updateVocabReview(vocab: SavedVocabEntity, isRemembered: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newMastery = if (isRemembered) (vocab.masteryLevel + 15).coerceAtMost(100) else (vocab.masteryLevel - 20).coerceAtLeast(10)
            val newInterval = if (isRemembered) vocab.intervalDays * 2 else 1
            val nextReview = System.currentTimeMillis() + (newInterval * 86400000L)
            repository.updateVocabSRS(vocab.id, newMastery, newInterval, nextReview)
        }
    }

    fun bookTutorSession(tutor: TutorProfile, slot: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.bookTutor(
                TutorBookingEntity(
                    tutorId = tutor.id,
                    tutorName = tutor.name,
                    sessionType = tutor.specialty,
                    scheduledTime = slot,
                    durationMinutes = 45,
                    isCompleted = false
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
        ttsManager.shutdown()
    }
}
