# DeviceLock Companion App

## Overview

DeviceLock Companion is an Android application designed to interact with the HMD Global DeviceLock system app (com.hmdglobal.app.devicelock) for educational purposes. This app demonstrates how to detect, interact with, and potentially modify system applications on Android devices.

## Features

- **DeviceLock Detection**: Identifies if the HMD Global DeviceLock app is installed on the device
- **Activity Launching**: Provides buttons to launch various activities within the DeviceLock app
- **Secret Code Triggering**: Can send secret code broadcasts to the DeviceLock app
- **Root Operations** (requires root access):
  - View shared preferences of the DeviceLock app
  - View database files used by the DeviceLock app
  - Create backups and reset registration data

## Technical Implementation

The app is built using:
- Kotlin programming language
- Android View Binding for UI
- MVVM architecture pattern
- Utility classes for handling various operations

## Core Components

- **DeviceLockUtils**: Handles interaction with the DeviceLock app
- **RootUtils**: Manages root operations and command execution
- **IntentUtils**: Safely handles intent creation and launching
- **LogUtils**: Manages logging within the application
- **ConfirmationDialog**: Provides confirmation dialogs for sensitive operations

## Educational Purpose

This application is intended for educational use only. It demonstrates:
- How to interact with system applications
- How to use root access responsibly
- How to implement proper error handling for system operations

## Disclaimer

This application should not be used to bypass or interfere with system security mechanisms without proper authorization. The app includes appropriate warning dialogs before performing any potentially sensitive operations.

## Requirements

- Android 8.0 (API level 26) or higher
- Root access for advanced features (optional)# Unlocker
