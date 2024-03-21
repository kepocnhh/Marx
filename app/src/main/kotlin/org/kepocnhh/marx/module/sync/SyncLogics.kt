package org.kepocnhh.marx.module.sync

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse
import org.kepocnhh.marx.module.app.Injection
import sp.kx.logics.Logics

internal class SyncLogics(
    private val injection: Injection,
) : Logics(injection.contexts.main) {
    sealed interface Broadcast {
        data class OnError(val error: Throwable) : Broadcast
        data class OnSuccess(val modified: Boolean) : Broadcast
    }

    data class State(
        val loading: Boolean,
    )

    private val _state = MutableStateFlow(State(loading = false))
    val state = _state.asStateFlow()

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private suspend fun itemsSync(result: Result<ItemsSyncResponse>) {
        val error = withContext(injection.contexts.default) {
            result.exceptionOrNull()
        }
        if (error != null) {
            _broadcast.emit(Broadcast.OnError(error))
            return
        }
        val response = withContext(injection.contexts.default) {
            result.getOrNull() ?: TODO()
        }
        when (response) {
            ItemsSyncResponse.NotModified -> {
                _broadcast.emit(Broadcast.OnSuccess(modified = false))
                return
            }
            is ItemsSyncResponse.UploadSession -> TODO()
        }
    }

    fun itemsSync(meta: Meta) = launch {
        _state.emit(State(loading = true))
        val result = withContext(injection.contexts.default) {
            runCatching {
                injection.remotes.itemsSync(meta)
            }
        }
        itemsSync(result)
        _state.emit(State(loading = false))
    }
}
