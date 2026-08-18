import SwiftUI
import FirebaseCore
import Shared

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        // Safe initialization to prevent crash if plist is missing
        if let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") {
            FirebaseApp.configure()
            print("Firebase initialized successfully")
        } else {
            print("Warning: GoogleService-Info.plist not found, Firebase features disabled")
        }
        return true
    }
}

@main
struct iosApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
