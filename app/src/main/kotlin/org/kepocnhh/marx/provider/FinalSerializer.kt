package org.kepocnhh.marx.provider

import org.json.JSONObject
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class FinalSerializer : Serializer {
    override fun toMeta(bytes: ByteArray): Meta {
        return JSONObject(String(bytes)).toMeta()
    }

    override fun serialize(value: Meta): ByteArray {
        return value.toJSONObject().toString().toByteArray()
    }

    override fun serialize(value: Foo): ByteArray {
        return value.toJSONObject().toString().toByteArray()
    }

    override fun toFoo(bytes: ByteArray): Foo {
        return JSONObject(String(bytes)).toFoo()
    }

    companion object {
        private fun JSONObject.toFoo(): Foo {
            return Foo(
                id = UUID.fromString(getString("id")),
                text = getString("text"),
                created = getLong("created").milliseconds,
            )
        }

        private fun Foo.toJSONObject(): JSONObject {
            return JSONObject()
                .put("id", id.toString())
                .put("text", text)
                .put("created", created.inWholeMilliseconds)
        }

        private fun Meta.toJSONObject(): JSONObject {
            return JSONObject()
                .put("id", id.toString())
                .put("created", created.inWholeMilliseconds)
                .put("updated", updated.inWholeMilliseconds)
                .put("hash", hash)
        }

        private fun JSONObject.toMeta(): Meta {
            return Meta(
                id = UUID.fromString(getString("id")),
                created = getLong("created").milliseconds,
                updated = getLong("updated").milliseconds,
                hash = getString("hash"),
            )
        }
    }
}
