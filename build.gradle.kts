// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlinCompose) apply false
    kotlin("plugin.serialization") version "2.1.20" apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.devtools.ksp") version "2.1.20-2.0.1" apply false
    id("de.mannodermaus.android-junit5") version "1.13.0.0" apply false
    id("tech.apter.junit5.jupiter.robolectric-extension-gradle-plugin") version "0.9.0" apply false
}