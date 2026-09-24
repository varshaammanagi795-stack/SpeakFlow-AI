package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Rivera",
    val email: String = "alex.learner@example.com",
    val currentCEFR: String = "B2",
    val targetCEFR: String = "C1",
    val xp: Int = 1420,
    val streakDays: Int = 12,
    val fluencyIndex: Int = 78,
    val totalWordsSpoken: Int = 4280,
    val totalMinutesPracticed: Int = 185,
    val preferredAccent: String = "US",
    val placementTestCompleted: Boolean = true,
    val isTutorMode: Boolean = false
)

@Entity(tableName = "saved_vocab")
data class SavedVocabEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wordOrPhrase: String,
    val ipa: String,
    val partOfSpeech: String,
    val definition: String,
    val exampleSentence: String,
    val cefrLevel: String = "B2",
    val masteryLevel: Int = 50, // 0-100%
    val reviewCount: Int = 1,
    val nextReviewTimestamp: Long = System.currentTimeMillis() + 86400000L,
    val intervalDays: Int = 1
)

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val scenarioTitle: String,
    val category: String,
    val durationSeconds: Int,
    val wpm: Int,
    val pronunciationScore: Int,
    val fluencyScore: Int,
    val fillerWordCount: Int,
    val keyFeedback: String,
    val estimatedCEFR: String
)

@Entity(tableName = "tutor_bookings")
data class TutorBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tutorId: String,
    val tutorName: String,
    val sessionType: String, // "1-on-1 Accent Reduction", "Mock Tech Interview"
    val scheduledTime: String,
    val durationMinutes: Int = 45,
    val isCompleted: Boolean = false,
    val tutorNotes: String = ""
)

@Entity(tableName = "user_badges")
data class UserBadgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val unlockedDate: String? = null
)
