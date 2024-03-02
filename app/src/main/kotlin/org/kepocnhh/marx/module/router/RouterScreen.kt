package org.kepocnhh.marx.module.router

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.kepocnhh.marx.module.foo.FooScreen
import org.kepocnhh.marx.util.compose.ColumnButton

private object RouterScreen {
    enum class Type { FOO, BAR }
    data class State(val type: Type?)
}

@Composable
internal fun RouterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val state = remember { mutableStateOf(RouterScreen.State(null)) }
        when (state.value.type) {
            RouterScreen.Type.FOO -> {
                FooScreen(
                    onBack = {
                        state.value = state.value.copy(type = null)
                    },
                )
            }
            RouterScreen.Type.BAR -> TODO()
            null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                ) {
                    ColumnButton(
                        text = "foo",
                        onClick = {
                            state.value = state.value.copy(type = RouterScreen.Type.FOO)
                        },
                    )
                    ColumnButton(
                        text = "bar",
                        onClick = {
                            state.value = state.value.copy(type = RouterScreen.Type.BAR)
                        },
                    )
                }
            }
        }
    }
}
