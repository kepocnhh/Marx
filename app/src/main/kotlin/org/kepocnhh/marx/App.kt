package org.kepocnhh.marx

import android.app.Application
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.entity.remote.ItemsSyncResponse
import org.kepocnhh.marx.module.app.Injection
import org.kepocnhh.marx.provider.Contexts
import org.kepocnhh.marx.provider.FinalLocals
import org.kepocnhh.marx.provider.Remotes
import org.kepocnhh.marx.util.compose.LocalOnBackPressedDispatcher
import sp.kx.logics.Logics
import sp.kx.logics.LogicsFactory
import sp.kx.logics.LogicsProvider
import sp.kx.logics.contains
import sp.kx.logics.get
import sp.kx.logics.remove

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

    private object MockRemotes : Remotes {
        override fun itemsSync(meta: Meta): ItemsSyncResponse {
            TODO("Not yet implemented: itemsSync")
        }
    }

    override fun onCreate() {
        super.onCreate()
        _injection = Injection(
            contexts = Contexts(
                main = Dispatchers.Main,
                default = Dispatchers.Default,
            ),
            locals = FinalLocals(this),
            remotes = MockRemotes,
        )
    }

    companion object {
        private var _injection: Injection? = null

        private val _logicsProvider = LogicsProvider(
            factory = object : LogicsFactory {
                override fun <T : Logics> create(type: Class<T>): T {
                    val injection = checkNotNull(_injection) { "No injection!" }
                    return type
                        .getConstructor(Injection::class.java)
                        .newInstance(injection)
                }
            },
        )

        @Composable
        inline fun <reified T : Logics> logics(label: String = T::class.java.name): T {
            val (contains, logic) = synchronized(App::class.java) {
                remember { _logicsProvider.contains<T>(label = label) } to _logicsProvider.get<T>(label = label)
            }
            DisposableEffect(Unit) {
                onDispose {
                    synchronized(App::class.java) {
                        if (!contains) {
                            _logicsProvider.remove<T>(label = label)
                        }
                    }
                }
            }
            return logic
        }
    }
}
