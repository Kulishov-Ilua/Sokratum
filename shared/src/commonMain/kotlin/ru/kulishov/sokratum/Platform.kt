package ru.kulishov.sokratum

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform