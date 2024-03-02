package org.kepocnhh.marx.entity

import java.util.UUID

internal data class Baz(
    val id: UUID,
    val barId: UUID,
    val text: String,
)
