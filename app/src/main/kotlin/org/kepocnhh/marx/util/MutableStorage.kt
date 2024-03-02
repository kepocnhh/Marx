package org.kepocnhh.marx.util

internal interface MutableStorage<T : Any> : Storage<T> {
    fun add(item: T)
    fun removeFirst(predicate: (T) -> Boolean)
}
