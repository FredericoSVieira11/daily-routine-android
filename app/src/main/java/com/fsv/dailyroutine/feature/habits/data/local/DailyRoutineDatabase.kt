package com.fsv.dailyroutine.feature.habits.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitCompletionDao
import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitDao
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitCompletionEntity
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCompletionEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class DailyRoutineDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
}