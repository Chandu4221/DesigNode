package io.github.chandu4221.designode

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.chandu4221.designode.ui.App
import io.github.chandu4221.designode.ui.EditorViewModel

fun main() = application {
    val viewModel = EditorViewModel()
    Window(
        onCloseRequest = ::exitApplication,
        title = "DesigNode",
    ) {
        App(viewModel = viewModel)
    }
}