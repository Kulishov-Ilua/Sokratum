package ru.kulishov.sokratum.ui


object DeviceData {
    var actualPlatform: Platforms? = null
    sealed class OS{
        object IOS: OS()
        object Android:OS()
        object MacOS:OS()
        object IpadOS:OS()
        object Windows:OS()
        object Linux:OS()
        object NotFound: OS()
    }
    sealed class Platforms{
        class Desktop(val os:OS, val info:String): Platforms()
        class Phone(os: OS,info: String): Platforms()
        class Tablet(os:OS, info: String): Platforms()
        class WebDesktop(info: String): Platforms()
        class WebTablet(info: String): Platforms()
        class WebPhone(info: String): Platforms()
    }
}