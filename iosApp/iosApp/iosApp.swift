import SwiftUI
import Shared

@main
struct iosApp: App {
    init() {
        // Initialize Database and Repositories on iOS
        let db = BibleDatabaseKt.getDatabaseBuilder().build()
        BibleRepository.shared.initializeDatabase(db: db)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
