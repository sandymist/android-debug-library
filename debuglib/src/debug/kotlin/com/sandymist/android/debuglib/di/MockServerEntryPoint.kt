package com.sandymist.android.debuglib.di

import com.sandymist.android.debuglib.mock.MockServer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MockServerEntryPoint {
    fun getMockServer(): MockServer
}
