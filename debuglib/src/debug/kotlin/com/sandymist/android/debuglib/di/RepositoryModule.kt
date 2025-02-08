package com.sandymist.android.debuglib.di

import com.sandymist.android.debuglib.repository.LogcatRepository
import com.sandymist.android.debuglib.repository.LogcatRepositoryImpl
import com.sandymist.android.debuglib.repository.NetworkLogRepository
import com.sandymist.android.debuglib.repository.NetworkLogRepositoryImpl
import com.sandymist.android.debuglib.repository.PreferencesRepository
import com.sandymist.android.debuglib.repository.PreferencesRepositoryImpl
import com.sandymist.android.debuglib.repository.RoomDBRepository
import com.sandymist.android.debuglib.repository.RoomDBRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindLogcatRepository(logcatRepositoryImpl: LogcatRepositoryImpl): LogcatRepository

    @Binds
    abstract fun bindRoomDBRepository(roomDBRepositoryImpl: RoomDBRepositoryImpl): RoomDBRepository

    @Binds
    abstract fun bindPreferencesRepository(preferencesRepositoryImpl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    abstract fun bindNetworkLogRepository(networkLogRepositoryImpl: NetworkLogRepositoryImpl): NetworkLogRepository
}
