package com.example.appigrejas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "devotionals")
data class DevotionalEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val verse: String
)

@Entity(tableName = "bible_chapters")
data class BibleChapterEntity(
    @PrimaryKey val id: Int,
    val book: String,
    val chapter: Int,
    val content: String
)

@Dao
interface DevotionalDao {
    @Query("SELECT * FROM devotionals ORDER BY date DESC LIMIT 1")
    fun getLatestDevotional(): Flow<DevotionalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevotional(devotional: DevotionalEntity)
}

@Dao
interface BibleDao {
    @Query("SELECT * FROM bible_chapters WHERE book = :book AND chapter = :chapter")
    suspend fun getChapter(book: String, chapter: Int): BibleChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: BibleChapterEntity)
}

@Database(entities = [DevotionalEntity::class, BibleChapterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun devotionalDao(): DevotionalDao
    abstract fun bibleDao(): BibleDao
}
