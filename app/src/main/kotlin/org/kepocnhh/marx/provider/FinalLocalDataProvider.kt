package org.kepocnhh.marx.provider

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Foo
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class FinalLocalDataProvider(
    context: Context,
) : LocalDataProvider {
    private val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    override var foo: List<Foo>
        get() {
            val json = preferences.getString("foo", "[]")
            val array = JSONArray(json)
            val result = mutableListOf<Foo>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val item = Foo(
                    id = UUID.fromString(obj.getString("id")),
                    text = obj.getString("text"),
                    created = obj.getLong("created").milliseconds
                )
                result.add(item)
            }
            return result
        }
        set(value) {
            val array = JSONArray()
            value.forEach {
                val obj = JSONObject()
                obj.put("id", it.id.toString())
                obj.put("text", it.text)
                obj.put("created", it.created.inWholeMilliseconds)
                array.put(obj)
            }
            preferences.edit().putString("foo", array.toString()).commit()
        }
    override var bar: List<Bar>
        get() {
            val json = preferences.getString("bar", "[]")
            val array = JSONArray(json)
            val result = mutableListOf<Bar>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val item = Bar(
                    id = UUID.fromString(obj.getString("id")),
                    count = obj.getInt("count"),
                    created = obj.getLong("created").milliseconds
                )
                result.add(item)
            }
            return result
        }
        set(value) {
            val array = JSONArray()
            value.forEach {
                val obj = JSONObject()
                obj.put("id", it.id.toString())
                obj.put("count", it.count)
                obj.put("created", it.created.inWholeMilliseconds)
                array.put(obj)
            }
            preferences.edit().putString("bar", array.toString()).commit()
        }

}
