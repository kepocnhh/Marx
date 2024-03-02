package org.kepocnhh.marx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import org.kepocnhh.marx.module.router.RouterScreen
import org.kepocnhh.marx.util.compose.BackHandler

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ComposeView(this)
        setContentView(view)
        view.setContent {
            App.Theme.Composition(
                onBackPressedDispatcher = onBackPressedDispatcher,
            ) {
                BackHandler {
                    finish()
                }
                RouterScreen()
            }
        }
    }
}
