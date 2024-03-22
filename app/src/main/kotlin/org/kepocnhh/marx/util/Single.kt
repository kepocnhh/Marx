package org.kepocnhh.marx.util

import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal sealed interface Single<out T : Any> {
    class Success<T : Any>(val value: T) : Single<T>
    class Failure(val error: Throwable) : Single<Nothing>
}

internal suspend fun <T : Any> withCatching(
    context: CoroutineContext,
    block: () -> T,
): Single<T> {
    return withContext(context) {
        try {
            Single.Success(value = block())
        } catch (error: Throwable) {
            Single.Failure(error = error)
        }
    }
}
