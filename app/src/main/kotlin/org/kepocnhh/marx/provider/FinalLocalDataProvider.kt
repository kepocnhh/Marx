package org.kepocnhh.marx.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Foo
import java.util.UUID
import kotlin.time.Duration.Companion.nanoseconds

internal class FinalLocalDataProvider(
    context: Context,
) : LocalDataProvider {
    private val helper = object : SQLiteOpenHelper(context, "marx", null, 9) {
        private fun onCreateTables(db: SQLiteDatabase) {
            setOf(
                "CREATE TABLE IF NOT EXISTS Foo(id TEXT PRIMARY KEY, text TEXT)",
                "CREATE TABLE IF NOT EXISTS Bar(id TEXT PRIMARY KEY, date TEXT)",
            ).forEach(db::execSQL)
        }

        override fun onCreate(db: SQLiteDatabase?) {
            checkNotNull(db)
            onCreateTables(db)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            checkNotNull(db)
            if (oldVersion > newVersion) TODO()
            if (newVersion > oldVersion) {
                onCreateTables(db)
            }
        }
    }

    override val foo = SQLMutableList(
        helper = helper,
        tableName = "Foo",
        args = arrayOf("id", "text"),
        toContentValues = {
            contentValuesOf(
                "id" to it.id.toString(),
                "text" to it.text,
            )
        },
        toItem = {
            Foo(
                id = it.getString("id").let(UUID::fromString),
                text = it.getString("text"),
            )
        },
    )

    override val bar = SQLMutableList(
        helper = helper,
        tableName = "Bar",
        args = arrayOf("id", "date"),
        toContentValues = {
            contentValuesOf(
                "id" to it.id.toString(),
                "date" to it.date.inWholeNanoseconds.toString(),
            )
        },
        toItem = {
            Bar(
                id = it.getString("id").let(UUID::fromString),
                date = it.getString("date").toLong().nanoseconds,
            )
        },
    )
}
