package io.github.chandu4221.designode.domain.event

import io.github.chandu4221.designode.domain.model.Screen
import io.github.chandu4221.designode.domain.model.ScreenId

sealed interface ProjectEvent {

    data class ScreenAdded(
        val screen: Screen,
        val index: Int,
    ) : ProjectEvent

    data class ScreenRemoved(
        val screen: Screen,          // full snapshot, for undo
        val index: Int,
    ) : ProjectEvent

    data class ScreenRenamed(
        val screenId: ScreenId,
        val oldName: String,
        val newName: String,
    ) : ProjectEvent

    data class StartScreenChanged(
        val oldScreenId: ScreenId?,
        val newScreenId: ScreenId?,
    ) : ProjectEvent
}