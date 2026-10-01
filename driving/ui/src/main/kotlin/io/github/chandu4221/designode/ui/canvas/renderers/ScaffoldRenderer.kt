package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.ui.canvas.PreviewRenderer

private val ScaffoldPreview: PreviewRenderer = { node, slots ->
    val topBar: @androidx.compose.runtime.Composable () -> Unit = {
        if (node.hasChildren("topBar")) slots(SlotId("topBar"))
    }
    val bottomBar: @androidx.compose.runtime.Composable () -> Unit = {
        if (node.hasChildren("bottomBar")) slots(SlotId("bottomBar"))
    }
    val fab: @androidx.compose.runtime.Composable () -> Unit = {
        if (node.hasChildren("floatingActionButton")) slots(SlotId("floatingActionButton"))
    }
    val snackbarHost: @androidx.compose.runtime.Composable () -> Unit = {
        if (node.hasChildren("snackbarHost")) slots(SlotId("snackbarHost"))
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = fab,
        snackbarHost = snackbarHost,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            slots(SlotId("content"))
        }
    }
}

fun scaffoldRenderers(): Map<ComponentTypeId, PreviewRenderer> = mapOf(
    ComponentTypeId("Scaffold") to ScaffoldPreview,
)