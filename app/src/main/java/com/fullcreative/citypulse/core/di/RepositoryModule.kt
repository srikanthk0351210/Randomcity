package com.fullcreative.citypulse.core.di


import com.fullcreative.citypulse.data.local.dao.CityEventDao
import com.fullcreative.citypulse.data.local.repository.CityEventRepositoryImpl
import com.fullcreative.citypulse.domain.repository.CityEventRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCityEventRepository(dao: CityEventDao): CityEventRepository {
        return CityEventRepositoryImpl(dao)
    }

}
