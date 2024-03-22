package org.kepocnhh.marx.module.sync

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse
import org.kepocnhh.marx.module.app.Injection
import org.kepocnhh.marx.util.Single
import org.kepocnhh.marx.util.withCatching
import sp.kx.logics.Logics
import java.util.UUID

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

    private val logger = injection.loggers.create("[Sync]")

    private suspend fun itemsSync(meta: Meta, response: ItemsSyncResponse) {
        when (response) {
            is ItemsSyncResponse.Download -> TODO()
            ItemsSyncResponse.NotModified -> {
                _broadcast.emit(Broadcast.OnSuccess(modified = false))
            }
            is ItemsSyncResponse.UploadSession -> {
                val result = withCatching(injection.contexts.default) {
                    injection.remotes.itemsUpload(
                        sessionId = response.sessionId,
                        bytes = injection.serializer.serialize(meta), // todo
                    )
                }
                when (result) {
                    is Single.Failure -> {
                        logger.warning("items upload error: ${result.error}")
                        _broadcast.emit(Broadcast.OnError(result.error))
                    }
                    is Single.Success -> {
                        _broadcast.emit(Broadcast.OnSuccess(modified = false))
                    }
                }
            }
        }
    }

    private suspend fun itemsSync(meta: Meta) {
        val result = withCatching(injection.contexts.default) {
            injection.remotes.itemsSync(meta)
        }
        when (result) {
            is Single.Failure -> {
                logger.warning("items sync error: ${result.error}")
                _broadcast.emit(Broadcast.OnError(result.error))
            }
            is Single.Success -> {
                itemsSync(meta, result.value)
            }
        }
    }

    fun itemsSync(metaId: UUID) = launch {
        logger.debug("items sync...")
        _state.emit(State(loading = true))
        val meta = withContext(injection.contexts.default) {
            injection.locals.metas.firstOrNull { it.id == metaId } ?: TODO()
        }
        itemsSync(meta = meta)
        _state.emit(State(loading = false))
    }
}
