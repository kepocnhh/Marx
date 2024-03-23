package org.kepocnhh.marx.provider

import org.json.JSONArray
import org.json.JSONObject
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class FinalSerializer : Serializer {
    private abstract class JsonTransformer<T : Any> : Serializer.Transformer<T> {
        override fun toValue(bytes: ByteArray): T {
            return fromJSONObject(JSONObject(String(bytes)))
        }

        override fun toList(bytes: ByteArray): List<T> {
            val array = JSONArray(String(bytes))
            return (0 until array.length()).map { index ->
                fromJSONObject(array.getJSONObject(index))
            }
        }

        override fun toByteArray(value: T): ByteArray {
            return toJSONObject(value).toString().toByteArray()
        }

        override fun toByteArray(list: List<T>): ByteArray {
            return JSONArray().also { array ->
                list.forEach { value ->
                    array.put(toJSONObject(value))
                }
            }.toString().toByteArray()
        }

        abstract fun toJSONObject(value: T): JSONObject
        abstract fun fromJSONObject(obj: JSONObject): T
    }

    override val meta: Serializer.Transformer<Meta> = object : JsonTransformer<Meta>() {
        override fun toJSONObject(value: Meta): JSONObject {
            return JSONObject()
                .put("id", value.id.toString())
                .put("created", value.created.inWholeMilliseconds)
                .put("updated", value.updated.inWholeMilliseconds)
                .put("hash", value.hash)
        }

        override fun fromJSONObject(obj: JSONObject): Meta {
            return Meta(
                id = UUID.fromString(obj.getString("id")),
                created = obj.getLong("created").milliseconds,
                updated = obj.getLong("updated").milliseconds,
                hash = obj.getString("hash"),
            )
        }
    }

    override val foo: Serializer.Transformer<Foo> = object : JsonTransformer<Foo>() {
        override fun toJSONObject(value: Foo): JSONObject {
            return JSONObject()
                .put("id", value.id.toString())
                .put("text", value.text)
                .put("created", value.created.inWholeMilliseconds)
        }

        override fun fromJSONObject(obj: JSONObject): Foo {
            return Foo(
                id = UUID.fromString(obj.getString("id")),
                text = obj.getString("text"),
                created = obj.getLong("created").milliseconds,
            )
        }
    }
}
