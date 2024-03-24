package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Meta
import kotlin.time.Duration

interface Storage<T : Any> {
    val meta: Meta
    val items: List<T>
    fun update(items: List<T>, updated: Duration)
}
