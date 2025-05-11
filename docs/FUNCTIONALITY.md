# DeviceLock Companion App - Functionality Guide

## Core Functionality

The DeviceLock Companion app is designed to interact with HMD Global's DeviceLock system app, providing a way to learn about system app interactions on Android. The app demonstrates several important mobile development concepts:

### 1. System App Detection

- Uses PackageManager to detect if the DeviceLock app is installed
- Updates UI based on detection results
- Shows appropriate action buttons only when relevant

### 2. System App Interaction

The app can interact with the DeviceLock system app in several ways:

- **Activity Launching**: Uses explicit intents to launch specific activities within the DeviceLock app
  - Launch Update Activity
  - Launch Factory Enroll Activity
  
- **Secret Code Triggering**: Sends SECRET_CODE broadcasts to the system, which can trigger hidden functionality
  - Uses the format "android_secret_code://73447837" (corresponding to DEVICEL on phone keypad)
  - Implements a SecretCodeReceiver to also handle incoming secret codes

### 3. Root Operations (Educational)

When the device has root access, the app demonstrates advanced capabilities:

- **Shared Preferences Access**: Reads the DeviceLock app's shared preferences, showing how app data is stored
- **Database Inspection**: Lists and provides schema information about the app's SQLite databases
- **Data Management**: Offers the ability to backup and wipe registration data (with appropriate warnings)

## Architecture and Design

The app follows modern Android development practices:

### MVVM Architecture

- **ViewModel (MainViewModel)**: Manages UI state and business logic
- **View (MainActivity)**: Handles UI interactions and observes ViewModel state
- **LiveData**: Used for reactive UI updates

### Utility Classes

- **DeviceLockUtils**: Handles DeviceLock-specific operations
- **RootUtils**: Manages root command execution and access
- **IntentUtils**: Provides safe intent handling
- **LogUtils**: Implements logging with buffer management

### Safety Features

- **ConfirmationDialog**: Requires user confirmation for potentially dangerous operations
- **Safe Activity Launch**: Checks if activities exist before attempting to launch them
- **Error Handling**: Provides appropriate user feedback when operations fail

## User Interface

The app features a clean, informative UI that:

- Shows cards for DeviceLock presence and root access status
- Displays appropriate action buttons based on app state
- Includes a scrollable log console to show operational history
- Provides clear warnings for educational-only features

## Educational Value

This app serves as an educational tool to demonstrate:

1. How system apps can be detected and accessed
2. The proper handling of intents for inter-app communication
3. Safe handling of root operations with appropriate warnings
4. Clean architecture patterns in Android development
5. Effective error handling and user feedback