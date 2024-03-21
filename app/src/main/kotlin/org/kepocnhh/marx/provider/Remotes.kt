package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse

internal interface Remotes {
    fun itemsSync(meta: Meta) : ItemsSyncResponse
}
