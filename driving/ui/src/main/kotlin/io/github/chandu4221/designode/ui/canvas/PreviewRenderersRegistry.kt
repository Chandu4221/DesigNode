package io.github.chandu4221.designode.ui.canvas

import io.github.chandu4221.designode.ui.canvas.renderers.atomRenderers
import io.github.chandu4221.designode.ui.canvas.renderers.barRenderers
import io.github.chandu4221.designode.ui.canvas.renderers.containerRenderers
import io.github.chandu4221.designode.ui.canvas.renderers.layoutRenderers
import io.github.chandu4221.designode.ui.canvas.renderers.scaffoldRenderers

/**
 * Assembles the full preview renderer map. Every component type in
 * [io.github.chandu4221.designode.catalog.Material3Catalog] must have an
 * entry here, or the canvas shows a placeholder for that type.
 */
object PreviewRenderersRegistry {

    fun build(): PreviewRenderers = PreviewRenderers(
        atomRenderers() +
                layoutRenderers() +
                containerRenderers() +
                barRenderers() +
                scaffoldRenderers()
    )
}