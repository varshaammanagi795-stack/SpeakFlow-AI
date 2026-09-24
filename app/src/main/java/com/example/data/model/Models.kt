package com.example.data.model

enum class CEFRLevel(val label: String, val description: String, val colorHex: Long) {
    A1("A1 - Beginner", "Basic phrases, slow speech, familiar everyday situations", 0xFF64748B),
    A2("A2 - Elementary", "Routine exchanges, simple descriptions of background", 0xFF3B82F6),
    B1("B1 - Intermediate", "Can handle most travel situations, describe experiences & opinions", 0xFF06B6D4),
    B2("B2 - Upper Intermediate", "Fluent spontaneous communication, technical discussions", 0xFF10B981),
    C1("C1 - Advanced", "Flexible & effective language for social, academic & professional purposes", 0xFF8B5CF6),
    C2("C2 - Proficient", "Native-like precision, subtle shades of meaning & idiom master", 0xFFF59E0B)
}

data class AIPersona(
    val id: String,
    val name: String,
    val role: String,
    val accent: String, // "US", "UK", "AU", "CA"
    val accentFlag: String,
    val speechPitch: Float = 1.0f,
    val speechRate: Float = 0.95f,
    val avatarColorHex: Long,
    val bio: String
)

enum class ScenarioCategory(val title: String, val iconEmoji: String) {
    CAREER("Career & Business", "💼"),
    TRAVEL("Travel & Transit", "✈️"),
    DAILY_LIFE("Daily & Social", "☕"),
    ACADEMIC("Academic & Tech", "🎓"),
    HEALTH("Medical & Wellness", "🩺"),
    NEGOTIATION("High-Stakes Talks", "🤝")
}

data class RoleplayScenario(
    val id: String,
    val title: String,
    val category: ScenarioCategory,
    val targetCEFR: CEFRLevel,
    val description: String,
    val initialMessage: String,
    val aiPersona: AIPersona,
    val contextObjectives: List<String>,
    val suggestedKeywords: List<String>
)

data class RoleplayMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val phoneticIPA: String? = null,
    val speechMetrics: SpeechMetrics? = null,
    val feedback: MessageFeedback? = null
)

enum class MessageSender {
    USER, AI, SYSTEM
}

data class MessageFeedback(
    val grammarCorrection: String? = null,
    val originalFlaw: String? = null,
    val naturalAlternative: String? = null,
    val pronunciationTip: String? = null,
    val intonationCurve: String? = null, // "Rising for inquiry", "Falling for assertion"
    val vocabularyUpgrade: String? = null,
    val cefrEstimate: CEFRLevel = CEFRLevel.B1,
    val clarityScore: Int = 85
)

data class SpeechMetrics(
    val wpm: Int = 130, // Words per minute
    val pitchVariationScore: Int = 78, // 0-100
    val fillerWordCount: Int = 0,
    val detectedFillers: List<String> = emptyList(),
    val pronunciationAccuracy: Int = 88, // 0-100
    val fluencyScore: Int = 84, // 0-100
    val clarityScore: Int = 90, // 0-100
    val totalWords: Int = 0,
    val durationSeconds: Float = 0f
)

data class PhonemeArticulation(
    val ipa: String,
    val soundName: String,
    val exampleWords: List<String>,
    val mouthShape: MouthShape,
    val tonguePosition: TonguePosition,
    val isVoiced: Boolean,
    val airflowType: AirflowType,
    val tips: String,
    val commonMistakes: String
)

enum class MouthShape {
    WIDE_SPREAD,     // /iː/ "see"
    OPEN_RELAXED,    // /æ/ "cat", /ʌ/ "cup"
    ROUND_TIGHT,     // /uː/ "too", /w/
    ROUND_OPEN,      // /ɔː/ "thought", /ɒ/ "hot"
    NEUTRAL_REST,    // /ə/ "schwa"
    LIP_TEETH_CONTACT // /f/, /v/
}

enum class TonguePosition {
    ALVEOLAR_RIDGE,   // /t/, /d/, /s/, /z/, /n/, /l/
    INTERDENTAL,      // /θ/ "think", /ð/ "this"
    PALATAL,          // /j/ "yes", /ʃ/ "shoe", /tʃ/ "church"
    VELAR_BACK,       // /k/, /g/, /ŋ/ "sing"
    RETROFLEX_CURL,   // American /r/
    LOW_FRONT         // /æ/ "bat"
}

enum class AirflowType {
    CONTINUOUS_FRICATIVE,
    EXPLOSIVE_PLOSIVE,
    NASAL_RESONANCE,
    LIQUID_FLOW,
    VOWEL_FREE
}

data class PlacementQuestion(
    val id: Int,
    val title: String,
    val prompt: String,
    val subPrompt: String,
    val preparationSeconds: Int = 10,
    val speakingSeconds: Int = 45,
    val focusArea: String // "Fluency & Intro", "Descriptive Grammar", "Argumentation & Complex Structures", "Idioms & Nuance", "Phonetics & Intonation"
)

data class PlacementAssessmentResult(
    val overallCEFR: CEFRLevel,
    val overallScore: Int, // 0-100
    val vocabularyScore: Int, // 0-100
    val grammarScore: Int,
    val fluencyScore: Int,
    val pronunciationScore: Int,
    val coherenceScore: Int,
    val keyStrengths: List<String>,
    val growthAreas: List<String>,
    val personalizedRoadmap: List<String>,
    val summaryFeedback: String
)

data class MicroLesson(
    val id: String,
    val title: String,
    val category: String, // "Phrasal Verbs", "Business Jargon", "Accent & Intonation", "Idioms", "Connected Speech"
    val durationMinutes: Int = 5,
    val targetCEFR: CEFRLevel,
    val overview: String,
    val formulaOrRule: String,
    val exampleDialogues: List<DialoguePair>,
    val shadowDrills: List<ShadowDrill>,
    val quickQuiz: List<QuizQuestion>
)

data class DialoguePair(
    val speakerA: String,
    val textA: String,
    val speakerB: String,
    val textB: String,
    val highlightPhrase: String,
    val explanation: String
)

data class ShadowDrill(
    val sentence: String,
    val phoneticGuide: String,
    val stressGuide: String,
    val tip: String
)

data class QuizQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class PeerPracticeTopic(
    val id: String,
    val title: String,
    val category: String,
    val durationSeconds: Int = 300,
    val icebreakers: List<String>,
    val keyDebatePoints: List<String>,
    val recommendedIdioms: List<String>
)

data class TutorProfile(
    val id: String,
    val name: String,
    val title: String,
    val accent: String,
    val rating: Double,
    val reviewsCount: Int,
    val specialty: String,
    val hourlyRate: String,
    val avatarBgColor: Long,
    val bio: String,
    val isOnline: Boolean = true
)

data class WhiteboardElement(
    val id: String,
    val points: List<androidx.compose.ui.geometry.Offset>,
    val colorHex: Long,
    val strokeWidth: Float,
    val isEraser: Boolean = false,
    val textLabel: String? = null,
    val labelPosition: androidx.compose.ui.geometry.Offset? = null
)

data class CommunityDiscussion(
    val id: String,
    val authorName: String,
    val authorLevel: CEFRLevel,
    val authorAvatarColor: Long,
    val title: String,
    val transcript: String,
    val audioDurationSec: Int,
    val upvotes: Int,
    val repliesCount: Int,
    val topicTag: String,
    val timeAgo: String,
    val isMasterclass: Boolean = false,
    val masterclassTime: String? = null
)

data class UserBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val xpReward: Int,
    val unlockedDate: String? = null
)
