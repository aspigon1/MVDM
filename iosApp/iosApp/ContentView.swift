import SwiftUI
import Shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Updated name to match the new Kotlin function
        return SharedMainViewControllerKt.createComposeViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
