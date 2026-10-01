package io.github.chandu4221.designode.ui.canvas

import androidx.compose.runtime.compositionLocalOf
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.ui.DropTarget

val LocalHitTestRegistry = compositionLocalOf<HitTestRegistry?> { null }
val LocalDropTarget = compositionLocalOf<DropTarget?> { null }
val LocalDragType = compositionLocalOf<ComponentTypeId?> { null }
