# iOS Setup Guide

To get your app working on iOS, you need to create an iOS project that consumes the `Shared` framework.

## 1. Create the Xcode Project
1. Open **Xcode** on a Mac.
2. Create a new **App** project named `iosApp`.
3. Select **SwiftUI** for the interface.
4. Save it in the root of your project: `C:/projects/MVDM/iosApp` (or the equivalent path on your Mac).

## 2. Connect the Shared Framework
You have two main options:

### Option A: CocoaPods (Easiest for KMP)
1. Add the CocoaPods plugin to your `shared/build.gradle.kts`.
2. Run `./gradlew :shared:generatePodspec`.
3. Create a `Podfile` in your `iosApp` directory:
```ruby
target 'iosApp' do
  use_frameworks!
  pod 'Shared', :path => '../shared'
end
```
4. Run `pod install`.

### Option B: Swift Package Manager (Modern)
Use the [Multiplatform Library Template](https://github.com/JetBrains/kotlin-multiplatform-wizard) approach or link the framework manually in Xcode.

## 3. Initialize the UI
In your SwiftUI entry point (`App.swift`), use the `MainViewController` from your shared module:

```swift
import SwiftUI
import Shared

@main
struct iosApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

## 4. Initialize Firebase on iOS
1. Go to the Firebase Console and add an iOS app to your project.
2. Download `GoogleService-Info.plist` and add it to your Xcode project.
3. Initialize Firebase in `AppDelegate` or the SwiftUI `App` init.

## 5. Shared Logic Initialization
In your `MainViewController.kt` (iOS), ensure you initialize the database:

```kotlin
fun MainViewController(): UIViewController = ComposeUIViewController {
    val db = getDatabaseBuilder().build()
    BibleRepository.init(db)
    LaunchedEffect(Unit) {
        BibleRepository.ensureSeeded()
    }
    App()
}
```

> [!TIP]
> Use the **Kotlin Multiplatform Wizard** to generate a standard `iosApp` folder if you want a reference implementation.
