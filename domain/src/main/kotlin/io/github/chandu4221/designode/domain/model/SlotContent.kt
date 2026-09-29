package io.github.chandu4221.designode.domain.model

sealed interface SlotContent {
    data object Empty : SlotContent
    data class One(val node: AtomicNode) : SlotContent
    data class Many(val nodes: List<AtomicNode>) : SlotContent

    companion object {
        fun of(nodes: List<AtomicNode>): SlotContent = when (nodes.size) {
            0 -> Empty
            1 -> One(nodes[0])
            else -> Many(nodes)
        }
    }
}