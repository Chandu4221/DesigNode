package io.github.chandu4221.designode.domain.model

data class Screen(
    val id: ScreenId,
    val name: String,
    val root: AtomicNode,
)