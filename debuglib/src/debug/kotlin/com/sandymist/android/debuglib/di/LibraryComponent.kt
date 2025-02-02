package com.sandymist.android.debuglib.di

import android.app.Application
import dagger.Component

@Component(modules = [DatabaseModule::class])
interface LibraryComponent {
    fun inject(application: Application)
}
