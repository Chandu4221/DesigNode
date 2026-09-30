package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.codegen.emitters.BoxEmitter
import io.github.chandu4221.designode.codegen.emitters.ButtonEmitter
import io.github.chandu4221.designode.codegen.emitters.CardEmitter
import io.github.chandu4221.designode.codegen.emitters.ColumnEmitter
import io.github.chandu4221.designode.codegen.emitters.ExtendedFloatingActionButtonEmitter
import io.github.chandu4221.designode.codegen.emitters.FloatingActionButtonEmitter
import io.github.chandu4221.designode.codegen.emitters.IconEmitter
import io.github.chandu4221.designode.codegen.emitters.NavigationBarEmitter
import io.github.chandu4221.designode.codegen.emitters.NavigationBarItemEmitter
import io.github.chandu4221.designode.codegen.emitters.RowEmitter
import io.github.chandu4221.designode.codegen.emitters.ScaffoldEmitter
import io.github.chandu4221.designode.codegen.emitters.SpacerEmitter
import io.github.chandu4221.designode.codegen.emitters.TextEmitter
import io.github.chandu4221.designode.codegen.emitters.TopAppBarEmitter
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.port.CodeEmitter
import io.github.chandu4221.designode.domain.service.CodeGenerator

/**
 * All emitters for the Material 3 catalog, keyed by [ComponentTypeId].
 *
 * The resulting map is what [CodeGenerator] needs to walk a tree and produce
 * Kotlin source. Every component type in [io.github.chandu4221.designode.catalog.Material3Catalog]
 * must have an entry here, or code generation will fail on that type.
 */
object EmitterRegistry {

    val emitters: Map<ComponentTypeId, CodeEmitter> = mapOf(
        // Atoms
        ComponentTypeId("Text") to TextEmitter(),
        ComponentTypeId("Icon") to IconEmitter(),
        ComponentTypeId("Spacer") to SpacerEmitter(),

        // Layout
        ComponentTypeId("Row") to RowEmitter(),
        ComponentTypeId("Column") to ColumnEmitter(),
        ComponentTypeId("Box") to BoxEmitter(),
        ComponentTypeId("Scaffold") to ScaffoldEmitter(),

        // Containment
        ComponentTypeId("Card") to CardEmitter(),

        // Actions
        ComponentTypeId("Button") to ButtonEmitter(),
        ComponentTypeId("FloatingActionButton") to FloatingActionButtonEmitter(),
        ComponentTypeId("ExtendedFloatingActionButton") to ExtendedFloatingActionButtonEmitter(),

        // Bars
        ComponentTypeId("TopAppBar") to TopAppBarEmitter(),
        ComponentTypeId("NavigationBar") to NavigationBarEmitter(),
        ComponentTypeId("NavigationBarItem") to NavigationBarItemEmitter(),
    )

    fun codeGenerator(): CodeGenerator = CodeGenerator(emitters)
}