package org.kepocnhh.marx.util

import android.content.SharedPreferences
import android.util.Base64
import org.kepocnhh.marx.provider.Serializer
import kotlin.reflect.KProperty

internal class ListDelegate<T : Any>(
    private val preferences: SharedPreferences,
    private val key: String,
    private val transformer: Serializer.Transformer<T>,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return preferences.getList(key, transformer::toList)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: List<T>) {
        preferences.putList(key, value, transformer::toByteArray)
    }

    private fun <T : Any> SharedPreferences.putList(
        key: String,
        items: List<T>,
        transform: (List<T>) -> ByteArray,
    ) {
        val base64 = Base64.encodeToString(transform(items), Base64.NO_WRAP)
        edit()
            .putString(key, base64)
            .commit()
    }

    private fun <T : Any> SharedPreferences.getList(
        key: String,
        transform: (ByteArray) -> List<T>,
    ): List<T> {
        val base64 = getString(key, null) ?: return emptyList()
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        return transform(bytes)
    }
}
