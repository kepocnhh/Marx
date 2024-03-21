package org.kepocnhh.marx.module.foo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.module.app.Injection
import sp.kx.logics.Logics
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class FooLogics(
    private val injection: Injection,
) : Logics(injection.contexts.main) {
    data class State(
        val items: List<Foo>,
    )

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    fun requestItems() = launch {
        val items = withContext(injection.contexts.default) {
            injection.locals.foo
        }
        _state.emit(State(items))
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
        _state.emit(State(items))
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
        _state.emit(State(items))
    }
}
