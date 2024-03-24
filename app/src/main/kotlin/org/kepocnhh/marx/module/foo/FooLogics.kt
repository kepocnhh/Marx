package org.kepocnhh.marx.module.foo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.module.app.Injection
import org.kepocnhh.marx.provider.Synchronizer
import org.kepocnhh.marx.util.minus
import sp.kx.logics.Logics
import java.math.BigInteger
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class FooLogics(
    private val injection: Injection,
) : Logics(injection.contexts.main) {
    data class State(
        val items: List<Foo>,
    )

    private val logger = injection.loggers.create("[Foo]")

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    private val synchronizer = Synchronizer(
        storage = injection.locals.foo,
        remotes = injection.remotes,
        transformer = injection.serializer.foo,
    )

    val syncBroadcast = synchronizer.broadcast
    val syncState = synchronizer.state

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

    fun requestItems() = launch {
        val items = withContext(injection.contexts.default) {
            injection.locals.foo.items
        }
        _state.emit(State(items = items))
    }

    fun deleteItem(id: UUID) = launch {
        val items = withContext(injection.contexts.default) {
            injection.locals.foo.items - { it.id == id }
        }
        withContext(injection.contexts.default) {
            injection.locals.foo.update(items = items, updated = System.currentTimeMillis().milliseconds)
        }
        _state.emit(State(items = items))
    }

    fun addItem(text: String) = launch {
        logger.debug("add item: \"$text\"...")
        val items = withContext(injection.contexts.default) {
            injection.locals.foo.items + Foo(
                id = UUID.randomUUID(),
                created = System.currentTimeMillis().milliseconds,
                text = text,
            )
        }
        withContext(injection.contexts.default) {
            injection.locals.foo.update(items = items, updated = System.currentTimeMillis().milliseconds)
        }
        _state.emit(State(items = items))
    }

    fun sync() = launch {
        logger.debug("sync...")
        withContext(injection.contexts.default) {
            synchronizer.sync()
        }
    }
}
