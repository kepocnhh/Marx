package org.kepocnhh.marx.provider

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.util.ListDelegate
import java.util.UUID

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

    override var foo: List<Foo> by ListDelegate(preferences, Foo.META_ID.toString(), serializer.foo)
    override var metas: List<Meta> by ListDelegate(preferences, "metas", serializer.meta)

    override fun getByMetaId(id: UUID): ByteArray {
        val base64 = preferences.getString(id.toString(), null)
        checkNotNull(base64)
        return Base64.decode(base64, Base64.DEFAULT)
    }

    companion object {
        private const val VERSION = 8
    }
}
