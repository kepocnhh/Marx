package org.kepocnhh.marx.module.foo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.module.app.Injection
import sp.kx.logics.Logics
import java.util.Arrays
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class FooLogics(
    private val injection: Injection,
) : Logics(injection.contexts.main) {
    data class State(
        val meta: Meta,
        val items: List<Foo>,
    )

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    private fun <T : Any> List<T>.toByteArray(): ByteArray {
        return ByteArray(1) { index ->
            (size + index).toByte()
        }
    }

    private fun Meta.update(body: ByteArray): Meta {
        return copy(
            updated = System.currentTimeMillis().milliseconds,
            hash = body.contentHashCode().toString(),
        )
    }

    fun requestItems() = launch {
        val items = withContext(injection.contexts.default) {
            injection.locals.foo
        }
        val meta = withContext(injection.contexts.default) {
            injection.locals.metas.firstOrNull {
                it.id == Foo.META_ID
            } ?: System.currentTimeMillis().milliseconds.let { created ->
                val meta = Meta(
                    id = Foo.META_ID,
                    created = created,
                    updated = created,
                    hash = "",
                )
                injection.locals.metas += meta
                meta
            }
        }
        _state.emit(State(meta, items))
    }

    fun deleteItem(id: UUID) = launch {
        withContext(injection.contexts.default) {
            injection.locals.foo = injection.locals.foo
                .toMutableList()
                .also { list ->
                    list.removeIf { it.id == id }
                }
        }
        val items = withContext(injection.contexts.default) {
            injection.locals.foo
        }
        val meta = withContext(injection.contexts.default) {
            injection.locals.metas.firstOrNull {
                it.id == Foo.META_ID
            }?.update(body = items.toByteArray()) ?: TODO()
        }
        _state.emit(State(meta, items))
    }

    fun addItem(text: String) = launch {
        withContext(injection.contexts.default) {
            val item = Foo(
                id = UUID.randomUUID(),
                created = System.currentTimeMillis().milliseconds,
                text = text,
            )
            injection.locals.foo = injection.locals.foo
                .toMutableList()
                .also { list ->
                    list.add(item)
                }
        }
        val items = withContext(injection.contexts.default) {
            injection.locals.foo
        }
        val meta = withContext(injection.contexts.default) {
            injection.locals.metas.firstOrNull {
                it.id == Foo.META_ID
            }?.update(body = items.toByteArray()) ?: TODO()
        }
        _state.emit(State(meta, items))
    }
}
