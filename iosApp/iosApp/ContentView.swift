import SwiftUI
import Shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Kotlin 'fun MainViewController()' becomes 'mainViewController()' in Swift
        return MainViewControllerKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
