import SwiftUI
import Shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Kotlin 'fun MainViewController()' usually translates to 'MainViewControllerKt.MainViewController()'
        // matching the exact casing of the Kotlin function.
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
