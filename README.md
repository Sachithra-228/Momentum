# Habbit Tracker

A comprehensive Android app for tracking daily habits, mood journaling, and hydration reminders. Built with Kotlin, Material Design 3, and modern Android architecture.

## Features

### 🎯 Daily Habit Tracker
- **Add/Edit/Delete Habits**: Create custom habits with different types (single completion or countable targets)
- **Progress Tracking**: Visual progress bars and completion tracking for each habit
- **Interactive Controls**: 
  - Single habits: Complete/Undo toggle
  - Countable habits: +/- buttons to adjust daily count
- **Swipe to Delete**: Swipe left or right on habit cards to delete with confirmation
- **Day Rollover**: Automatic reset of daily progress at midnight

### 😊 Mood Journal
- **Emoji Selector**: Choose from 12 predefined emojis to express your mood
- **Optional Notes**: Add text notes to accompany your mood entries
- **Visual Chart**: Line chart showing mood entry trends over the last 7 days
- **Share Feature**: Share a summary of your mood entries from the last 7 days
- **Date Grouping**: Mood entries are grouped by date with "Today", "Yesterday", and formatted dates

### 💧 Hydration Reminder
- **Customizable Intervals**: Set reminders every 30 minutes, 1 hour, 2 hours, or 3 hours
- **Smart Notifications**: Uses WorkManager for reliable, battery-efficient notifications
- **Boot Persistence**: Reminders automatically reschedule after device reboot
- **Notification Channel**: Proper notification channel management for Android 8.0+

### ⚙️ Settings & Customization
- **App Customization**: 
  - Change app name (persisted across sessions)
  - Select from 12 predefined primary colors
- **Hydration Settings**: Enable/disable reminders and adjust intervals
- **Demo Data Reset**: Reset to initial demo data for testing
- **About Section**: App version information

## Technical Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Material Design 3 (MDC)
- **Architecture**: Single Activity with Fragments
- **Navigation**: BottomNavigationView
- **Data Persistence**: SharedPreferences with JSON serialization (Gson)
- **Background Tasks**: WorkManager for notifications
- **Charts**: MPAndroidChart for mood trend visualization
- **View Binding**: Enabled for type-safe view access

### Package Structure
```
com.example.habbittracker/
├── data/                    # Data models and persistence
│   ├── Habit.kt            # Habit data class
│   ├── MoodEntry.kt        # Mood entry data class
│   └── PreferencesHelper.kt # SharedPreferences management
├── ui/
│   ├── fragments/          # Main UI fragments
│   │   ├── HabitsFragment.kt
│   │   ├── MoodFragment.kt
│   │   └── SettingsFragment.kt
│   ├── adapters/           # RecyclerView adapters
│   │   ├── HabitsAdapter.kt
│   │   └── MoodAdapter.kt
│   └── dialogs/            # Dialog fragments
│       ├── AddHabitDialog.kt
│       └── AddMoodDialog.kt
├── work/                   # Background workers
│   └── HydrationWorker.kt  # Notification worker
├── receiver/               # Broadcast receivers
│   └── BootReceiver.kt     # Boot completion receiver
└── MainActivity.kt         # Main activity
```

## Data Models

### Habit
```kotlin
data class Habit(
    val id: String,
    var name: String,
    var type: String, // "single" or "countable"
    var targetCount: Int = 1,
    var currentCountToday: Int = 0,
    var lastUpdatedDate: String // yyyy-MM-dd format
)
```

### MoodEntry
```kotlin
data class MoodEntry(
    val id: String,
    val emoji: String,
    val timestamp: Long,
    val note: String? = null
)
```

## Configuration

### App Identity (Configurable)
- **APP_NAME**: "Habbit Tracker" (customizable in Settings)
- **PRIMARY_COLOR_HEX**: "#8D6E63" (Brown - customizable in Settings)

### Advanced Features
The app implements **Option A** (Chart visualization) by default. Options B and C are provided as commented templates:

- **Option A (Active)**: MPAndroidChart showing 7-day mood trend
- **Option B (Template)**: Home-screen widget for habit completion percentage
- **Option C (Template)**: Accelerometer shake detection for quick mood entry

To enable Options B or C, uncomment the relevant code sections and add required dependencies.

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 2.0.21+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Dependencies
All required dependencies are configured in `gradle/libs.versions.toml`:
- Material Design 3
- WorkManager for notifications
- MPAndroidChart for data visualization
- Gson for JSON serialization
- Navigation Components
- ViewBinding

## Key Implementation Details

### Data Persistence
- **Storage**: SharedPreferences with JSON serialization
- **Keys**: `habits_json`, `moods_json`, `hydration_enabled`, etc.
- **Day Rollover**: Automatic habit progress reset when app opens on a new day
- **Demo Data**: Pre-seeded with 3 sample habits and 3 mood entries

### Notification System
- **Channel**: "Hydration Reminders" with default importance
- **WorkManager**: Periodic work requests with battery optimization
- **Boot Persistence**: BootReceiver reschedules notifications after device restart
- **Constraints**: Requires battery not low for optimal user experience

### UI/UX Features
- **Material 3**: Latest Material Design components and theming
- **Dark Mode**: Full support with appropriate color schemes
- **Responsive**: Works on phones and tablets in portrait/landscape
- **Accessibility**: Proper content descriptions and touch targets
- **Animations**: Smooth transitions and feedback

### Sharing Functionality
- **Mood Summary**: Generates text summary of last 7 days' mood entries
- **Format**: Emoji counts and total entries with emoji frequency
- **Intent**: Uses implicit share intent for maximum compatibility

## Development Notes

### Day Rollover Logic
The app automatically detects when a new day begins and resets all habit progress:
1. On app launch, compares `last_open_date` with current date
2. If different, resets all habits' `currentCountToday` to 0
3. Updates `lastUpdatedDate` to today's date
4. Persists changes to SharedPreferences

### WorkManager Integration
Hydration reminders use WorkManager for reliability:
- Periodic work requests with flexible intervals
- Battery optimization constraints
- Automatic rescheduling after device reboot
- Unique work names to prevent duplicates

### Error Handling
- Graceful JSON parsing with fallback to empty lists
- Input validation for habit creation and editing
- Toast messages for user feedback
- Confirmation dialogs for destructive actions

## Testing & Demo Data

The app includes comprehensive demo data for immediate testing:
- **Sample Habits**: "Drink Water" (countable, target 8), "Exercise" (single), "Meditate" (single)
- **Sample Moods**: Various emoji entries with notes
- **Reset Feature**: Settings option to restore demo data

## Future Enhancements

Potential improvements for production use:
- Database migration from SharedPreferences
- Cloud sync capabilities
- Widget implementation (Option B)
- Sensor integration (Option C)
- Habit streaks and statistics
- Export/import functionality
- Reminder customization (specific times, days)

## License

This project is created as an educational example and demonstration of Android development best practices.

---

**Viva Points:**
- Data persistence via SharedPreferences with JSON serialization
- Day rollover logic with automatic habit progress reset
- WorkManager for reliable background notifications
- Material Design 3 implementation with dark mode support
- Chart visualization using MPAndroidChart
- Share functionality with mood summary generation
- Single-activity architecture with Fragment navigation
