package org.kepocnhh.marx

import android.app.Application
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.provider.LocalDataProvider
import org.kepocnhh.marx.util.compose.LocalOnBackPressedDispatcher
import java.util.UUID

internal class App : Application() {
    object Theme {
        @Composable
        fun Composition(
            onBackPressedDispatcher: OnBackPressedDispatcher,
            content: @Composable () -> Unit,
        ) {
            CompositionLocalProvider(
                LocalOnBackPressedDispatcher provides onBackPressedDispatcher,
                content = content,
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        // todo
    }

    private class MockLocalDataProvider(override var foo: List<Foo>) : LocalDataProvider // todo

    companion object {
        val ldp: LocalDataProvider = MockLocalDataProvider(
            foo = (1..20).map { number ->
                Foo(id = UUID.randomUUID(), text = "number: $number")
            }
        ) // todo
    }
}
