package io.github.chandu4221.designode.domain.model

sealed interface SlotContent {
    data object Empty : SlotContent
        data class One(val node: AtomicNode) : SlotContent
    data class Many(val nodes: List<AtomicNode>) : SlotContent
}