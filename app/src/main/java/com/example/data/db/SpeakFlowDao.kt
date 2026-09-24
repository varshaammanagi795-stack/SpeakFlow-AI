package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeakFlowDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET xp = xp + :xpDelta, fluencyIndex = :newFluency, totalMinutesPracticed = totalMinutesPracticed + :minDelta WHERE id = 1")
    suspend fun addXPAndStats(xpDelta: Int, newFluency: Int, minDelta: Int)

    // Saved Vocab
    @Query("SELECT * FROM saved_vocab ORDER BY nextReviewTimestamp ASC")
    fun getAllVocab(): Flow<List<SavedVocabEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocab(vocab: SavedVocabEntity): Long

    @Query("UPDATE saved_vocab SET masteryLevel = :newMastery, reviewCount = reviewCount + 1, intervalDays = :newInterval, nextReviewTimestamp = :nextReview WHERE id = :id")
    suspend fun updateVocabSRS(id: Long, newMastery: Int, newInterval: Int, nextReview: Long)

    @Delete
    suspend fun deleteVocab(vocab: SavedVocabEntity)

    // Practice Sessions
    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PracticeSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSessionEntity)

    // Tutor Bookings
    @Query("SELECT * FROM tutor_bookings ORDER BY id DESC")
    fun getAllTutorBookings(): Flow<List<TutorBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutorBooking(booking: TutorBookingEntity)

    @Query("UPDATE tutor_bookings SET isCompleted = 1, tutorNotes = :notes WHERE id = :id")
    suspend fun completeBooking(id: Long, notes: String)

    // Badges
    @Query("SELECT * FROM user_badges")
    fun getAllBadges(): Flow<List<UserBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<UserBadgeEntity>)

    @Query("UPDATE user_badges SET isUnlocked = 1, unlockedDate = :date WHERE id = :id")
    suspend fun unlockBadge(id: String, date: String)
}
