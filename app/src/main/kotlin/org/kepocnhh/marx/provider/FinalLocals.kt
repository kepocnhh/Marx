package org.kepocnhh.marx.provider

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta

internal class FinalLocals(
    private val preferences: SharedPreferences,
    private val serializer: Serializer,
) : Locals {

    init {
        val version = preferences.getInt("version", -1)
        if (version < 0) {
            preferences.edit().putInt("version", VERSION).commit()
        } else if (version < VERSION) {
            preferences
                .edit()
                .clear()
                .putInt("version", VERSION)
                .commit()
        }
    }

    override var foo: List<Foo>
        get() {
            return preferences.getList(serializer::toFoo)
        }
        set(value) {
            preferences.putList(value, serializer::serialize)
        }

    override var metas: List<Meta>
        get() {
            return preferences.getList(serializer::toMeta)
        }
        set(value) {
            preferences.putList(value, serializer::serialize)
        }

    companion object {
        private const val VERSION = 7

        private inline fun <reified T : Any> SharedPreferences.putList(
            items: List<T>,
            transform: (T) -> ByteArray,
        ) {
            val type = T::class.java
            val array = JSONArray()
            items.forEach {
                val bytes = transform(it)
                array.put(JSONObject(String(bytes)))
            }
            edit()
                .putString("list:${type.name}", array.toString())
                .commit()
        }

        private inline fun <reified T : Any> SharedPreferences.getList(
            transform: (ByteArray) -> T,
        ): List<T> {
            val type = T::class.java
            val json = getString("list:${type.name}", null)
                ?: return emptyList()
            val array = JSONArray(json)
            val items = mutableListOf<T>()
            for (i in 0 until array.length()) {
                items.add(transform(array.getJSONObject(i).toString().toByteArray()))
            }
            return items
        }
    }
}
