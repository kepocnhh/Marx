package org.kepocnhh.marx.util

internal operator fun <T : Any> Iterable<T>.plus(pair: Pair<T, (T) -> Boolean>): List<T> {
    val (value, predicate) = pair
    val result = toMutableList()
    for (i in result.indices) {
        if (predicate(result[i])) {
            result.removeAt(i)
            result.add(value)
            return result
        }
    }
    error("The value ($value) is not replaced!")
}

internal operator fun <T : Any> Iterable<T>.minus(predicate: (T) -> Boolean): List<T> {
    val result = toMutableList()
    for (i in result.indices) {
        if (predicate(result[i])) {
            result.removeAt(i)
            return result
        }
    }
    error("The value is not deleted!")
}
