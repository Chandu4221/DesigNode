package io.github.chandu4221.designode.domain.model

enum class LayoutScope {
    Unscoped,           // no scoped modifiers apply
    RowScope,       // weight, align (horizontal)
    ColumnScope,    // weight, align (vertical)
    BoxScope,       // align, matchParentSize
    FlowRowScope,   // Compose Flow layouts
    FlowColumnScope,
}