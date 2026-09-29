package io.github.chandu4221.designode.domain.model

fun SlotContent.nodes(): List<AtomicNode> = when (this) {
    SlotContent.Empty -> emptyList()
    is SlotContent.One -> listOf(node)
    is SlotContent.Many -> nodes
}

fun SlotContent.map(transform: (AtomicNode) -> AtomicNode): SlotContent =
    SlotContent.of(nodes().map(transform))