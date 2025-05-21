package com.sandymist.android.debuglib.di

import android.content.Context
import com.sandymist.android.debuglib.db.DebugLibDatabase
import com.sandymist.android.debuglib.db.LogcatDao
import com.sandymist.android.debuglib.db.MockDao
import com.sandymist.android.debuglib.db.PreferencesDao
import com.sandymist.android.debuglib.model.MyObjectBox
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.objectbox.BoxStore
import java.util.concurrent.CompletableFuture
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@AppContext context: Context): DebugLibDatabase {
        return DebugLibDatabase.getDatabase(context)
    }

    @Provides
    fun provideLogcatDao(database: DebugLibDatabase): LogcatDao =
        database.logcatDao()

    @Provides
    fun provideMockDao(database: DebugLibDatabase): MockDao =
        database.mockDao()

    @Provides
    fun providePreferencesDao(database: DebugLibDatabase): PreferencesDao =
        database.preferencesDao()

    @Provides
    @Singleton
    fun provideBoxStore(@AppContext context: Context): BoxStore {
        return CompletableFuture.supplyAsync {
            MyObjectBox.builder()
                .androidContext(context)
                .build()
        }.get()
    }
}
