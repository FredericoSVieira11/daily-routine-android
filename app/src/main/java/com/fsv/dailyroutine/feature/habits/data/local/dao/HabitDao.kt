package com.fsv.dailyroutine.feature.habits.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitEntity
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): HabitEntity?

    @Query("UPDATE habits SET archived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Transaction
    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY created_at DESC, id DESC")
    fun observeActiveWithCompletions(): Flow<List<HabitWithCompletions>>
}