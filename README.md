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

# PermissionH

The `PermissionH` object provides utility functions for managing runtime permissions in Android applications.

## Usage

### `checkRunTimePermissions`
Checks if the provided permissions are granted or not.

#### Parameters:
- `permission`: List of permissions to check.
- `activity`: The current activity.
- `callback`: Callback invoked with a boolean value indicating if all permissions are granted.

#### Example:
```kotlin
val permissionsToCheck = listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

PermissionH.requestPermissions(permissionsToCheck, this) { allPermissionsGranted ->
    if (allPermissionsGranted) {
        // All permissions granted, proceed with necessary operations
    } else {
        // Permissions not granted, handle accordingly
    }
}
```

## **Loadialog:** *Loadialog Your Loading*
The `Loadialog` class is a custom dialog component crafted for overseeing loading indicators within your Android application.

## Usage

- **Initialization**: Construct the `Loadialog` within your Activity:
  ```
  val loadDialog = Loadialog(this)
  ```
- **Usage**: Show loading within your Activity:
  ```
  loadDialog.showDialog("Your text here...")
  ```
  #### Show Progress bar:
  ```
  loadDialog.setProgress(progressMax, currentProgress))
  ```
    #### Dismiss Dialog:
  ```
  loadDialog.dismissDialog()
  ```

## File Extensions:

| Function Name            | Description                                     |
| -------------------------| ----------------------------------------------- |
| `formatFileSize`         | Formats file size for easy human readability.    |
| `deleteDir`              | Deletes a directory and its contents.            |
| `getFolderSize`          | Retrieves the size of a specific folder.         |
| `getAppFolderSize`       | Gets the size of the application's folder.      |
| `getMediaDuration`       | Retrieves duration information from media files. |
| `is_MP4_Or_MP3`          | Checks if a file is in MP4 or MP3 format.       |
| `getMediaThumbnail`      | Fetches thumbnails from media files.            |
| `getFileSizeOnline`      | Retrieves the size of a file hosted online.     |
| `getMediaFrame`          | Retrieves frames from media files.              |
| `getFilePathFromContentUri` | Fetches file path from a content URI.       |
| `getThumbFromAudio`      | Retrieves a thumbnail image from an audio file. |
| `fileToBytes`            | Converts a file into a byte array.              |
| `shareMultipleFiles`     | Shares multiple files using an intent.          |


## Usage Examples:

### `formatFileSize()`

This function formats the file size to display in a human-readable format. Example usage:

```kotlin
val file = File("/path/to/your/file")
val formattedSize = file.formatFileSize()
println("Formatted File Size: $formattedSize")
"Formatted File Size: 349 KB"
```
### Global Extensions


| Function Name                    | Description                                       |
| ---------------------------------| ------------------------------------------------- |
| `intentData`                     | Holds data for intents.                            |
| `getDeviceHashID()`              | Returns device ID as String.                       |
| `toast(message)`                 | Displays a brief message.                          |
| `routWithData(data, destination)` | Navigates to a destination with specified data.   |
| `rout(destination)`             | Opens a new screen or destination.                 |
| `isInitialized(): Boolean`      | Checks if an object is initialized.                |
| `routFlow(destination)`         | Navigates to a destination with priority.          |
| `saveFolder(): File?`           | Retrieves the path for storing files.              |
| `formatTo01(): String`          | Converts a number to a two-digit format.           |
| `verticalRv()`                  | Prepares a vertical scrolling list.                |
| `gridV(span: Int)`              | Sets up a grid-style layout.                       |
| `lastIndex(): Int`              | Retrieves the last index of an array or list.      |
| `convertMillisToDateFormat(): String` | Formats milliseconds to a readable date.      |
| `formatToDay(): String`         | Converts the current date to the day of the week.  |
| `copyToClipboard(text)`         | Copies text to the clipboard.                      |
| `bitArray(link): ByteArray`     | Fetches an image from a link as a ByteArray.       |
| `convertToMinutesSeconds(): String` | Converts milliseconds to minutes and seconds. |
| `calculateNetworkSpeed(bytes, time): String` | Calculates network speed.                 |
| `log(message): String`          | Logs a message.                                   |
| `shareLink(link)`               | Shares a link via available apps.                  |
| `getAvailableRAM(): Long`       | Retrieves available RAM.                          |
| `downloadThisFile(url, path, fileName): File?` | Downloads a file to a specified location. |
| `shortenText(text): String`     | Shortens a text to a specific length.              |
| `isWifiAvailable(): Boolean`    | Checks if Wi-Fi is available.                      |
| `removeSpecialChars(text): String` | Removes special characters from a string.      |
| `getStringByName(name): String?` | Retrieves a string by its resource name.         |
| `isInternetAvailable(): Boolean`| Checks internet availability.                      |
| `getCurrentTime(): String`      | Retrieves the current time.                        |
| `imageToByteArray(): ByteArray` | Converts an image to a ByteArray.                  |
| `resizeImageByScale(scale): Bitmap` | Resizes an image by a specific scale.         |
| `shortenUrl(url, callback)`:    | Shortens a URL and provides a callback.           |
| `openLinkInChrome(url)`         | Opens a URL in the Chrome browser if available.    |
| `sendFeedbackEmailTo(email)`    | Composes an email for feedback.                    |
| `capitalizeFirstLetter(): String`| Capitalizes the first letter of a string.   

### View Extensions

| Function Name                           | Description                                                     |
| ---------------------------------------| --------------------------------------------------------------- |
| `tint(color: Int)`                      | Sets a tint color to the ImageView.                              |
| `loadImage(src: Any)`                   | Loads an image into the ImageView using Glide.                    |
| `loadImageFromAssets(fileName: String)` | Loads an image from assets into the ImageView.                   |
| `loadImage(src: Any, errorIco: Int)`    | Loads an image with an error icon into the ImageView.            |
| `loadImage(src: Any, cache: Boolean)`   | Loads an image into the ImageView with or without caching.       |
| `setTint(color: Int)`                   | Sets a background tint color to the View.                        |
| `setOnSwipeListener(onSwipe: ...)`      | Sets a swipe listener to detect swipe directions on the View.    |
| `safeClickListener(debounceTime: Long)` | Sets a click listener with a debounce time to avoid rapid clicks.|
| `animIn(left: Boolean, right: Boolean)` | Animates the View to appear from left or right with scaling.     |
| `animOut(left: Boolean, right: Boolean)`| Animates the View to disappear to left or right with scaling.    |
| `showKeyboard()`                        | Displays the soft keyboard for the View.                         |
| `hideKeyboard()`                        | Hides the soft keyboard for the View.                            |
| `setOnSearchClickListener(callback)`    | Sets a listener for search action on EditText.                   |
| `setOnTextChangeListener(callback)`    | Sets a text change listener on EditText.                         |
| `autoFillFromClipBoard()`               | Automatically fills an EditText from clipboard if a URL exists.   |
| `focusListener()`                       | Sets a focus listener to toggle keyboard visibility on the View. |
       

## Contributing
Contributions to this project are welcome! If you have ideas, improvements, or bug fixes to suggest, please create a new issue or submit a pull request to this repository.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Contact
For questions, issues, or feedback, please visit the [GitHub Repository](https://github.com/syedhaseeb1/extensions_hsb) and open an issue. We'd love to hear from you!
