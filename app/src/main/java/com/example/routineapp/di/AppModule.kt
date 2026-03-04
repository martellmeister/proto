package com.example.routineapp.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.routineapp.data.local.AppDatabase
import com.example.routineapp.data.local.RoutineDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "routine_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideRoutineDao(db: AppDatabase): RoutineDao = db.routineDao()

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}
