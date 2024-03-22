package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse
import java.util.UUID

internal interface Remotes {
    fun itemsSync(meta: Meta): ItemsSyncResponse
    fun itemsUpload(sessionId: UUID, bytes: ByteArray)
}
