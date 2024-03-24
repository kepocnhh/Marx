package org.kepocnhh.marx.provider

import android.content.Context
import android.content.SharedPreferences
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

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
                    val meta = Meta(
                        id = Foo.META_ID,
                        updated = System.currentTimeMillis().milliseconds,
                        hash = ""
                    )
                    // todo hash
                    preferences.edit()
                        .putString("${Foo.META_ID}:meta", String(serializer.meta.encode(meta)))
                        .commit()
                    return meta
                }
                return serializer.meta.decode(json.toByteArray())
            }

        override val items: List<Foo>
            get() {
                val json = preferences.getString(Foo.META_ID.toString(), null)
                if (json == null) return emptyList()
                return serializer.foo.list.decode(json.toByteArray())
            }

        override fun update(items: List<Foo>, updated: Duration) {
            val meta = meta.copy(
                updated = updated,
            )
            // todo hash
            preferences.edit()
                .putString(Foo.META_ID.toString(), String(serializer.foo.list.encode(items)))
                .putString("${Foo.META_ID}:meta", String(serializer.meta.encode(meta)))
                .putLong("${Foo.META_ID}:synchronized", System.currentTimeMillis())
                .putBoolean("${Foo.META_ID}:modified", false)
                .commit()
        }

        override val synchronized: Duration?
            get() {
                val key = "${Foo.META_ID}:synchronized"
                if (!preferences.contains(key)) return null
                val time = preferences.getLong("${Foo.META_ID}:synchronized", -1)
                if (time < 0) return null
                return time.milliseconds
            }

        override val modified: Boolean
            get() {
                val key = "${Foo.META_ID}:modified"
                return preferences.getBoolean(key, false)
            }

        override fun detach() {
            preferences.edit()
                .remove("${Foo.META_ID}:synchronized")
                .putBoolean("${Foo.META_ID}:modified", true)
                .commit()
        }

        override fun update(items: List<Foo>) {
            val meta = meta.copy(
                updated = System.currentTimeMillis().milliseconds,
            )
            // todo hash
            preferences.edit()
                .putString(Foo.META_ID.toString(), String(serializer.foo.list.encode(items)))
                .putString("${Foo.META_ID}:meta", String(serializer.meta.encode(meta)))
                .putBoolean("${Foo.META_ID}:modified", true)
                .commit()
        }
    }

    companion object {
        private const val VERSION = 11
    }
}
