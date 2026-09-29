package io.github.chandu4221.designode

import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "DesigNode") {
        Text("DesigNode — composition root")
    }
}