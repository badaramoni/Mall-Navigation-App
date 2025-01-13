# Mall Navigation App

A cross-platform mobile application built with Kotlin Multiplatform and Compose Multiplatform that helps users navigate through shopping malls. The app provides an interactive map interface with real-time navigation instructions between shops.


https://github.com/user-attachments/assets/da52a209-08a7-466e-a11a-8a1080100fcd
## Features

- 🗺️ Interactive Mall Map
  - Pan and zoom functionality
  - Grid-based layout visualization
  - Shop locations with custom emoji indicators
  - Color-coded markers for start and destination points

- 🧭 Navigation
  - Select start and destination shops
  - Visual path display between locations
  - Turn-by-turn navigation instructions
  - Real-time heading updates

- 🏪 Shop Management
  - Visual representation of shops with custom icons
  - Categorized shop emojis based on shop type
  - Easy-to-use shop selection interface

## Technical Architecture

### Project Structure

* `/composeApp` - Shared code across platforms
  - `commonMain` - Core business logic and UI components
  - `androidMain` - Android-specific implementations
  - `iosMain` - iOS-specific implementations

* `/iosApp` - iOS application entry point and SwiftUI integrations

### Key Components

1. **NavigationScreen**
   - Main UI component handling the map visualization
   - Implements gesture detection for map interaction
   - Manages shop rendering and path display

2. **NavigationViewModel**
   - Handles navigation logic and state management
   - Processes shop selection and path calculations
   - Manages real-time heading updates

3. **MallMap**
   - Data structure representing the mall layout
   - Manages shop locations and connections
   - Handles path finding between locations

## Implementation Details

### Map Rendering
- Custom Canvas implementation for map drawing
- Efficient scaling and translation handling
- Dynamic text rendering with proper constraints
- Responsive grid system that scales with zoom level

### Navigation System
- Path finding between selected shops
- Real-time navigation instructions
- Visual feedback for current location and destination

### UI Components
- Material Design components for consistent look
- Custom dropdowns for shop selection
- Interactive markers and paths
- Responsive layout adapting to different screen sizes

## Building and Running

### Prerequisites
- Android Studio Arctic Fox or newer
- Xcode 13 or newer (for iOS development)
- JDK 11 or newer
- Kotlin Multiplatform Mobile plugin

### Android
1. Open project in Android Studio
2. Select 'composeApp' configuration
3. Run on your device or emulator

### iOS
1. Open `/iosApp/iosApp.xcworkspace` in Xcode
2. Select your target device
3. Build and run

## Development

### Adding New Features
1. Add shared code to `/composeApp/commonMain`
2. Implement platform-specific code in respective folders
3. Update tests accordingly

### Testing
- Unit tests: `./gradlew test`
- Android instrumented tests: `./gradlew connectedAndroidTest`
- iOS tests: Run through Xcode

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details

## Acknowledgments

- Built with [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- UI powered by [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Navigation algorithms inspired by standard pathfinding techniques
