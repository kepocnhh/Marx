package org.kepocnhh.marx.provider

import org.json.JSONArray
import org.json.JSONObject

internal interface JsonObjectTransformer<T : Any> {
    fun decode(encoded: JSONObject): T
    fun encode(value: T): JSONObject
}

internal class JsonTransformer<T : Any>(
    private val delegate: JsonObjectTransformer<T>,
) : Transformer<T> {
    override fun decode(encoded: ByteArray): T {
        return delegate.decode(JSONObject(String(encoded)))
    }

    override fun encode(value: T): ByteArray {
        return delegate.encode(value).toString().toByteArray()
    }
}

private class JsonArrayTransformer<T : Any>(
    private val delegate: JsonObjectTransformer<T>,
) : Transformer<List<T>> {
    override fun decode(encoded: ByteArray): List<T> {
        val array = JSONArray(String(encoded))
        return (0 until array.length()).map { index ->
            delegate.decode(array.getJSONObject(index))
        }
    }

    override fun encode(value: List<T>): ByteArray {
        return JSONArray().also { array ->
            value.forEach { value ->
                array.put(delegate.encode(value))
            }
        }.toString().toByteArray()
    }
}

internal class JsonListTransformer<T : Any>(
    private val delegate: JsonObjectTransformer<T>,
) : ListTransformer<T> {
    override fun decode(encoded: ByteArray): T {
        return delegate.decode(JSONObject(String(encoded)))
    }

    override fun encode(value: T): ByteArray {
        return delegate.encode(value).toString().toByteArray()
    }

    override val list: Transformer<List<T>> = JsonArrayTransformer(delegate)
}
