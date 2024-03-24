package org.kepocnhh.marx.provider

import android.content.Context
import android.content.SharedPreferences
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import java.math.BigInteger
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class FinalLocals(
    context: Context,
    serializer: Serializer,
    security: Security,
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
        private fun Meta.updated(
            updated: Duration,
            bytes: ByteArray,
        ): Meta {
            return copy(
                updated = updated,
                hash = sha256(
                    id = id,
                    updated = updated,
                    bytes = bytes,
                )
            )
        }

        private fun sha256(
            id: UUID,
            updated: Duration,
            bytes: ByteArray,
        ): String {
            val hash = StringBuilder()
                .append(id)
                .append(updated.inWholeMilliseconds)
                .toString()
                .toByteArray()
                .plus(bytes)
            return String.format("%064x", BigInteger(1, security.sha256(hash)))
        }

        override val meta: Meta
            get() {
                val json = preferences.getString("${Foo.META_ID}:meta", null)
                if (json == null) {
                    val updated = System.currentTimeMillis().milliseconds
                    val meta = Meta(
                        id = Foo.META_ID,
                        updated = updated,
                        hash = "",
                    ).updated(updated = updated, bytes = ByteArray(0))
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
            val bytes = serializer.foo.list.encode(items)
            val meta = meta.updated(
                updated = updated,
                bytes = bytes,
            )
            preferences.edit()
                .putString(Foo.META_ID.toString(), String(bytes))
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
            val bytes = serializer.foo.list.encode(items)
            val meta = meta.updated(
                updated = System.currentTimeMillis().milliseconds,
                bytes = bytes,
            )
            preferences.edit()
                .putString(Foo.META_ID.toString(), String(bytes))
                .putString("${Foo.META_ID}:meta", String(serializer.meta.encode(meta)))
                .putBoolean("${Foo.META_ID}:modified", true)
                .commit()
        }
    }

    companion object {
        private const val VERSION = 12
    }
}
