package com.fsv.dailyroutine.feature.habits.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitCompletionEntity
import java.time.LocalDate

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: HabitCompletionEntity): Long

    @Query("DELETE FROM habit_completions WHERE habit_id = :habitId AND date = :date")
    suspend fun delete(habitId: Long, date: LocalDate)
}