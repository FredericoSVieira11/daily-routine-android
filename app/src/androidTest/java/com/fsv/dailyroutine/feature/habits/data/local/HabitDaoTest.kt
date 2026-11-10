package com.fsv.dailyroutine.feature.habits.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitCompletionDao
import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitDao
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitCompletionEntity
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var db: DailyRoutineDatabase
    private lateinit var habitDao: HabitDao
    private lateinit var completionDao: HabitCompletionDao

    private val date = LocalDate.of(2026, 11, 10)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DailyRoutineDatabase::class.java).build()
        habitDao = db.habitDao()
        completionDao = db.habitCompletionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun newHabit(name: String = "Run") = HabitEntity(
        name = name,
        description = null,
        createdAt = date,
        reminderTime = null,
        archived = false,
    )

    @Test
    fun duplicateCompletionOnSameDayIsIgnored() = runTest {
        val habitId = habitDao.insert(newHabit())
        completionDao.insert(HabitCompletionEntity(habitId = habitId, date = date))

        val secondInsert = completionDao.insert(HabitCompletionEntity(habitId = habitId, date = date))

        assertEquals(-1L, secondInsert)
        val completions = habitDao.observeActiveWithCompletions().first().single().completions
        assertEquals(1, completions.size)
    }

    @Test
    fun archivedHabitsAreNotObserved() = runTest {
        val id = habitDao.insert(newHabit())

        habitDao.archive(id)

        assertTrue(habitDao.observeActiveWithCompletions().first().isEmpty())
    }

    @Test
    fun deletingCompletionRemovesItFromRelation() = runTest {
        val habitId = habitDao.insert(newHabit())
        completionDao.insert(HabitCompletionEntity(habitId = habitId, date = date))

        completionDao.delete(habitId, date)

        val habit = habitDao.observeActiveWithCompletions().first().single()
        assertTrue(habit.completions.isEmpty())
    }
}