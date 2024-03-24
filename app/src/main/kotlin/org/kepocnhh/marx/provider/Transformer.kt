package org.kepocnhh.marx.provider

internal interface Transformer<T : Any> {
    fun decode(encoded: ByteArray): T
    fun encode(value: T): ByteArray
}

internal interface ListTransformer<T : Any> : Transformer<T> {
    val list: Transformer<List<T>>
}
