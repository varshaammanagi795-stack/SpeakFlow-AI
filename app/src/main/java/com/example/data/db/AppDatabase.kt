package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        SavedVocabEntity::class,
        PracticeSessionEntity::class,
        TutorBookingEntity::class,
        UserBadgeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun speakFlowDao(): SpeakFlowDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "speakflow_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.speakFlowDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: SpeakFlowDao) {
                // Initial user
                dao.insertOrUpdateProfile(
                    UserProfileEntity(
                        id = 1,
                        name = "Alex Rivera",
                        email = "alex.learner@example.com",
                        currentCEFR = "B2",
                        targetCEFR = "C1",
                        xp = 1850,
                        streakDays = 14,
                        fluencyIndex = 82,
                        totalWordsSpoken = 5620,
                        totalMinutesPracticed = 210,
                        preferredAccent = "US",
                        placementTestCompleted = true
                    )
                )

                // Initial Vocab Deck
                val initialVocab = listOf(
                    SavedVocabEntity(
                        wordOrPhrase = "Circumlocution",
                        ipa = "/ˌsɜːkəmloʊˈkjuːʃən/",
                        partOfSpeech = "noun",
                        definition = "The use of many words where fewer would do, especially in a deliberate attempt to be vague.",
                        exampleSentence = "He used clever circumlocution to avoid answering the tough salary question directly.",
                        cefrLevel = "C1",
                        masteryLevel = 75,
                        reviewCount = 3
                    ),
                    SavedVocabEntity(
                        wordOrPhrase = "Touch base",
                        ipa = "/tʌtʃ beɪs/",
                        partOfSpeech = "idiom",
                        definition = "Briefly make contact or reconnect with someone.",
                        exampleSentence = "Let's touch base next Monday after you review the product specification.",
                        cefrLevel = "B2",
                        masteryLevel = 90,
                        reviewCount = 5
                    ),
                    SavedVocabEntity(
                        wordOrPhrase = "Nuance",
                        ipa = "/ˈnuːɑːns/",
                        partOfSpeech = "noun",
                        definition = "A subtle difference in meaning, opinion, or sound.",
                        exampleSentence = "Native speakers pick up on the subtle emotional nuance in intonation.",
                        cefrLevel = "B2",
                        masteryLevel = 60,
                        reviewCount = 2
                    ),
                    SavedVocabEntity(
                        wordOrPhrase = "Pivotal",
                        ipa = "/ˈpɪvətl/",
                        partOfSpeech = "adjective",
                        definition = "Of crucial importance in relation to the development or success of something else.",
                        exampleSentence = "Clear articulation played a pivotal role in her successful conference keynote.",
                        cefrLevel = "C1",
                        masteryLevel = 80,
                        reviewCount = 4
                    ),
                    SavedVocabEntity(
                        wordOrPhrase = "Bring up",
                        ipa = "/brɪŋ ʌp/",
                        partOfSpeech = "phrasal verb",
                        definition = "To mention a topic during a conversation or meeting.",
                        exampleSentence = "I wanted to bring up our budget timeline in today's standup.",
                        cefrLevel = "B1",
                        masteryLevel = 95,
                        reviewCount = 6
                    )
                )
                initialVocab.forEach { dao.insertVocab(it) }

                // Initial Badges
                val badges = listOf(
                    UserBadgeEntity("b_streak_7", "7-Day Streak Master", "Maintained consistent speech practice for 7 consecutive days", "🔥", true, "2026-09-20"),
                    UserBadgeEntity("b_filler_slayer", "Filler Word Slayer", "Spoke for 2 continuous minutes with zero 'um' or 'like' fillers", "🗡️", true, "2026-09-22"),
                    UserBadgeEntity("b_cefr_b2", "CEFR B2 Certified", "Achieved Upper-Intermediate level in the automated speech assessment", "🎓", true, "2026-09-23"),
                    UserBadgeEntity("b_accent_guru", "Phonetics Virtuoso", "Mastered 10 tricky IPA consonant clusters and vowel pairs", "🎯", false),
                    UserBadgeEntity("b_negotiator", "Master Negotiator", "Successfully completed the Executive Salary Negotiation Roleplay", "💼", false),
                    UserBadgeEntity("b_peer_champion", "Global Peer Champion", "Completed 5 peer-to-peer 5-minute conversation rooms", "🌍", false)
                )
                dao.insertBadges(badges)

                // Initial Sessions
                dao.insertSession(
                    PracticeSessionEntity(
                        scenarioTitle = "Tech Architecture Q&A",
                        category = "Career & Business",
                        durationSeconds = 180,
                        wpm = 138,
                        pronunciationScore = 91,
                        fluencyScore = 86,
                        fillerWordCount = 2,
                        keyFeedback = "Excellent technical lexicon. Softened alveolar /t/ sound into American flap /ɾ/ naturally.",
                        estimatedCEFR = "B2"
                    )
                )
                dao.insertSession(
                    PracticeSessionEntity(
                        scenarioTitle = "Airport Baggage Claim & Customs",
                        category = "Travel & Transit",
                        durationSeconds = 145,
                        wpm = 125,
                        pronunciationScore = 88,
                        fluencyScore = 82,
                        fillerWordCount = 1,
                        keyFeedback = "Clear declarative intonation. Practiced polite modals 'Could you please assist...'.",
                        estimatedCEFR = "B1"
                    )
                )

                // Initial Tutor Booking
                dao.insertTutorBooking(
                    TutorBookingEntity(
                        tutorId = "tutor_1",
                        tutorName = "Sarah Jenkins, M.Ed.",
                        sessionType = "1-on-1 Accent Reduction & Executive Presentation",
                        scheduledTime = "Tomorrow at 4:00 PM (GMT-4)",
                        durationMinutes = 45,
                        isCompleted = false,
                        tutorNotes = "Preparation: Prepare 2-minute elevator pitch and focus on linking /r/ and vowel length."
                    )
                )
            }
        }
    }
}
