// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.objectbox.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.sandymist.mobile.plugin.interceptor") version "0.1.3" apply false
}

val projectVersion: String by extra("0.0.8-DEV5")
