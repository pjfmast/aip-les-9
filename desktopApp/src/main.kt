package com.example.aip_les_9

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "aip-les-9") {
        App()
    }
}
