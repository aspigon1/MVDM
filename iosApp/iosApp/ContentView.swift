import SwiftUI
import Shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Use the new reliable bridge class
        return IOSBridge().createRootController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
