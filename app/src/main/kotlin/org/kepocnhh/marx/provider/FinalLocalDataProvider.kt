package org.kepocnhh.marx.provider

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import java.util.Date
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class FinalLocalDataProvider(
    context: Context,
) : LocalDataProvider {
    private val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    init {
        val version = preferences.getInt("version", -1)
        if (version < 0) {
            preferences.edit().putInt("version", Version).commit()
        } else if (version < Version) {
            preferences
                .edit()
                .clear()
                .putInt("version", Version)
                .commit()
        }
    }

    override var foo: List<Foo>
        get() {
            return preferences.getList { it.toFoo() }
        }
        set(value) {
            preferences.putList(value) { it.toJSONObject() }
        }

    override var bar: List<Bar>
        get() {
            return preferences.getList { it.toBar() }
        }
        set(value) {
            preferences.putList(value) { it.toJSONObject() }
        }

    companion object {
        private const val Version = 2

        private fun JSONObject.toBar(): Bar {
            return Bar(
                id = UUID.fromString(getString("id")),
                count = getInt("count"),
                created = getLong("created").milliseconds,
            )
        }

        private fun Bar.toJSONObject(): JSONObject {
            return JSONObject()
                .put("id", id.toString())
                .put("count", count)
                .put("created", created.inWholeMilliseconds)
        }

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

        private fun SharedPreferences.getSupported(): Map<String, String> {
            val json = getString("supported", null) ?: return emptyMap()
            val obj = JSONObject(json)
            return obj.keys().asSequence().map { key ->
                key to obj.getString(key)
            }.toMap()
        }

        private inline fun <reified T : Any> SharedPreferences.putList(
            items: List<T>,
            transform: (T) -> JSONObject,
        ) {
            val type = T::class.java
            val array = JSONArray()
            items.forEach {
                array.put(transform(it))
            }
            val updated = System.currentTimeMillis().milliseconds
            val listMeta = getMetaOrCreate(type)
                .copy(
                    updated = updated,
                    hash = array.toString().hashCode().toString(),
                )
            val supported = getSupported().toMutableMap().also {
                it[listMeta.id.toString()] = listMeta.hash
            }.toMap()
            println("supported: $supported") // todo
            val hash = supported.toList().sortedBy { (id, _) ->
                id
            }.joinToString(separator = "") { (_, hash) ->
                hash
            }.hashCode().toString()
            val meta = getMetaOrCreate(Meta::class.java)
                .copy(
                    updated = updated,
                    hash = hash,
                )
            println("meta:${type.name}: $listMeta") // todo
            println("meta: $meta") // todo
            edit()
                .putString("supported", JSONObject(supported).toString())
                .putString("list:${type.name}", array.toString())
                .putString("meta:${type.name}", listMeta.toJSONObject().toString())
                .putString("meta:${Meta::class.java.name}", meta.toJSONObject().toString())
                .commit()
        }

        private inline fun <reified T : Any> SharedPreferences.getList(
            transform: (JSONObject) -> T,
        ): List<T> {
            val type = T::class.java
            val json = getString("list:${type.name}", null)
                ?: return emptyList()
            val array = JSONArray(json)
            val items = mutableListOf<T>()
            for (i in 0 until array.length()) {
                items.add(transform(array.getJSONObject(i)))
            }
            return items
        }

        private fun println(title: String, meta: Meta) {
            // todo
            val message = """
                $title
                id: ${meta.id}
                created: ${Date(meta.created.inWholeMilliseconds)}
                updated: ${Date(meta.updated.inWholeMilliseconds)}
                hash: ${meta.hash}
            """.trimIndent()
            println(message)
        }

        private fun <T : Any> SharedPreferences.getMetaOrCreate(
            type: Class<T>,
        ): Meta = synchronized(this) {
            val key = "meta:${type.name}"
            val json = getString(key, null)
            if (json == null) {
                val created = System.currentTimeMillis().milliseconds
                val meta = Meta(
                    id = UUID.randomUUID(),
                    created = created,
                    updated = created,
                    hash = "",
                )
                edit()
                    .putString(key, meta.toJSONObject().toString())
                    .commit()
                println("create:${type.name}", meta) // todo
                return meta
            }
            return JSONObject(json).toMeta()
        }
    }
}
