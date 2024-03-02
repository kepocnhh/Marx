package org.kepocnhh.marx.provider

import android.content.ContentValues
import android.database.Cursor

internal fun <T : Any> Cursor.toMutableList(
    toItem: (Cursor) -> T,
): MutableList<T> {
    val result = mutableListOf<T>()
    while (moveToNext()) {
        val item = toItem(this)
        result.add(item)
    }
    return result
}

internal fun <T : Any> Cursor.firstOrNull(
    predicate: (T) -> Boolean,
    toItem: (Cursor) -> T,
): T? {
    while (moveToNext()) {
        val item = toItem(this)
        if (predicate(item)) return item
    }
    return null
}

internal fun Cursor.getString(columnName: String): String {
    return getString(getColumnIndexOrThrow(columnName))
}

internal fun contentValuesOf(
    first: Pair<String, String>,
    vararg other: Pair<String, String>,
): ContentValues {
    val values = ContentValues()
    values.put(first.first, first.second)
    for ((key, value) in other) {
        values.put(key, value)
    }
    return values
}
