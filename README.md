# ScanPro - Smart PDF Scanner

A modern Android application for scanning documents and creating PDFs with advanced image processing capabilities.

## Features

### Core Functionality
- 📷 **Camera Integration**: Capture document images using CameraX API
- 🖼️ **Image Processing**: Apply filters and enhancements to scanned images
  - Grayscale filter
  - Black & White filter
  - Auto-rotation based on EXIF data
- 📄 **PDF Generation**: Convert multiple images into a single PDF document
- 📚 **PDF Management**: View, organize, and manage all your scanned PDFs
- 🔗 **Sharing**: Easy PDF sharing via Android's share functionality
- 💾 **Storage**: Efficient file management with app-specific storage

### User Interface
- Material Design 3 theme with modern UI/UX
- Intuitive navigation between activities
- Floating action button for quick scanning
- RecyclerView-based gallery for PDF listing
- Real-time image preview with filter effects
- Progress indicators for processing operations

## Project Structure

```
ScanPro/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/scanpro/scanner/
│   │   │   │   ├── MainActivity.kt              # Landing screen
│   │   │   │   ├── CameraActivity.kt            # Document capture
│   │   │   │   ├── ImageProcessingActivity.kt   # Image editing
│   │   │   │   ├── PDFViewerActivity.kt         # PDF viewing
│   │   │   │   ├── GalleryActivity.kt           # PDF gallery
│   │   │   │   ├── utils/
│   │   │   │   │   ├── ImageProcessor.kt        # Image processing utilities
│   │   │   │   │   ├── PDFGenerator.kt          # PDF creation utilities
│   │   │   │   │   └── FileManager.kt           # File management utilities
│   │   │   │   └── adapters/
│   │   │   │       └── PDFAdapter.kt            # RecyclerView adapter
│   │   │   ├── res/
│   │   │   │   ├── layout/                      # XML layouts
│   │   │   │   ├── values/                      # Strings, colors, themes
│   │   │   │   ├── drawable/                    # Icons and drawables
│   │   │   │   ├── menu/                        # Menu resources
│   │   │   │   └── xml/                         # Provider paths, backup rules
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── .gitignore
```

## Technical Specifications

### Android Requirements
- **Target SDK**: 34 (Android 14)
- **Minimum SDK**: 24 (Android 7.0)
- **Compile SDK**: 34
- **Java Version**: 17

### Dependencies
- **AndroidX Core & AppCompat**: Modern Android components
- **Material Design 3**: UI components and theming
- **CameraX**: Camera functionality (version 1.3.1)
- **iText**: PDF generation (version 5.5.10)
- **OpenCV**: Image processing (version 4.8.0)
- **PDF Viewer**: Android PDF viewer library
- **Kotlin Coroutines**: Asynchronous operations

### Permissions
The app requires the following permissions:
- `CAMERA`: For capturing document images
- `READ_EXTERNAL_STORAGE`: For reading files (API < 33)
- `WRITE_EXTERNAL_STORAGE`: For saving files (API < 33)
- `READ_MEDIA_IMAGES`: For accessing images (API ≥ 33)
- `INTERNET`: For potential cloud features

## Setup Instructions

### Prerequisites
1. Android Studio Arctic Fox or later
2. JDK 17
3. Android SDK with API level 34
4. Gradle 8.1.4 or compatible version

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/zaheerabbas7892034214-ai/ScanPro-Smart-PDF-Scanner.git
   cd ScanPro-Smart-PDF-Scanner
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use command line: `./gradlew installDebug`

### Gradle Wrapper

If you don't have the Gradle wrapper, you can generate it:
```bash
gradle wrapper --gradle-version 8.1.4
```

## How to Use

### Scanning a Document
1. Open the app and tap "Scan Document" or the camera FAB
2. Grant camera permissions when prompted
3. Position the document and tap the capture button
4. Preview the captured image and apply filters if needed
5. Tap "Add Page" to scan more pages or "Create PDF" to finish

### Managing PDFs
1. Tap "View Gallery" from the home screen
2. Browse all saved PDFs
3. Tap a PDF to view it
4. Use the menu button to share or delete PDFs

### Image Filters
- **Original**: Keep the image as captured
- **Grayscale**: Convert to black and white tones
- **Black & White**: High contrast for text documents

## Architecture

The app follows modern Android development best practices:
- **MVVM-like pattern** with Activity-based architecture
- **Kotlin Coroutines** for asynchronous operations
- **Material Design 3** for consistent UI/UX
- **FileProvider** for secure file sharing
- **CameraX** for modern camera integration

## File Storage

PDFs are stored in the app-specific external files directory:
```
/storage/emulated/0/Android/data/com.scanpro.scanner/files/Documents/ScanPro/
```

This ensures:
- No need for broad storage permissions on Android 10+
- Automatic cleanup when app is uninstalled
- Secure, app-specific storage

## Building for Release

1. **Generate a signed APK**
   - Build → Generate Signed Bundle / APK
   - Create or select a keystore
   - Fill in the signing configuration
   - Select "Release" build variant

2. **ProGuard Configuration**
   The app includes ProGuard rules for:
   - iText PDF library
   - OpenCV image processing
   - Optimization and obfuscation

## Known Limitations

- Page count in PDF list is currently a placeholder (requires PDF parsing)
- Flash toggle UI could be enhanced with better icons
- No OCR (text recognition) feature yet
- Limited image editing features (no manual cropping)

## Future Enhancements

- [ ] OCR text extraction
- [ ] Manual edge detection and cropping
- [ ] Cloud storage integration
- [ ] PDF editing (merge, split, reorder pages)
- [ ] Document templates
- [ ] Batch scanning mode
- [ ] QR code scanning
- [ ] Dark theme support

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is created as a demonstration Android application.

## Support

For issues, questions, or suggestions, please open an issue on GitHub.

---

**Made with ❤️ for Android developers**
