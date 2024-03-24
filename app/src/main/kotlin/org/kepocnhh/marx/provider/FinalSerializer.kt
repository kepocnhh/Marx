package org.kepocnhh.marx.provider

import org.json.JSONObject
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class FinalSerializer : Serializer {
    override val meta: Transformer<Meta> = JsonTransformer(
        object : JsonObjectTransformer<Meta> {
            override fun decode(encoded: JSONObject): Meta {
                return Meta(
                    id = UUID.fromString(encoded.getString("id")),
                    updated = encoded.getLong("updated").milliseconds,
                    hash = encoded.getString("hash"),
                )
            }

            override fun encode(value: Meta): JSONObject {
                return JSONObject()
                    .put("id", value.id.toString())
                    .put("updated", value.updated.inWholeMilliseconds)
                    .put("hash", value.hash)
            }
        },
    )

    override val foo: ListTransformer<Foo> = JsonListTransformer(
        object : JsonObjectTransformer<Foo> {
            override fun decode(encoded: JSONObject): Foo {
                return Foo(
                    id = UUID.fromString(encoded.getString("id")),
                    text = encoded.getString("text"),
                    created = encoded.getLong("created").milliseconds,
                )
            }

            override fun encode(value: Foo): JSONObject {
                return JSONObject()
                    .put("id", value.id.toString())
                    .put("text", value.text)
                    .put("created", value.created.inWholeMilliseconds)
            }
        },
    )
}
