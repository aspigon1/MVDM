import SwiftUI
import Shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Use the new reliable Factory class
        return IOSLauncher().create()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
