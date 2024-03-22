package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta

internal interface Serializer {
    fun toMeta(bytes: ByteArray): Meta
    fun serialize(value: Meta): ByteArray
    fun toFoo(bytes: ByteArray): Foo
    fun serialize(value: Foo): ByteArray
}
