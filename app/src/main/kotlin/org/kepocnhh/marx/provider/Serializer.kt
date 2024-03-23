package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta

internal interface Serializer {
    interface Transformer<T : Any> {
        fun toValue(bytes: ByteArray): T
        fun toList(bytes: ByteArray): List<T>
        fun toByteArray(value: T): ByteArray
        fun toByteArray(list: List<T>): ByteArray
    }

    val meta: Transformer<Meta>
    val foo: Transformer<Foo>
}
