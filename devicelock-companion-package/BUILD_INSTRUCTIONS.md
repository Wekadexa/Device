# Building DeviceLock Companion App

This document provides instructions for building the DeviceLock Companion app.

## Option 1: Build with Android Studio (Recommended)

1. **Install Android Studio**
   - Download and install Android Studio from [developer.android.com](https://developer.android.com/studio)

2. **Import the Project**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the devicelock-companion folder and select it

3. **Sync Gradle Files**
   - Android Studio will automatically sync the Gradle files
   - Wait for this process to complete

4. **Build the APK**
   - Click on "Build" in the menu
   - Select "Build Bundle(s) / APK(s)"
   - Choose "Build APK(s)"

5. **Locate the APK**
   - When the build completes, Android Studio will show a notification
   - Click "locate" in the notification to find the APK
   - The APK will be in app/build/outputs/apk/debug/

## Option 2: Build from Command Line

1. **Install the Java Development Kit (JDK)**
   - Download and install JDK 11 or newer

2. **Set JAVA_HOME Environment Variable**
   - Set JAVA_HOME to point to your JDK installation

3. **Build Using Gradle Wrapper**
   - Open a terminal/command prompt
   - Navigate to the project root directory
   - Run:
     ```
     ./gradlew assembleDebug
     ```
   - On Windows, use `gradlew.bat` instead of `./gradlew`

4. **Locate the APK**
   - The APK will be in app/build/outputs/apk/debug/

## Installing on Your Phone

1. **Enable Developer Options**
   - On your Android phone, go to Settings
   - Navigate to "About phone"
   - Tap "Build number" seven times to enable Developer Options

2. **Enable USB Debugging**
   - Go to Settings > System > Developer Options
   - Enable "USB debugging"

3. **Connect Your Phone**
   - Connect your phone to your computer via USB
   - Confirm any permission prompts on your phone

4. **Install via ADB**
   - Using Android Studio or command line:
     ```
     adb install path/to/devicelock-companion-debug.apk
     ```
   - Or, transfer the APK to your phone and install it directly

## Troubleshooting

- **Build Errors**: Make sure you have the correct Android SDK version installed
- **Installation Issues**: Ensure that "Install from Unknown Sources" is enabled on your phone
- **App Crashes**: Check logcat for error details