package com.fullcreative.citypulse.core.di

import android.content.Context
import androidx.room.Room
import com.fullcreative.citypulse.data.local.dao.CityEventDao
import com.fullcreative.citypulse.data.local.database.AppDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "city-db"
        ).build()
    }

    @Provides
    fun provideCityEventDao(database: AppDatabase): CityEventDao {
        return database.cityEventDao()
    }

}