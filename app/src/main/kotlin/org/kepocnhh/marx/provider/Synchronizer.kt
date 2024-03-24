package org.kepocnhh.marx.provider

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse

internal class Synchronizer<T : Any>(
    private val storage: Storage<T>,
    private val remotes: Remotes,
    private val transformer: Transformer<List<T>>,
) {
    sealed interface Broadcast {
        data class OnSyncResult(val result: SyncResult) : Broadcast
    }

    data class State(
        val loading: Boolean,
    )

    sealed interface SyncResult {
        data class OnSuccess(val modified: Boolean) : SyncResult
        data class OnError(val error: Throwable) : SyncResult
    }

    private val _state = MutableStateFlow(State(loading = false))
    val state = _state.asStateFlow()

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private fun onItemsUpload(response: ItemsSyncResponse.UploadSession): SyncResult {
        return runCatching {
            remotes.itemsUpload(
                sessionId = response.sessionId,
                bytes = transformer.encode(storage.items),
            )
        }.fold(
            onSuccess = {
                SyncResult.OnSuccess(modified = false)
            },
            onFailure = {
                SyncResult.OnError(it)
            },
        )
    }

    private fun onItemsDownload(response: ItemsSyncResponse.Download): SyncResult {
        val items = transformer.decode(response.bytes)
        storage.update(items = items, updated = response.updated)
        return SyncResult.OnSuccess(modified = true)
    }

    private fun onResponse(response: ItemsSyncResponse): SyncResult {
        return when (response) {
            is ItemsSyncResponse.Download -> {
                onItemsDownload(response = response)
            }
            ItemsSyncResponse.NotModified -> {
                SyncResult.OnSuccess(modified = false)
            }
            is ItemsSyncResponse.UploadSession -> {
                onItemsUpload(response = response)
            }
        }
    }

    suspend fun sync() {
        _state.emit(State(loading = true))
        val result = runCatching {
            remotes.itemsSync(storage.meta)
        }.map {
            onResponse(it)
        }.getOrElse {
            SyncResult.OnError(it)
        }
        _broadcast.emit(Broadcast.OnSyncResult(result))
        _state.emit(State(loading = false))
    }
}
