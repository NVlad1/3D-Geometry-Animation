# F-Droid inclusion notes

This project is prepared for an initial F-Droid submission from a public source repository with clear release history.

## App identity

- App name: 3D Geometry Animation
- Application ID: `io.github.nvlad1.function3danimator`
- Version for initial listing: `1.4.0`
- Version code for initial listing: `24`
- License: MIT, documented in `LICENSE.txt`

## Privacy and offline behavior

- The app does not declare the Android `INTERNET` permission.
- The app does not include ads, analytics, tracking SDKs, account sign-in, or external API calls.
- User-created functions are stored locally with Room.
- User settings are stored locally with Android preferences.
- Android system backup is enabled in the manifest with `android:allowBackup="true"`, so device-level backup services may copy app data outside the app if the user has backups enabled.

## F-Droid policy notes

- Source code should be published before submission so F-Droid can build from public source.
- Release tags should match the submitted version name and version code.
- Dependencies are Gradle/Maven dependencies, not vendored binary blobs.
- The app requires OpenGL ES 2.0 via `<uses-feature android:glEsVersion="0x00020000" android:required="true" />`.

## App-side metadata

Localized app metadata is stored under `fastlane/metadata/android/en-US/`:

- `title.txt`
- `short_description.txt`
- `full_description.txt`
- `changelogs/24.txt`
- `images/icon.png`
