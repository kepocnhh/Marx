package org.kepocnhh.marx.provider

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Baz
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.util.MutableStorage
import java.util.UUID

internal class FinalLocalDataProvider(
    context: Context,
) : LocalDataProvider {
    private val helper = object : SQLiteOpenHelper(context, "marx", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            checkNotNull(db)
            val sql = """
                CREATE TABLE Foo(id TEXT PRIMARY KEY, text TEXT)
            """.trimIndent()
            db.execSQL(sql)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            // todo
        }
    }

    private val pref = context.getSharedPreferences("foo", Context.MODE_PRIVATE)!!

    override val foo: MutableStorage<Foo> = object : MutableStorage<Foo> {
        override fun add(item: Foo) {
            val values = ContentValues()
            values.put("id", item.id.toString())
            values.put("text", item.text)
            helper.writableDatabase.insert("Foo", null, values)
        }

        private fun Cursor.toItem(): Foo {
            return Foo(
                id = getString(getColumnIndexOrThrow("id")).let(UUID::fromString),
                text = getString(getColumnIndexOrThrow("text")),
            )
        }

        private fun Cursor.firstOrNull(predicate: (Foo) -> Boolean): Foo? {
            while (moveToNext()) {
                val item = toItem()
                if (predicate(item)) return item
            }
            return null
        }

        override fun removeFirst(predicate: (Foo) -> Boolean) {
            val item = helper.readableDatabase.query(
                "Foo",
                arrayOf("id", "text"),
                null,
                null,
                null,
                null,
                null,
            ).use {
                it.firstOrNull(predicate)
            } ?: return
            helper.writableDatabase.delete("Foo", "id LIKE ?", arrayOf(item.id.toString()))
        }

        override val size: Int
            get() {
                return helper.readableDatabase.rawQuery(
                    "SELECT COUNT(*) FROM Foo",
                    null,
                ).use { cursor ->
                    check(cursor.moveToNext())
                    cursor.getInt(0)
                }
            }

        override fun iterator(): Iterator<Foo> {
            return helper.readableDatabase.query(
                "Foo",
                arrayOf("id", "text"),
                null,
                null,
                null,
                null,
                null,
            ).use { cursor ->
                val result = mutableListOf<Foo>()
                while (cursor.moveToNext()) {
                    val item = Foo(
                        id = cursor.getString(cursor.getColumnIndexOrThrow("id")).let(UUID::fromString),
                        text = cursor.getString(cursor.getColumnIndexOrThrow("text")),
                    )
                    result.add(item)
                }
                result
            }.iterator()
        }
    }

    override var bar: List<Bar> = emptyList()
}
