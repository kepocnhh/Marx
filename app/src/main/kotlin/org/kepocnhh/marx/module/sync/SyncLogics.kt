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

    private suspend fun itemsUpload(meta: Meta, sessionId: UUID) {
        withContext(injection.contexts.default) {
            runCatching {
                injection.remotes.itemsUpload(
                    sessionId = sessionId,
                    bytes = injection.serializer.serialize(meta), // todo
                )
            }
        }.fold(
            onSuccess = {
                _broadcast.emit(Broadcast.OnSuccess(modified = false))
            },
            onFailure = { error ->
                logger.warning("items upload error: $error")
                _broadcast.emit(Broadcast.OnError(error))
            },
        )
    }

    private suspend fun itemsSync(meta: Meta, response: ItemsSyncResponse) {
        when (response) {
            is ItemsSyncResponse.Download -> TODO()
            ItemsSyncResponse.NotModified -> {
                _broadcast.emit(Broadcast.OnSuccess(modified = false))
            }
            is ItemsSyncResponse.UploadSession -> {
                itemsUpload(meta, sessionId = response.sessionId)
            }
        }
    }

    private suspend fun itemsSync(meta: Meta) {
        withContext(injection.contexts.default) {
            runCatching {
                injection.remotes.itemsSync(meta)
            }
        }.fold(
            onSuccess = { response ->
                itemsSync(meta, response)
            },
            onFailure = { error ->
                logger.warning("items sync error: $error")
                _broadcast.emit(Broadcast.OnError(error))
            },
        )
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
