# HSB Extensions Library

## Overview
The HSB Extensions Library is a powerful collection of Kotlin extensions designed to simplify your Android development tasks and enhance code readability. With a wide range of utility functions and extensions, this library is here to make your development journey smoother.

## Installation
You can quickly integrate the HSB Extensions Library into your project. Add the following to your project's build.gradle file:


### Gradle (Groovy)
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```
```
dependencies {
    implementation 'com.github.syedhaseeb1:extensions_hsb:$latestVersion'
}
```

### Gradle (Kotlin KTS)

```KTS

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}
```
```
dependencies {
    implementation("com.github.syedhaseeb1:extensions_hsb:$latestVersion")
}
```
## Features
- **Simplify Your Code**: Reduce boilerplate code and improve code readability.
- **Boost Productivity**: Enhance your development productivity with a wide range of utility functions and extensions.
- **Versatile Functions**: Access numerous utility functions for common tasks.
- **Keep Your Codebase Clean**: Organize and maintain your codebase effectively using these extensions.

## Usage
To get started with the HSB Extensions Library, simply import the relevant extensions into your project and start using them in your code. Explore the documentation and examples to understand how to make the most of this library.

## Dependencies
This library leverages the power of the following dependencies:
- [Glide (Version 4.16.0)](https://github.com/bumptech/glide)
- [PermissionX (Version 1.7.1)](https://github.com/guolindev/PermissionX)
- [OkHttp (Version 4.11.0)](https://github.com/square/okhttp)

## Custom Classes
In addition to the mentioned dependencies, the HSB Extensions Library includes custom-made classes to simplify common tasks and enhance reusability in your Android development projects. Feel free to explore the source code and documentation to learn more about these custom classes.


## File Extensions:

1. `formatFileSize`: Formats the file size to a human-readable format.
2. `deleteDir`: Deletes a directory and its contents.
3. `getFolderSize`: Retrieves the size of a folder.
4. `getAppFolderSize`: Gets the size of the application's folder.
5. `getMediaDuration`: Retrieves the duration of media files.
6. `is_MP4_Or_MP3`: Checks if a file is an MP4 or MP3 file.
7. `getMediaThumbnail`: Retrieves the thumbnail of media files.
8. `getFileSizeOnline`: Retrieves the size of a file hosted online.
9. `getMediaFrame`: Retrieves the frame of media files.
10. `getFilePathFromContentUri`: Retrieves the file path from a content URI.
11. `getThumbFromAudio`: Retrieves a thumbnail image from an audio file.
12. `fileToBytes`: Converts a file into a byte array.
13. `shareMultipleFiles`: Shares multiple files via an intent.

## Usage Examples:

### `formatFileSize()`

This function formats the file size to display in a human-readable format. Example usage:

```kotlin
val file = File("/path/to/your/file")
val formattedSize = file.formatFileSize()
println("Formatted File Size: $formattedSize")
```
### Global Extensions

| Function Name | Function Name | Function Name |
| ------------- | ------------- | ------------- |
| `intentData` | `toast(msg: String)` | `routWithData(data: String, cls: Class<*>)` |
| `rout(cls: Class<*>)` | `isInitialized(): Boolean` | `routFlow(cls: Class<*>)` |
| `saveFolder(): File?` | `formatTo01(): String` | `verticalRv()` |
| `gridV(span: Int)` | `lastIndex(): Int` | `convertMillisToDateFormat(): String` |
| `formatToDay(): String` | `copyToClipboard(text: String)` | `bitArray(link: String): ByteArray` |
| `convertToMinutesSeconds(): String` | `calculateNetworkSpeed(downloadedBytes: Long, elapsedTime: Long): String` | `log(msg: String): String` |
| `shareLink(link: String)` | `getAvailableRAM(): Long` | `downloadThisFile(url: String, destinationPath: String?, fileName: String): File?` |
| `short(s: String): String` | `isWifiAvailable(): Boolean` | `replaceCharacters(): String` |
| `getStringByName(resourceName: String): String?` | `isInternetAvailable(): Boolean` | `getCurrentTime(): String` |
| `byteArray(): ByteArray` | `resizeBy(size: Int): Bitmap` | `shortenUrl(longUrl: String, callback: (String?) -> Unit)` |
| `openLinkInChrome(url: String)` | `sendFeedbackEmailTo(email: String)` | `capitalizeFirstLetter(): String` |

## Contributing
Contributions to this project are welcome! If you have ideas, improvements, or bug fixes to suggest, please create a new issue or submit a pull request to this repository.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Contact
For questions, issues, or feedback, please visit the [GitHub Repository](https://github.com/syedhaseeb1/extensions_hsb) and open an issue. We'd love to hear from you!
