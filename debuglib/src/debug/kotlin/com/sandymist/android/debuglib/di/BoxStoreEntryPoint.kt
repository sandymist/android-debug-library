package com.sandymist.android.debuglib.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.objectbox.BoxStore

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BoxStoreEntryPoint {
    fun getBoxStore(): BoxStore
}