package io.github.chandu4221.designode.catalog

import io.github.chandu4221.designode.catalog.specs.*
import io.github.chandu4221.designode.domain.port.ComponentRegistry
import io.github.chandu4221.designode.domain.service.InMemoryComponentRegistry

/**
 * The DesigNode Material 3 catalog.
 *
 * Every spec registered here must reference only component types that are
 * also registered here. Cross-references are enforced by [CatalogIntegrityTest].
 */
object Material3Catalog {

    val specs = listOf(
        // Atoms
        TextSpec,
        IconSpec,
        SpacerSpec,

        // Layout
        RowSpec,
        ColumnSpec,
        BoxSpec,
        ScaffoldSpec,

        // Containment
        CardSpec,

        // Actions
        ButtonSpec,
        FloatingActionButtonSpec,
        ExtendedFloatingActionButtonSpec,

        // Bars
        TopAppBarSpec,
        NavigationBarSpec,
        NavigationBarItemSpec,
    )

    fun registry(): ComponentRegistry = InMemoryComponentRegistry(specs)
}