package org.kepocnhh.marx.provider

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import java.util.function.Predicate

internal class SQLMutableList<T : Any>(
    private val helper: SQLiteOpenHelper,
    private val tableName: String,
    private val args: Array<String>,
    private val primaryKey: String = "id",
    private val getPrimaryValue: (T) -> String,
    private val toContentValues: (T) -> ContentValues,
    private val toItem: (Cursor) -> T,
) : MutableList<T> {
    private fun <T : Any> Cursor.toMutableList(
        toItem: (Cursor) -> T,
    ): MutableList<T> {
        val result = mutableListOf<T>()
        while (moveToNext()) {
            val item = toItem(this)
            result.add(item)
        }
        return result
    }

    private fun <T : Any> Cursor.firstOrNull(
        predicate: (T) -> Boolean,
        toItem: (Cursor) -> T,
    ): T? {
        while (moveToNext()) {
            val item = toItem(this)
            if (predicate(item)) return item
        }
        return null
    }

    override val size: Int
        get() {
            return helper.readableDatabase.rawQuery(
                "SELECT COUNT(*) FROM $tableName",
                null,
            ).use { cursor ->
                check(cursor.moveToNext())
                cursor.getInt(0)
            }
        }

    override fun clear() {
        TODO("Not yet implemented: clear")
    }

    override fun addAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented: addAll")
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        TODO("Not yet implemented: addAll")
    }

    override fun add(index: Int, element: T) {
        TODO("Not yet implemented: add")
    }

    override fun add(element: T): Boolean {
        val values = toContentValues(element)
        helper.writableDatabase.insert(tableName, null, values)
        return true
    }

    override fun get(index: Int): T {
        TODO("Not yet implemented: get")
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun iterator(): MutableIterator<T> {
        return helper.readableDatabase.query(
            tableName,
            args,
            null,
            null,
            null,
            null,
            null,
        ).use {
            it.toMutableList(toItem)
        }.iterator()
    }

    override fun listIterator(): MutableListIterator<T> {
        TODO("Not yet implemented: listIterator")
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        TODO("Not yet implemented: listIterator")
    }

    override fun removeAt(index: Int): T {
        TODO("Not yet implemented: removeAt")
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO("Not yet implemented: subList")
    }

    override fun set(index: Int, element: T): T {
        TODO("Not yet implemented: set")
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented: retainAll")
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented: removeAll")
    }

    override fun remove(element: T): Boolean {
        TODO("Not yet implemented: remove")
    }

    override fun lastIndexOf(element: T): Int {
        TODO("Not yet implemented: lastIndexOf")
    }

    override fun indexOf(element: T): Int {
        TODO("Not yet implemented: indexOf")
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented: containsAll")
    }

    override fun contains(element: T): Boolean {
        TODO("Not yet implemented: contains")
    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        val item = helper.readableDatabase.query(
            tableName,
            args,
            null,
            null,
            null,
            null,
            null,
        ).use { cursor ->
            cursor.firstOrNull(predicate = {filter.test(it)}, toItem = toItem)
        } ?: return false
        val count = helper.writableDatabase.delete(tableName, "$primaryKey LIKE ?", arrayOf(getPrimaryValue(item)))
        return count == 1
    }
}
