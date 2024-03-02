package org.kepocnhh.marx.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Foo
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

    override var bar: List<Bar> = emptyList()
}
