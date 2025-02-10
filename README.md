# Introduction

This library provides an on-device solution for obtaining and displaying debug information 
related to the app, with options to export data as needed. It is enabled exclusively for 
DEBUG builds, while a no-op version is available for RELEASE variants. Designed to assist 
both developers and QA teams, it streamlines issue reporting and diagnostics during 
development and testing. A key advantage of this library is that all debugging insights 
are accessible directly on the device, eliminating the need for external tools.

# Caution
The information collected, stored and shared MAY contain sensitive information and/or 
impact the privacy policy of the app. This library should STRICTLY be used only for 
debugging purposes, and **NEVER in production**.

# Features

The debug information collected and reported include:

- Network traffic
- Logcat output
- Summary and contents of Room Database
- Shared preferences
- Status of network, power, and audio systems

The recorded information is persisted on the device, ensuring availability even after a crash.
Note: To prevent excessive growth, older entries will be periodically purged.
## Network traffic

The library uses an ASM based gradle plugin to intercept OkHTTP traffic (no need to add Interceptors manually).

<img src="assets/net1.png" alt="Traffic log" width="200" height="400">
<img src="assets/net2.png" alt="Details" width="200" height="400">

## Logcat

<img src="assets/logcat.png" alt="Logcat output" width="200" height="400">

## Room Database

<img src="assets/room.png" alt="Room DB" width="200" height="400">

## Device Monitor

<img src="assets/devmon1.png" alt="Network Monitor" width="200" height="400">
<img src="assets/devmon2.png" alt="Power and Audio" width="200" height="400">

## Share the collected information

<img src="assets/share.png" alt="Share debug information" width="200" height="400">

# How to use

## Gradle changes
Add to repositories

```
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
```

Add to root project gradle file
```
plugins {
    id("com.sandymist.mobile.plugin.interceptor") version "0.1.3" apply false
}
```

Add to app gradle file as follows

```
    plugins {
        id("com.sandymist.mobile.plugin.interceptor")
    }

    interceptor {
        targetClassName = "com.sandymist.mobile.plugins.network.NetworkPlugin"
    }

    dependencies {
        debugImplementation("com.github.sandymist.android-debug-library:debuglib-debug:<version>")
        releaseImplementation("com.github.sandymist.android-debug-library:debuglib-no-op:<version>")
    }
```

## Code changes

Integrate Debug menu into the app's UI.

### Step 1: Add this line in Application onCreate() (in debug source set)

```
    DebugLib.init(this)
```

### Step 2: Add UI hook

Option 1: Call the composable function directly.

```
    import com.sandymist.android.debuglib.ui.screens.DebugScreen

    DebugScreen(modifier: Modifier)
```

Option 2: Launch the DebugActivity (non-Jetpack-Compose apps)

```
    import com.sandymist.android.debuglib.ui.DebugActivity

    val intent = Intent(context, DebugActivity::class.java)
    context.startActivity(intent)
```
