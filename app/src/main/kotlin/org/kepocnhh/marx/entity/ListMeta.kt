package org.kepocnhh.marx.entity

import java.util.UUID
import kotlin.time.Duration

internal data class ListMeta<T : Any>(
    val id: UUID,
    val updated: Duration,
    val hash: String,
    val items: List<T>,
)
