package org.kepocnhh.marx.entity.remote

import java.util.UUID
import kotlin.time.Duration

internal sealed interface ItemsSyncResponse {
    data class UploadSession(val sessionId: UUID) : ItemsSyncResponse
    class Download(
        val updated: Duration,
        val bytes: ByteArray,
    ) : ItemsSyncResponse
    data object NotModified : ItemsSyncResponse
}
