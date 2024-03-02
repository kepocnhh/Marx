package org.kepocnhh.marx.entity

import java.util.UUID
import kotlin.time.Duration

internal data class Bar(
    val id: UUID,
    val date: Duration,
)
