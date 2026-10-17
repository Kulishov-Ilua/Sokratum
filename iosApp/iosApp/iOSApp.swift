import SwiftUI

@main
struct iOSApp: App {
    init(){
        initKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}