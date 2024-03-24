package org.kepocnhh.marx.entity

import java.util.UUID
import kotlin.time.Duration

data class Meta(
    val id: UUID,
    val updated: Duration,
    val hash: String,
)
