DeviceLock Companion App
======================

VERSION: 1.0
PACKAGE: com.example.devicelockcompanion

Description:
-----------
The DeviceLock Companion is an Android application designed to interact with HMD Global's
DeviceLock system app. This app was created for educational purposes to demonstrate how to
detect, interact with, and potentially modify system applications on Android devices.

Features:
--------
1. DeviceLock Detection: Identifies if the HMD Global DeviceLock app is installed
2. Activity Launching: Provides buttons to launch various activities within the DeviceLock app
3. Secret Code Triggering: Can send secret code broadcasts to the DeviceLock app
4. Root Operations (requires root access):
   - View shared preferences of the DeviceLock app
   - View database files used by the DeviceLock app
   - Create backups and reset registration data

Installation:
------------
1. Enable USB debugging on your Android device
2. Connect your device to your computer
3. Run: adb install app-debug.apk

Requirements:
------------
- Android 8.0 (API level 26) or higher
- Root access for advanced features (optional)

Technical Details:
----------------
- Written in Kotlin
- Uses View Binding for UI
- MVVM architecture pattern
- DeviceLock package target: com.hmdglobal.app.devicelock

Disclaimer:
----------
This application is intended for educational use only. It demonstrates app interaction techniques
and should not be used to bypass or interfere with system security mechanisms without proper
authorization.
