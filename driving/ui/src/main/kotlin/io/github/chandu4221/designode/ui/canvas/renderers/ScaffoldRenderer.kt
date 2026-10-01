package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.ui.canvas.PreviewRenderer

private val TopAppBarType = setOf(ComponentTypeId("TopAppBar"))
private val NavigationBarType = setOf(ComponentTypeId("NavigationBar"))
private val FabTypes = setOf(
    ComponentTypeId("FloatingActionButton"),
    ComponentTypeId("ExtendedFloatingActionButton"),
)

private val ScaffoldPreview: PreviewRenderer = { node, slots ->
    val topBar: @androidx.compose.runtime.Composable () -> Unit = {
        if (node.hasChildren("topBar")) {
            slots(SlotId("topBar"))
        } else {
            ScaffoldSlotPlaceholder(
                nodeId = node.id,
                slotId = SlotId("topBar"),
                label = "Top bar",
                acceptedTypes = TopAppBarType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }

    val bottomBar: @androidx.compose.runtime.Composable () -> Unit = {
        if (node.hasChildren("bottomBar")) {
            slots(SlotId("bottomBar"))
        } else {
            ScaffoldSlotPlaceholder(
                nodeId = node.id,
                slotId = SlotId("bottomBar"),
                label = "Bottom bar",
                acceptedTypes = NavigationBarType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }

    val fab: @androidx.compose.runtime.Composable () -> Unit = {
        if (node.hasChildren("floatingActionButton")) {
            slots(SlotId("floatingActionButton"))
        } else {
            ScaffoldSlotPlaceholder(
                nodeId = node.id,
                slotId = SlotId("floatingActionButton"),
                label = "FAB",
                acceptedTypes = FabTypes,
                cornerRadius = 16.dp,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp),
            )
        }
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