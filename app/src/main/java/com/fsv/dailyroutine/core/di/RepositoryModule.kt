package com.fsv.dailyroutine.core.di

import com.fsv.dailyroutine.feature.habits.data.repository.HabitRepositoryImpl
import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository
}