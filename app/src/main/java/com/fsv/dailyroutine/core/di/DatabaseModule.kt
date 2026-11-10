package com.fsv.dailyroutine.core.di

import android.content.Context
import androidx.room.Room
import com.fsv.dailyroutine.feature.habits.data.local.DailyRoutineDatabase
import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitCompletionDao
import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DailyRoutineDatabase =
        Room.databaseBuilder(
            context,
            DailyRoutineDatabase::class.java,
            "daily_routine.db",
        ).build()

    @Provides
    fun provideHabitDao(db: DailyRoutineDatabase): HabitDao = db.habitDao()

    @Provides
    fun provideHabitCompletionDao(db: DailyRoutineDatabase): HabitCompletionDao =
        db.habitCompletionDao()
}