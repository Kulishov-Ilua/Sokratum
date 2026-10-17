package ru.kulishov.sokratum

import ru.kulishov.sokratum.di.initKoin
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("KoinHelper", exact = true)
object KoinHelper {
    fun doInitKoin() = initKoin()
}