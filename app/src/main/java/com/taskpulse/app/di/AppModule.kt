package com.taskpulse.app.di

import android.content.Context
import androidx.room.Room
import com.taskpulse.app.data.local.AppDatabase
import com.taskpulse.app.data.repository.AppUsageRepositoryImpl
import com.taskpulse.app.domain.repository.IAppUsageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindAppUsageRepository(
        repository: AppUsageRepositoryImpl
    ): IAppUsageRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "taskpulse_database"
            ).fallbackToDestructiveMigration().build()
        }

        @Provides
        @Singleton
        fun provideUsageDao(database: AppDatabase) = database.usageStatDao()
    }
}
