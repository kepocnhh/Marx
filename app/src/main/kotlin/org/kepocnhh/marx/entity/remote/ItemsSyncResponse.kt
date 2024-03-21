package org.kepocnhh.marx.entity.remote

import java.util.UUID

internal sealed interface ItemsSyncResponse {
    data class UploadSession(val sessionId: UUID) : ItemsSyncResponse
    class Download(val body: ByteArray) : ItemsSyncResponse
    data object NotModified : ItemsSyncResponse
}
