package org.kepocnhh.marx.entity.remote

import java.util.UUID

internal sealed interface ItemsSyncResponse {
    data class UploadSession(val sessionId: UUID) : ItemsSyncResponse
    data object NotModified : ItemsSyncResponse
}
