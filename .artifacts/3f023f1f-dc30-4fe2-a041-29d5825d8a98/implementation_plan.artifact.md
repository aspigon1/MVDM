# Update App Logo and Icon

This plan updates the application's launcher icon to use the newly added `MANNE1.png` logo, styled with the project's brand colors.

## User Review Required

> [!IMPORTANT]
> This will replace the default Android "bugroid" launcher icon with your custom logo.

## Proposed Changes

### Resources

#### [MODIFY] [ic_launcher_background.xml](file:///C:/Users/info/AndroidStudioProjects/MyApplication/app/src/main/res/drawable/ic_launcher_background.xml)
- Change the background color from the default Android green to the app's dark theme color (`#1A1207`).

#### [MODIFY] [ic_launcher_foreground.xml](file:///C:/Users/info/AndroidStudioProjects/MyApplication/app/src/main/res/drawable/ic_launcher_foreground.xml)
- Replace the vector "bugroid" paths with a reference to `MANNE1.png`.
- Ensure the logo is properly centered and scaled.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/info/AndroidStudioProjects/MyApplication/app/src/main/java/com/test/myapplication/MainActivity.kt)
- Update the hardcoded "✝" text in the `HomeScreen` header to use the `MANNE1.png` drawable for a more professional look.

## Verification Plan

### Manual Verification
- Deploy the app to a device/emulator.
- Verify the new icon appears on the home screen.
- Verify the logo appears correctly in the app's header on the Home screen.
