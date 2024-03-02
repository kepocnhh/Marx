package org.kepocnhh.marx.util

internal interface Storage<T : Any> : Iterable<T> {
    val size: Int
}

internal fun <T : Any> Storage<T>.isEmpty(): Boolean {
    return size == 0
}

internal fun <T : Any> Storage<T>.isNotEmpty(): Boolean {
    return size > 0
}
