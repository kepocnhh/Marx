package org.kepocnhh.marx.provider

import android.content.Context
import android.content.SharedPreferences
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import kotlin.time.Duration

internal class FinalLocals(
    context: Context,
    serializer: Serializer,
) : Locals {
    private val preferences: SharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    init {
        if (preferences.getInt("version", -1) < VERSION) {
            preferences
                .edit()
                .clear()
                .putInt("version", VERSION)
                .commit()
        }
    }

    override val foo: Storage<Foo> = object : Storage<Foo> {
        override val meta: Meta
            get() {
                val json = preferences.getString("${Foo.META_ID}:meta", null)
                if (json == null) {
                    TODO()
                }
                return serializer.meta.toValue(json.toByteArray())
            }

        override val items: List<Foo>
            get() {
                val json = preferences.getString(Foo.META_ID.toString(), null)
                if (json == null) return emptyList()
                return serializer.foo.toList(json.toByteArray())
            }

        override fun update(items: List<Foo>, updated: Duration) {
            val meta = meta.copy(
                updated = updated,
            )
            // todo hash
            preferences.edit()
                .putString(Foo.META_ID.toString(), String(serializer.foo.toByteArray(items)))
                .putString("${Foo.META_ID}:meta", String(serializer.meta.toByteArray(meta)))
                .commit()
        }
    }

    companion object {
        private const val VERSION = 10
    }
}
