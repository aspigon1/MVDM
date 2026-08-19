import SwiftUI
import Shared

@main
struct iosApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea() // Ensure Compose takes full screen
        }
    }
}
