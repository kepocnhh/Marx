package org.kepocnhh.marx.module.foo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.module.app.Injection
import sp.kx.logics.Logics
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KMutableProperty
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class FooLogics(
    private val injection: Injection,
) : Logics(injection.contexts.main) {
    data class State(
        val meta: Meta,
        val items: List<Foo>,
    )

    private val logger = injection.loggers.create("[Foo]")
    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    private operator fun <T> List<T>.plus(pair: Pair<T, (T) -> Boolean>): List<T> {
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

    private fun sha256(
        id: UUID,
        created: Duration,
        updated: Duration,
        bytes: ByteArray,
    ): String {
        val hash = StringBuilder()
            .append(id)
            .append(created.inWholeMilliseconds)
            .append(updated.inWholeMilliseconds)
            .toString()
            .toByteArray()
            .plus(bytes)
            .let(injection.security::sha256)
        return String.format("%064x", BigInteger(1, hash))
    }

    private fun updateMeta(id: UUID, bytes: ByteArray): Meta {
        logger.debug("update meta: $id...")
        val oldMeta = injection.locals.metas.firstOrNull { it.id == id } ?: TODO()
        logger.debug("old meta: $oldMeta")
        val updated = System.currentTimeMillis().milliseconds
        val sha256 = sha256(
            id = id,
            created = oldMeta.created,
            updated = updated,
            bytes = bytes,
        )
        val newMeta = oldMeta.copy(
            updated = updated,
            hash = sha256,
        )
        logger.debug("new meta: $newMeta")
        injection.locals.metas += newMeta to { it.id == id }
        return newMeta
    }

    fun requestItems() = launch {
        val items = withContext(injection.contexts.default) {
            injection.locals.foo
        }
        val meta = withContext(injection.contexts.default) {
            val metaId = Foo.META_ID
            injection.locals.metas.firstOrNull {
                it.id == metaId
            } ?: System.currentTimeMillis().milliseconds.let { created ->
                val sha256 = sha256(
                    id = metaId,
                    created = created,
                    updated = created,
                    bytes = injection.serializer.foo.toByteArray(emptyList()),
                )
                val meta = Meta(
                    id = metaId,
                    created = created,
                    updated = created,
                    hash = sha256,
                )
                injection.locals.metas += meta
                meta
            }
        }
        _state.emit(State(meta, items))
    }

    private operator fun <T> List<T>.minus(predicate: (T) -> Boolean): List<T> {
        val result = toMutableList()
        for (i in result.indices) {
            if (predicate(result[i])) {
                result.removeAt(i)
                return result
            }
        }
        error("The value is not deleted!")
    }

    fun deleteItem(id: UUID) = launch {
        val items = withContext(injection.contexts.default) {
            injection.locals.foo - { it.id == id }
        }
        val meta = withContext(injection.contexts.default) {
            injection.locals.foo = items
            updateMeta(id = Foo.META_ID, bytes = injection.serializer.foo.toByteArray(items))
        }
        _state.emit(State(meta, items))
    }

    fun addItem(text: String) = launch {
        logger.debug("add item: \"$text\"...")
        val items = withContext(injection.contexts.default) {
            injection.locals.foo + Foo(
                id = UUID.randomUUID(),
                created = System.currentTimeMillis().milliseconds,
                text = text,
            )
        }
        val meta = withContext(injection.contexts.default) {
            injection.locals.foo = items
            updateMeta(id = Foo.META_ID, bytes = injection.serializer.foo.toByteArray(items))
        }
        _state.emit(State(meta, items))
    }
}
